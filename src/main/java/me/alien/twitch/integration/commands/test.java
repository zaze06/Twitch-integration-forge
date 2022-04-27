package me.alien.twitch.integration.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.common.lib.radiation.RadiationManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.LivingEntity;

public class test {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("test").executes(context -> {
            if(context.getSource().getEntity() instanceof LivingEntity e) {
                MekanismAPI.getRadiationManager().radiate(new Coord4D(e), 4);
            }
            return Command.SINGLE_SUCCESS;
        }));
    }
}
