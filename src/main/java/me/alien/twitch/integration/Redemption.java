package me.alien.twitch.integration;

import com.github.twitch4j.pubsub.domain.ChannelPointsUser;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;

import static me.alien.twitch.integration.Main.*;

public class Redemption extends Thread {

    RewardRedeemedEvent event;
    Main plugin;

    public Redemption(RewardRedeemedEvent event, Main plugin){
        this.event = event;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        String id = event.getRedemption().getReward().getId();
        long cost = event.getRedemption().getReward().getCost();
        String userName = event.getRedemption().getUser().getDisplayName();
        ChannelPointsUser user = event.getRedemption().getUser();

        if(chat == null){
            return;
        }
        int odds = (int) (Math.random() * 100);

        System.out.println(odds + "");

        if (id.equalsIgnoreCase(redemptions.getString("hiss")) && !grace) {
            ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                @Override
                public void run() {
                    for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                        if (odds <= Config.Client.chargedCreeperOdds.get()) {
                            Creeper creeper = new Creeper(EntityType.CREEPER, p.getLevel());
                            CompoundTag nbt = creeper.serializeNBT();
                            nbt.putBoolean("powered", true);
                            creeper.setTarget(p);
                            creeper.setPos(Vec3.atCenterOf(getPosBehind(p)));
                            //creeper.setBaby(true);
                            creeper.setCustomName(new TextComponent(userName));
                            p.getLevel().addFreshEntity(creeper);
                        }
                        else if (odds <= Config.Client.creeperOdds.get()) {
                            Creeper creeper = new Creeper(EntityType.CREEPER, p.getLevel());
                            CompoundTag nbt = creeper.serializeNBT();
                            nbt.putBoolean("powered", true);
                            creeper.setTarget(p);
                            creeper.setPos(Vec3.atCenterOf(getPosBehind(p)));
                            //creeper.setBaby(true);
                            creeper.setCustomName(new TextComponent(userName));
                            p.getLevel().addFreshEntity(creeper);
                        }
                    }
                }
            });
            //p.sendMessage(event.getRedemption().getUser().getDisplayName()+" redeemed hiss!");
        }
        else if (id.equalsIgnoreCase(redemptions.getString("balloonPop")) && !grace) {
            if (odds <= Config.Client.balloonPopOdds.get()) {
                ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                            BlockPos pos = p.getOnPos();
                            for (int x = -3; x < 4; x++) {
                                for (int y = -3; y < 4; y++) {
                                    for (int z = -3; z < 4; z++) {
                                        BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                                        p.getLevel().setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                                    }
                                }
                            }
                            int i = (int) (Math.random() * potionEffectTypes.size());
                            p.addEffect(new MobEffectInstance(potionEffectTypes.get(i), 40 * 20, 4));
                            i = (int) (Math.random() * potionEffectTypes.size());
                            p.addEffect(new MobEffectInstance(potionEffectTypes.get(i), 40 * 20, 4));
                            p.sendMessage(new TextComponent(event.getRedemption().getUser().getDisplayName() + " redeemed BalloonPop!"), Util.NIL_UUID);
                        }
                    }
                });
            }
        }
        else if (id.equalsIgnoreCase(redemptions.getString("knock")) && !grace) {
            if (odds <= Config.Client.knockKnockOdds.get()) {

                ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                            BlockPos pos = getPosBehind(p);

                            p.getLevel().setBlock(pos, Blocks.CRIMSON_DOOR.defaultBlockState(), 3);

                            Zombie zombie = new Zombie(p.getLevel());

                            zombie.setCustomName(new TextComponent(event.getRedemption().getUser().getDisplayName()));
                            zombie.setSilent(true);
                            if (odds <= Config.Client.knockKnockBabyOdds.get()) {
                                zombie.setBaby(true);
                            }
                            zombie.setPos(Vec3.atCenterOf(getPosBehind(p)));

                            p.getLevel().addFreshEntity(zombie);
                        }
                    }
                });
            }
        }
        else if (id.equalsIgnoreCase(redemptions.getString("nut")) && !grace) {
            if (odds <= Config.Client.nutOdds.get()){//getInt("NutOdds")) {
                //List of monsters to spawn
                ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {

                            int pilliger = ((int) (Math.random() * 2)) + 2;
                            int vindicators = ((int) (Math.random() * 2)) + 5;
                            int witchs = ((int) (Math.random() * 2)) + 1;
                            int evokers = 2;
                            int RavagerVindicator = ((int) (Math.random() * 1)) + 1;
                            int RavagerEvoker = 1;

                            int total = pilliger + vindicators + witchs + evokers + RavagerEvoker + RavagerVindicator;

                            BlockPos location = getPosBehind(p);
// redemtion nut, should spawn some monsters accoring to the list above
                            for (int i = 0; i < total; i++) {
                                int x = (int) (Math.random() * ((location.getX() + 30) - (location.getX() - 30)) + (location.getX() + 30));
                                int z = (int) (Math.random() * ((location.getZ() + 30) - (location.getZ() - 30)) + (location.getZ() + 30));
                                int y = p.getLevel().getMaxBuildHeight();

                                while (p.getLevel().getBlockState(new BlockPos(x, y, z)).isAir() && y != p.getLevel().getMinBuildHeight()) {
                                    y--;
                                }
                                if (y == p.getLevel().getMinBuildHeight()) {
                                    y = location.getY();
                                    for (int x1 = x - 1; x1 < x + 1; x1++) {
                                        for (int z1 = z - 1; z1 < z + 1; z1++) {
                                            BlockState blockAt = p.getLevel().getBlockState(new BlockPos(x1, y - 1, z1));
                                            if (blockAt.isAir()){
                                                p.getLevel().setBlock(new BlockPos(x1, y - 1, z1), Blocks.DIRT.defaultBlockState(), 3);
                                            }
                                        }
                                    }
                                }

                                if (pilliger > 0) {
                                    Pillager pillager = new Pillager(EntityType.PILLAGER, p.getLevel());
                                    pillager.setTarget(p);
                                    pillager.setSilent(true);
                                    pillager.setCustomName(new TextComponent(userName));
                                    pillager.setPos(x,y,z);
                                    p.getLevel().addFreshEntity(pillager);

                                    pilliger--;
                                } else if (vindicators > 0) {

                                    Vindicator vindicator = new Vindicator(EntityType.VINDICATOR, p.getLevel());
                                    vindicator.setTarget(p);
                                    vindicator.setSilent(true);
                                    vindicator.setCustomName(new TextComponent(userName));
                                    vindicator.setPos(x,y,z);
                                    p.getLevel().addFreshEntity(vindicator);

                                    vindicators--;
                                } else if (witchs > 0) {

                                    Witch witch = new Witch(EntityType.WITCH, p.getLevel());
                                    witch.setTarget(p);
                                    witch.setSilent(true);
                                    witch.setCustomName(new TextComponent(userName));
                                    witch.setPos(x,y,z);
                                    p.getLevel().addFreshEntity(witch);

                                    witchs--;
                                } else if (evokers > 0) {

                                    Evoker evoker = new Evoker(EntityType.EVOKER, p.getLevel());
                                    evoker.setTarget(p);
                                    evoker.setSilent(true);
                                    evoker.setCustomName(new TextComponent(userName));
                                    evoker.setPos(x,y,z);
                                    p.getLevel().addFreshEntity(evoker);

                                    evokers--;
                                } else if (RavagerEvoker > 0) {

                                    Evoker evoker = new Evoker(EntityType.EVOKER, p.getLevel());
                                    evoker.setTarget(p);
                                    evoker.setSilent(true);
                                    evoker.setCustomName(new TextComponent(userName));

                                    Ravager ravager = new Ravager(EntityType.RAVAGER, p.getLevel());
                                    ravager.setTarget(p);
                                    ravager.setSilent(true);
                                    ravager.setCustomName(new TextComponent(userName));
                                    ravager.setPos(x,y,z);
                                    ravager.positionRider(evoker);

                                    p.getLevel().addFreshEntity(ravager);

                                    RavagerEvoker--;
                                } else if (RavagerVindicator > 0) {

                                    Vindicator vindicator = new Vindicator(EntityType.VINDICATOR, p.getLevel());
                                    vindicator.setTarget(p);
                                    vindicator.setSilent(true);
                                    vindicator.setCustomName(new TextComponent(userName));

                                    Ravager ravager = new Ravager(EntityType.RAVAGER, p.getLevel());
                                    ravager.setTarget(p);
                                    ravager.setSilent(true);
                                    ravager.setCustomName(new TextComponent(userName));
                                    ravager.setPos(x,y,z);
                                    ravager.positionRider(vindicator);

                                    p.getLevel().addFreshEntity(ravager);

                                    RavagerVindicator--;
                                }
                            }
                        }
                    }
                });
            }
        }
        else if (id.equalsIgnoreCase(redemptions.getString("boo")) && !grace) {
            if (odds <= Config.Client.booOdds.get()) {
                ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                            p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 20, 3));
                        }
                    }
                });
            }
        }
        else if (id.equalsIgnoreCase(redemptions.getString("Mission Failed")) && !grace) {
            if (odds <= Config.Client.missionFailedOdds60s.get()) time = 60;
            else if (odds <= Config.Client.missionFailedOdds30s.get()) time = 30;
            disableShit = true;
        }
        else if (id.equalsIgnoreCase(redemptions.getString("Drop it")) && !grace) {
            if (odds <= Config.Client.dropItOdds.get()) {
                ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                            BlockPos pos = getPosBehind(p);
                            for (int y = pos.getY() + 4; y >= -60; y--) {
                                for (int x = -2; x <= 2; x++) {
                                    for (int z = -2; z <= 2; z++) {
                                        p.getLevel().setBlock(new BlockPos(pos.getX() + x, y, pos.getZ() + z), Blocks.AIR.defaultBlockState(), 3);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        else if (id.equalsIgnoreCase(redemptions.getString("Name Generator")) && !grace) {
            if (odds <= Config.Client.nameGenOdds.get()) {
                ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                            ArrayList<LivingEntity> livingEntities = new ArrayList<>();
                            for(Entity e : p.getLevel().getAllEntities()){
                                if(e instanceof LivingEntity le){
                                    livingEntities.add(le);
                                }
                            }
                            LivingEntity e = (LivingEntity) livingEntities.get(((int)(Math.random()*(livingEntities.size()-1)))).getType().create(p.getLevel());
                            e.setCustomName(new TextComponent(event.getRedemption().getUserInput()));
                            e.setPos(Vec3.atCenterOf(getPosBehind(p)));
                            p.getLevel().addFreshEntity(e);

                            e = (LivingEntity) livingEntities.get(((int)(Math.random()*(livingEntities.size()-1)))).getType().create(p.getLevel());
                            e.setCustomName(new TextComponent(event.getRedemption().getUserInput()));
                            e.setPos(Vec3.atCenterOf(getPosBehind(p)));
                            p.getLevel().addFreshEntity(e);
                        }
                    }
                });
            }
        }
        else if (id.equalsIgnoreCase(redemptions.getString("Ara Ara")) && !grace) {
            if (odds <= Config.Client.araAraOdds.get()) {
                ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                            BlockPos pos = getPosBehind(p);

                            for(int i = 0; i < 2; i++) {
                                Evoker evoker = new Evoker(EntityType.EVOKER, p.getLevel());
                                evoker.setTarget(p);
                                evoker.setSilent(true);
                                evoker.setCustomName(new TextComponent(userName));
                                evoker.setPos(Vec3.atCenterOf(pos));
                                p.getLevel().addFreshEntity(evoker);
                            }

                            for(int i = 0; i < 2; i++) {
                                Vindicator vindicator = new Vindicator(EntityType.VINDICATOR, p.getLevel());
                                vindicator.setTarget(p);
                                vindicator.setSilent(true);
                                vindicator.setCustomName(new TextComponent(userName));
                                vindicator.setPos(Vec3.atCenterOf(getPosBehind(p)));
                                p.getLevel().addFreshEntity(vindicator);
                            }

                            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,60 * 20, 2));
                        }
                    }
                });
            }
        }
        else if (id.equalsIgnoreCase(redemptions.getString("Hydrate")) && !grace) {
            if (odds <= Config.Client.hydrateOdds.get()) {
                ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                            BlockPos pos = p.getOnPos();
                            for(int x = pos.getX() - 50; x < pos.getX() + 50; x++) {
                                for (int y = pos.getY() - 50; y < pos.getY() + 50; y++) {
                                    BlockPos blockPos = new BlockPos(x, y, pos.getZ()+50);
                                    BlockState blockState = p.getLevel().getBlockState(blockPos);
                                    if(blockState.isAir()){
                                        p.getLevel().setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                                    }else if(blockState.hasProperty(BlockStateProperties.WATERLOGGED)){
                                        blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                                    }
                                }
                            }
                            for(int x = pos.getX() - 50; x < pos.getX() + 50; x++) {
                                for (int y = pos.getY() - 50; y < pos.getY() + 50; y++) {
                                    BlockPos blockPos = new BlockPos(x, y, pos.getZ()-50);
                                    BlockState blockState = p.getLevel().getBlockState(blockPos);
                                    if(blockState.isAir()){
                                        p.getLevel().setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                                    }else if(blockState.hasProperty(BlockStateProperties.WATERLOGGED)){
                                        blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                                    }
                                }
                            }

                            for(int z = pos.getZ() - 50; z < pos.getZ() + 50; z++) {
                                for (int y = pos.getY() - 50; y < pos.getY() + 50; y++) {
                                    BlockPos blockPos = new BlockPos(pos.getX()-50, y, z);
                                    BlockState blockState = p.getLevel().getBlockState(blockPos);
                                    if(blockState.isAir()){
                                        p.getLevel().setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                                    }else if(blockState.hasProperty(BlockStateProperties.WATERLOGGED)){
                                        blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                                    }
                                }
                            }
                            for(int z = pos.getZ() - 50; z < pos.getZ() + 50; z++) {
                                for (int y = pos.getY() - 50; y < pos.getY() + 50; y++) {
                                    BlockPos blockPos = new BlockPos(pos.getX()+50, y, z);
                                    BlockState blockState = p.getLevel().getBlockState(blockPos);
                                    if(blockState.isAir()){
                                        p.getLevel().setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3);
                                    }else if(blockState.hasProperty(BlockStateProperties.WATERLOGGED)){
                                        blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        if (cost >= 100) {
            ServerLifecycleHooks.getCurrentServer().execute(new Runnable() {
                @Override
                public void run() {
                    for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                        Skeleton skeleton = new Skeleton(EntityType.SKELETON, p.getLevel());
                        skeleton.setCustomName(new TextComponent(userName));
                        skeleton.setTarget(p);
                        skeleton.setSilent(true);
                        skeleton.setPos(Vec3.atCenterOf(p.getOnPos()));
                        p.getLevel().addFreshEntity(skeleton);
                    }
                }
            });
        }
        synchronized (viewerPoints) {
            Integer points = viewerPoints.get(user.getId());
            points = (points == null ? 0 : points);
            viewerPoints.put(user.getId(), (int) ((points + (cost * 0.10))));
        }
    }

    private BlockPos getPosBehind(ServerPlayer p) {
        int pitch = (int) p.getViewXRot(1f);
        BlockPos pos = p.getOnPos();
        if (pitch < 0) {
            pitch = 180 + (-pitch);
        }
        if (pitch == 360) {
            pitch = 0;
        }
        if (pitch >= -45 && pitch < 45) {
            pos.offset(0, 0, -1);
        } else if (pitch >= 45 && pitch < 135) {
            pos.offset(1, 0, 0);
        } else if (pitch >= 135 && pitch < 225) {
            pos.offset(0, 0, 1);
        } else if (pitch >= 225 && pitch < 360) {
            pos.offset(-1, 0, 0);
        }
        return pos;
    }

    /*static class WaterCube extends Thread{

        final Main plugin;
        final Player p;
        final int xOff;
        final int pPosX;
        final int pPosY;
        final int pPosZ;

        WaterCube(Main plugin, Player p, int xOff, int pPosX, int pPosY, int pPosZ) {
            this.plugin = plugin;
            this.p = p;
            this.xOff = xOff;
            this.pPosX = pPosX;
            this.pPosY = pPosY;
            this.pPosZ = pPosZ;
        }


        @Override
        public void run() {
            for (int x1 = -50+xOff; x1 <= xOff; x1++) {
                for (int y1 = -50; y1 <= 50; y1++) {
                    for (int z1 = -50; z1 <= 50; z1++) {
                        int x = x1, y = y1, z = z1;
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            Block block = p.getWorld().getBlockAt(pPosX + x, pPosY + y, pPosZ + z);
                            if (block.getType().isAir()) {
                                p.getWorld().setType(pPosX + x, pPosY + y, pPosZ + z, Material.WATER);
                            } else if (!(block instanceof Door) && block.getBlockData() instanceof Waterlogged w) {
                                w.setWaterlogged(true);
                            }
                        });
                        try {
                            sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }*/
}
