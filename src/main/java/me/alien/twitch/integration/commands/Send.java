package me.alien.twitch.integration.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.server.level.ServerPlayer;

import static me.alien.twitch.integration.Listener.twitchClient;
import static me.alien.twitch.integration.Main.chat;

public class Send {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("send").requires((command) -> {
            return command.hasPermission(Commands.LEVEL_ALL);
        }).then(Commands.argument("message", MessageArgument.message()).executes((command) -> {
            if(command.getSource().getEntity() instanceof ServerPlayer) {
                if (chat != null) {
                    twitchClient.getChat().sendMessage(chat, "<" + command.getSource().getEntity().getDisplayName().getString() + "> " + MessageArgument.getMessage(command, "message").getString());
                }
            }
            return Command.SINGLE_SUCCESS;
        })));
    }


}
