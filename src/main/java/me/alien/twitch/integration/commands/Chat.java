package me.alien.twitch.integration.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static me.alien.twitch.integration.Listener.twitchClient;
import static me.alien.twitch.integration.Main.chat;
import static me.alien.twitch.integration.Main.twitchChat;

public class Chat {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("chat").requires(sender -> {
            return sender.hasPermission(Commands.LEVEL_ALL);
        }).then(Commands.literal("twitch").executes((command) -> {
            twitchChat = !twitchChat;
            return Command.SINGLE_SUCCESS;
        })));
    }
}
