package me.alien.twitch.integration.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;

import static me.alien.twitch.integration.Listener.twitchClient;
import static me.alien.twitch.integration.Main.LOGGER;
import static me.alien.twitch.integration.Main.chat;

public class Connect {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("connect").requires((command) -> {
            return command.hasPermission(1);
        }).then(Commands.argument("TwitchUser", MessageArgument.message()).executes((command) -> {
            return execute(command);
        })));
    }

    private static int execute(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player p){
            try {
                String user = MessageArgument.getMessage(command, "TwitchUser").getString();
                twitchClient.getChat().joinChannel(user);
                chat = user;
                command.getSource().getEntity().sendMessage(new TextComponent("<a_twitch_bot_> Connected to chat "+chat), Util.NIL_UUID);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
