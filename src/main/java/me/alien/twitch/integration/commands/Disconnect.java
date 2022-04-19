package me.alien.twitch.integration.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;

import static me.alien.twitch.integration.Listener.twitchClient;

public class Disconnect {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("connect").executes((command) -> {
            twitchClient.getChat().disconnect();
            return Command.SINGLE_SUCCESS;
        }));
    }
}
