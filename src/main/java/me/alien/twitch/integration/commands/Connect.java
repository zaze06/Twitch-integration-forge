package me.alien.twitch.integration.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.User;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alien.twitch.integration.TwitchListener;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;

import java.util.Arrays;

import static me.alien.twitch.integration.Listener.twitchClient;
import static me.alien.twitch.integration.Main.*;

public class Connect {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("connect").requires((command) -> {
            return command.hasPermission(Commands.LEVEL_ALL);
        }).then(Commands.argument("TwitchUser", MessageArgument.message()).executes((command) -> execute(command))));
    }

    private static int execute(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player p){
            try {
                if(command.getSource().getEntity() instanceof ServerPlayer && chat == null) {
                    String user = MessageArgument.getMessage(command, "TwitchUser").getString();
                    User twitchUser = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, Arrays.asList(chat)).execute().getUsers().get(0);
                    twitchClient.getChat().joinChannel(user);
                    chat = user;
                    channelId = twitchUser.getId();
                    command.getSource().getEntity().sendMessage(new TextComponent("<a_twitch_bot_> Connected to chat " + chat), Util.NIL_UUID);
                    twitchClient.getChat().sendMessage(chat, "I was summoned hear by " + command.getSource().getEntity().getDisplayName().getString());
                    twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, TwitchListener::onChatMessage);
                }
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
