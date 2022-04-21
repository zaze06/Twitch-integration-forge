package me.alien.twitch.integration;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;

import java.util.ArrayList;

import static me.alien.twitch.integration.Listener.twitchClient;
import static me.alien.twitch.integration.Main.chat;

public class TwitchListener {
    public static void onChatMessage(ChannelMessageEvent event){
        String command = event.getMessage();
        String[] args = new String[0];

        if(twitchClient != null && chat != null) return;

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
        }else{
            if(player != null) {
                if (player.getServer() !=null){
                    player.getServer().sendMessage(new TextComponent("<" + user.getName() + "> " + event.getMessage()), Util.NIL_UUID);
                }
            }
        }
    }
}
