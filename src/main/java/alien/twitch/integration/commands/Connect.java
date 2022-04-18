package alien.twitch.integration.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class Connect {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("connect").then(Commands.literal("twitchUser").executes((command) -> {
            return execute(command);
        })));
    }

    private static int execute(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player p){
            //command.getArgument()
        }
        return Command.SINGLE_SUCCESS;
    }
}
