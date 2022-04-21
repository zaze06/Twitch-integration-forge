package me.alien.twitch.integration.commands;

import com.github.twitch4j.helix.domain.User;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alien.twitch.integration.util.Pair;
import me.alien.twitch.integration.util.ValueComparator;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import static me.alien.twitch.integration.Listener.twitchClient;
import static me.alien.twitch.integration.Main.credential;
import static me.alien.twitch.integration.Main.viewerPoints;

public class Points {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("points").executes((command) -> {
            TreeMap<String, Integer> viewerPointsSorted = new TreeMap<>(new ValueComparator(viewerPoints));
            viewerPointsSorted.putAll(viewerPoints);
            ArrayList<Pair<String, Integer>> topViewerPoints = new ArrayList<>();
            int i = 0;
            for(String key : viewerPointsSorted.keySet()){
                if(i > 9) break;
                User user = twitchClient.getHelix().getUsers(credential.getAccessToken(), Arrays.asList(key), null).execute().getUsers().get(0);
                if(user == null) break;
                topViewerPoints.add(i, new Pair<>(user.getDisplayName(), viewerPoints.get(key)));
            }
            StringBuilder str = new StringBuilder();
            if(topViewerPoints.size() > 0) {
                str.append("Top ").append(topViewerPoints.size()).append(" soul of the lost user").append((topViewerPoints.size()>1?"s":""));
                for(i = topViewerPoints.size()-1; i >= 0; i--){
                    Pair<String, Integer> user = topViewerPoints.get(i);
                    str.append("\n").append(user.getKey()).append(" have ").append((user.getValue()==null?"null":user.getValue())).append(" Soul").append(((user.getValue()==null?0:user.getValue())>1?"s":"")).append(" of the lost");
                }
            }
            if(command.getSource().getEntity() instanceof ServerPlayer) {
                command.getSource().getEntity().sendMessage(new TextComponent("<a_twitch_bot_> " + str.toString()), Util.NIL_UUID);
            }
            return Command.SINGLE_SUCCESS;
        }));
    }
}
