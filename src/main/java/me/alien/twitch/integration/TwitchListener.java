package me.alien.twitch.integration;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import me.alien.twitch.integration.util.Action;
import me.alien.twitch.integration.util.Factorys;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.alien.twitch.integration.Listener.*;
import static me.alien.twitch.integration.Main.*;

public class TwitchListener {
    public static void onChatMessage(ChannelMessageEvent event){
        String command = event.getMessage();
        String[] args = new String[0];

        if(twitchClient == null || chat == null) return;

        try{
            command = event.getMessage().substring(0, event.getMessage().indexOf(' '));
        }catch (Exception ignored){

        }
        try {
            args = event.getMessage().substring(event.getMessage().indexOf(' ')).split(" ");
            ArrayList<String> tmp = new ArrayList<>();
            for(int i = 1; i < args.length; i++){
                tmp.add(args[i]);
            }
            if(tmp.size() > 0) {
                args = tmp.toArray(new String[0]);
            }
        }catch (Exception ignored){}

        EventUser user = event.getUser();
        Player player = Minecraft.getInstance().player;

        if(command.equalsIgnoreCase("-test")){
            twitchClient.getChat().sendMessage(chat, "Yes i work @"+user.getName());
        }
        else if(command.equalsIgnoreCase("-source")){
            twitchClient.getChat().sendMessage(chat, "I'm a bot made by @AlienFromDia and my source code is located at https://github.com/zaze06/Twitch");
        }
        else if(command.equalsIgnoreCase("-points")){
            if(args.length > 0){
                User user1 = null;
                String uname = args[0];
                try{
                    uname = args[0].split("@")[1];
                }catch (Exception ignored){}
                try{

                    user1 = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, Collections.singletonList(uname)).execute().getUsers().get(0);
                }catch (Exception e){LOGGER.warn(e.getCause().toString());}
                if(user1 != null) {
                    twitchClient.getChat().sendMessage(chat, user1.getDisplayName() + " currently have " + viewerPoints.get(user1.getId()) + " Soul of the lost");
                }else{
                    /*List<User> users = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, Collections.singletonList(uname)).execute().getUsers();
                    StringBuilder data = new StringBuilder();
                    for(User user1 : users){
                        data.append(user1.getDisplayName());
                    }
                    getServer().getLogger().info(data.toString());*/
                    twitchClient.getChat().sendMessage(chat, "Sorry @"+ user.getName()+" i cant find "+uname+" :(");
                }
            }else {
                Integer points = viewerPoints.get(user.getId());
                twitchClient.getChat().sendMessage(chat, "@" + user.getName() + " you currently have " + (points!=null?points:0) + " Soul of the lost");
            }
        }
        else if(command.equalsIgnoreCase("-products")){
            twitchClient.getChat().sendMessage(chat, "@"+ user.getName()+" you have the production list hear https://docs.google.com/spreadsheets/d/1irumyoV5YYpgm9GQeObTmKXLtBU5vXMnBXgybnwufeI/ you may need to change to the products page(bottom of the screen)");
        }
        else if(command.startsWith("-buy")){
            if(args.length > 0){
                if(chat == null){
                    return;
                }

                Integer points = viewerPoints.get(user.getId());
                boolean removedPoints = false;

                String redemption = args[0];
                if (redemption.equalsIgnoreCase("0") || redemption.equalsIgnoreCase("heal")) {
                    if (points >= 100 && !timersNotFinished[0]) {
                        ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
                                for(Player p : players) {
                                    p.setHealth(p.getMaxHealth());
                                }
                            }
                        });
                        if( !removedPoints ) {
                            points -= 100;
                            removedPoints = true;
                        }
                        timersDelay[0] = 3*60;
                    } else if(timersNotFinished[0]) {
                        twitchClient.getChat().sendMessage(chat, "Sorry "+ user.getName()+" it's "+ timersDelay[0]+" sec left");
                    }  else {
                        twitchClient.getChat().sendMessage(chat, user.getName() + " you don't have enough soul of the lost");
                    }
                }
                else if (redemption.equalsIgnoreCase("1") || redemption.equalsIgnoreCase("feed")) {
                    if (points >= 100 && !timersNotFinished[1]) {
                        ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
                                for(Player p : players) {
                                    p.getFoodData().setFoodLevel(20);
                                }
                            }
                        });
                        if( !removedPoints ) {
                            points -= 100;
                            removedPoints = true;
                        }
                        timersDelay[1] = 3*60;
                    } else if(timersNotFinished[1]) {
                        twitchClient.getChat().sendMessage(chat, "Sorry "+ user.getName()+" it's "+ timersDelay[1]+" sec left");
                    }  else {
                        twitchClient.getChat().sendMessage(chat, user.getName() + " you don't have enough soul of the lost");
                    }
                }
                else if (redemption.equalsIgnoreCase("2") || redemption.equalsIgnoreCase("grace")) {
                    if (points >= 500 && !timersNotFinished[2]) {
                        grace = true;
                        graceTime = 60;
                        graceTimeOrig = graceTime;
                        points -= 500;
                        timersDelay[2] = 2*60;
                    } else if(timersNotFinished[2]) {
                        twitchClient.getChat().sendMessage(chat, "Sorry "+ user.getName()+" it's "+ timersDelay[2]+" sec left");
                    }  else {
                        twitchClient.getChat().sendMessage(chat, user.getName() + " you don't have enough soul of the lost");
                    }
                }
                else if (redemption.equalsIgnoreCase("3") || redemption.equalsIgnoreCase("teleport") && !grace) {
                    if (points >= 500 && !timersNotFinished[3]) {
                        ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
                                for(Player p : players) {
                                    BlockPos loc = p.getOnPos();
                                    int x = (int) (Math.random() * ((loc.getX() + 1000) - (loc.getX() - 1000)) + (loc.getX() - 1000)), y = (int) (Math.random() * ((loc.getY() + 1000) - (loc.getY() - 1000)) + (loc.getY() - 1000)), z = (int) (Math.random() * ((loc.getZ() + 1000) - (loc.getZ() - 1000)) + (loc.getZ() - 1000));
                                    for (int i = 0; i < 200; i++) {
                                        if (player.getLevel().getBlockState(new BlockPos(x, y, z)).isAir() && player.getLevel().getBlockState(new BlockPos(x, y + 1, z)).isAir() && y > -60 && y < 360)
                                            break;
                                        x = (int) (Math.random() * ((loc.getX() + 1000) - (loc.getX() - 1000)) + (loc.getX() - 1000));
                                        y = (int) (Math.random() * ((loc.getY() + 1000) - (loc.getY() - 1000)) + (loc.getY() - 1000));
                                        z = (int) (Math.random() * ((loc.getZ() + 1000) - (loc.getZ() - 1000)) + (loc.getZ() - 1000));
                                    }
                                    int finalX = x;
                                    int finalY = y;
                                    int finalZ = z;
                                    if (p.getLevel().getBlockState(new BlockPos(finalX, finalY, finalZ)).isAir() && player.getLevel().getBlockState(new BlockPos(finalX, finalY + 1, finalZ)).isAir()) {
                                        p.teleportTo(finalX,finalY,finalZ);
                                    }
                                }
                            }
                        });
                        if( !removedPoints ) {
                            points -= 500;
                            removedPoints = true;
                        }
                        timersDelay[3] = 4*60;
                    } else if(timersNotFinished[3]) {
                        twitchClient.getChat().sendMessage(chat, "Sorry "+ user.getName()+" it's "+ timersDelay[3]+" sec left");
                    } else {
                        twitchClient.getChat().sendMessage(chat, user.getName() + " you don't have enough soul of the lost");
                    }
                }
                else if (redemption.equalsIgnoreCase("4") || redemption.equalsIgnoreCase("hydrate")) {
                    if(points >= 300 && !timersNotFinished[4]){
                        twitchClient.getEventManager().publish(new RewardRedeemedEvent(Instant.now(), Factorys.redemptionFactory(user, redemptions.getString("Hydrate"))));
                        if( !removedPoints ) {
                            points -= 300;
                            removedPoints = true;
                        }
                    } else if(timersNotFinished[4]) {
                        twitchClient.getChat().sendMessage(chat, "Sorry "+ user.getName()+" it's "+ timersDelay[4]+" sec left");
                    }  else {
                        twitchClient.getChat().sendMessage(chat, user.getName() + " you don't have enough soul of the lost");
                    }
                }
                else if (redemption.equalsIgnoreCase("5") || redemption.equalsIgnoreCase("hiss")){
                    if(points >= 50 && !timersNotFinished[5]){
                        twitchClient.getEventManager().publish(new RewardRedeemedEvent(Instant.now(), Factorys.redemptionFactory(user, redemptions.getString("hiss"))));
                        if( !removedPoints ) {
                            points -= 50;
                            removedPoints = true;
                        }
                    } else if(timersNotFinished[5]) {
                        twitchClient.getChat().sendMessage(chat, "Sorry "+ user.getName()+" it's "+ timersDelay[5]+" sec left");
                    }  else {
                        twitchClient.getChat().sendMessage(chat, user.getName() + " you don't have enough soul of the lost");
                    }
                }
                else if (redemption.equalsIgnoreCase("6") || redemption.equalsIgnoreCase("nut")){
                    if(points >= 50 && !timersNotFinished[6]){
                        twitchClient.getEventManager().publish(new RewardRedeemedEvent(Instant.now(), Factorys.redemptionFactory(user, redemptions.getString("nut"))));
                        if( !removedPoints ) {
                            points -= 50;
                            removedPoints = true;
                        }
                    } else if(timersNotFinished[6]) {
                        twitchClient.getChat().sendMessage(chat, "Sorry "+ user.getName()+" it's "+ timersDelay[6]+" sec left");
                    }  else {
                        twitchClient.getChat().sendMessage(chat, user.getName() + " you don't have enough soul of the lost");
                    }
                }
                else if (redemption.equalsIgnoreCase("7") || redemption.equalsIgnoreCase("drop-it")){
                    if(points >= 100 && !timersNotFinished[7]){
                        twitchClient.getEventManager().publish(new RewardRedeemedEvent(Instant.now(), Factorys.redemptionFactory(user, redemptions.getString("Drop it"))));
                        if( !removedPoints ) {
                            points -= 100;
                            removedPoints = true;
                        }
                    } else if(timersNotFinished[7]) {
                        twitchClient.getChat().sendMessage(chat, "Sorry "+ user.getName()+" it's "+ timersDelay[7]+" sec left");
                    }  else {
                        twitchClient.getChat().sendMessage(chat, user.getName() + " you don't have enough soul of the lost");
                    }
                }
                else if (redemption.equalsIgnoreCase("8") || redemption.equalsIgnoreCase("mission-failed")){
                    if(points >= 300 && !timersNotFinished[8]){
                        twitchClient.getEventManager().publish(new RewardRedeemedEvent(Instant.now(), Factorys.redemptionFactory(user, redemptions.getString("Mission Failed"))));
                        if( !removedPoints ) {
                            points -= 300;
                            removedPoints = true;
                        }
                    } else if(timersNotFinished[8]) {
                        twitchClient.getChat().sendMessage(chat, "Sorry "+ user.getName()+" it's "+ timersDelay[8]+" sec left");
                    }  else {
                        twitchClient.getChat().sendMessage(chat, user.getName() + " you don't have enough soul of the lost");
                    }
                }

                for(int i = 0; i < timersDelay.length; i++){
                    if(!timersNotFinished[i] && timersDelay[i] > 0){
                        timersNotFinished[i] = true;
                    }
                }

                viewerPoints.put(user.getId(), points);
            }
        }
        else{
            if(twitchChat) {
                ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                    @Override
                    public void run() {
                        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                        server.sendMessage(new TextComponent("<"+user.getName()+"> "+event.getMessage()), Util.NIL_UUID);
                    }
                });
            }
        }
    }

    public static void onRedeemed(RewardRedeemedEvent e){
        Redemption r = new Redemption(e, null);
        r.start();
    }
}
