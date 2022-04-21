package me.alien.twitch.integration.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;

import static me.alien.twitch.integration.Listener.twitchClient;
import static me.alien.twitch.integration.Main.chat;

public class Disconnect {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("disconnect").executes((command) -> {
            twitchClient.getChat().sendMessage(chat, "I was instructed to self destruct");
            twitchClient.getChat().disconnect();
            chat = null;
            return Command.SINGLE_SUCCESS;
        }));
    }
}
