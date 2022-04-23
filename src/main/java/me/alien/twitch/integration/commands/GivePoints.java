package me.alien.twitch.integration.commands;

import com.github.twitch4j.helix.domain.User;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;

import static me.alien.twitch.integration.Listener.twitchClient;
import static me.alien.twitch.integration.Main.*;

public class GivePoints {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("givePoints").requires((command) -> {
            return command.hasPermission(Commands.LEVEL_ALL);
        }).then(Commands.argument("TwitchUser", MessageArgument.message()).then(Commands.argument("points", IntegerArgumentType.integer())).executes((command) -> {
            String cName = MessageArgument.getMessage(command, "message").getString();
            User user = null;
            try {
                user = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, Collections.singletonList(cName)).execute().getUsers().get(0);
                //LOGGER.info("found user " + user.getDisplayName());
            } catch (Exception ignore) {
            }

            if(user == null){
                command.getSource().sendFailure(new TextComponent("Can't find user "+cName));
            }else{
                Integer uPoits = viewerPoints.get(user.getId());
                int points = IntegerArgumentType.getInteger(command, "points");
                viewerPoints.put(user.getId(), (uPoits == null?0:uPoits)+points);
            }
            return Command.SINGLE_SUCCESS;
        })));
    }
}
