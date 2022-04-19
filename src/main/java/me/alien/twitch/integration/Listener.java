package me.alien.twitch.integration;

import me.alien.twitch.integration.commands.Connect;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import me.alien.twitch.integration.commands.Disconnect;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static me.alien.twitch.integration.Main.*;

@Mod.EventBusSubscriber
public class Listener {
    private final Main main;
    public static TwitchClient twitchClient;

    public Listener(Main main) {
        this.main = main;
    }

    @SubscribeEvent
    public void onServerStart(ServerStartingEvent e){
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String connection = "jdbc:mariadb://"+main.mysql.getString("ip")+"/"+main.mysql.getString("database")+"?user="+main.mysql.getString("username")+"&password="+main.mysql.getString("password");
            LOGGER.info(connection);
            //conn = DriverManager.getConnection(connection );
            String con2 = "jdbc:mariadb://"+main.mysql.getString("ip")+"/"+main.mysql.getString("database");
            main.conn = DriverManager.getConnection(con2, "twitch_bot", "Twitch_bot");
        } catch (Exception ex) {LOGGER.warn("Cant connect to sql server, defaulting to json backup may be out of date");
            try {
                JSONObject points = new JSONObject(Loader.leadFile(new FileInputStream(System.getProperty("user.dir")+"/data/backup.json")));
                for(String key : points.keySet()){
                    main.viewerPoints.put(key, points.getInt(key));
                }
            } catch (FileNotFoundException e1) {LOGGER.warn("Cant find file "+System.getProperty("user.dir")+"/data/backup.json"+" this file be be created");
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

        if(main.conn != null) {
            try {
                Statement stmt = main.conn.createStatement();

                ResultSet rs = stmt.executeQuery("select * from mcpoits.aniki");

                for (int row = 1; row <= rs.getRow(); row++) {
                    rs.absolute(row);
                    main.viewerPoints.put(rs.getString("id"), rs.getInt("points"));
                }
            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }

        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withEnablePubSub(true)
                .withEnableHelix(true)
                .withEnableTMI(true)
                .withChatAccount(main.credential)
                .withCredentialManager(main.credentialManager)
                .build();
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent e){
        twitchClient.getChat().disconnect();
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent e){
        Disconnect.register(e.getDispatcher());
        Connect.register(e.getDispatcher());
    }
}
