package me.alien.twitch.integration;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.User;
import me.alien.twitch.integration.commands.*;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import static me.alien.twitch.integration.Main.*;

@Mod.EventBusSubscriber
public class Listener {
    private final Main main;
    public static TwitchClient twitchClient;

    public Listener(Main main) {
        this.main = main;
    }
    public static Timer pointAccumulation;
    public static Timer looper;
    public static final boolean[] timersNotFinished = new boolean[12];
    public static final int[] timersDelay = new int[12];
    public Timer timers;
    public Timer timer;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onServerStart(ServerStartingEvent e){
        try {
            int i = 0;
            Class.forName("org.mariadb.jdbc.Driver");
            String connection = "jdbc:mariadb://"+main.mysql.getString("ip")+"/"+main.mysql.getString("database")+"?user="+main.mysql.getString("username")+"&password="+main.mysql.getString("password");
            LOGGER.info(connection);
            //conn = DriverManager.getConnection(connection );
            String con2 = "jdbc:mariadb://"+main.mysql.getString("ip")+"/"+main.mysql.getString("database");
            conn = DriverManager.getConnection(connection, "twitch_bot", "Twitch_bot");
        } catch (Exception ex) {
            LOGGER.warn("Cant connect to sql server, defaulting to json backup may be out of date");
            try {
                JSONObject points = new JSONObject(Loader.leadFile(new FileInputStream(System.getProperty("user.dir")+"/data/backup.json")));
                for(String key : points.keySet()){
                    viewerPoints.put(key, points.getInt(key));
                }
            } catch (FileNotFoundException e1) {
                LOGGER.warn("Cant find file \""+System.getProperty("user.dir")+"/twitchIntegration/data/backup.json\""+" this file be be created");
                File file = new File(System.getProperty("user.dir")+"/twitchIntegration/data/backup.json");
                try {
                    new File(file.getParent()).mkdirs();
                    file.createNewFile();
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
            //ex.printStackTrace();
        }

        if(conn != null) {
            try {
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("select * from mcpoits.aniki");

                for (int row = 1; row <= rs.getRow(); row++) {
                    rs.absolute(row);
                    viewerPoints.put(rs.getString("id"), rs.getInt("points"));
                }
            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }



        pointAccumulation = new Timer(5*1000*60, e1 -> {
            if(twitchClient != null) {
                List<User> users = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, twitchClient.getMessagingInterface().getChatters(chat).execute().getAllViewers()).execute().getUsers();
                for (User key : users) {
                    synchronized (viewerPoints) {
                        Integer points = viewerPoints.get(key.getId());
                        points = (points == null ? 0 : points);
                        viewerPoints.put(key.getId(), points + 50);
                       LOGGER.info("add " + 50 + " to " + key.getDisplayName() + " now has " + (points + 50));
                    }
                }
            }
        });

        pointAccumulation.start();

        looper = new Timer(20*1000*60, e1 -> {
            if(conn != null) {
                StringBuilder data = new StringBuilder("insert into aniki(id, points) values ");
                Set<String> set = viewerPoints.keySet();
                int i = 0;
                for (String key : set) {

                    data.append("(").append(key).append(", ").append(viewerPoints.get(key)).append(")").append((i < set.size() ? ", " : ";"));
                    i++;
                }
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(data.toString());
                } catch (SQLException ex) {
                    // handle any errors
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                }
            }else{
                JSONObject points = new JSONObject();
                for(String key : viewerPoints.keySet()){
                    points.put(key, viewerPoints.get(key));
                }
                File file = new File(System.getProperty("user.dir")+"/data/backup.json");
                try{
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    BufferedWriter out = new BufferedWriter(new FileWriter(file));
                    out.write(points.toString(4));
                    out.flush();
                }catch (Exception ignored){}
            }
        });

        looper.start();

        timers = new Timer(1000, e1 -> {
            for(int i = 0; i < timersDelay.length; i++){
                if(timersDelay[i] > 0){
                    timersDelay[i]--;
                } else if (timersNotFinished[i]) {
                    timersNotFinished[i] = false;
                }
            }
        });

        timers.start();

        timer = new Timer(1000, e1 -> {
            if(time > 0){
                time--;
            }else
            if(time == 0 && disableShit){
                disableShit = false;
            }

            if(graceTime > 0){
                graceTime--;
            }else
            if(graceTime == 0 && grace){
                grace = false;
                e.getServer().sendMessage(new TextComponent("<a_twitch_bot_> grace period is now over"), Util.NIL_UUID);
            }

            if(graceTime == graceTimeOrig/2 && grace){
                e.getServer().sendMessage(new TextComponent("<a_twitch_bot_> "+(graceTimeOrig/2/60)+" min left on grace period"), Util.NIL_UUID);
            }else if(graceTime == 10 && grace){
                e.getServer().sendMessage(new TextComponent("<a_twitch_bot_> 10 seconds left on grace period"), Util.NIL_UUID);
            }else if(graceTime == 5 && grace){
                e.getServer().sendMessage(new TextComponent("<a_twitch_bot_> 5 seconds left on grace period"), Util.NIL_UUID);
            }
        });

        timer.start();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onServerStop(ServerStoppingEvent e){
        twitchClient.getChat().sendMessage(chat, "I was instructed to self destruct");
        twitchClient.getChat().disconnect();
        chat = null;
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent e){
        Disconnect.register(e.getDispatcher());
        Connect.register(e.getDispatcher());
        Send.register(e.getDispatcher());
        Points.register(e.getDispatcher());
        Chat.register(e.getDispatcher());
    }
}
