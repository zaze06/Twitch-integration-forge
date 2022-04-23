package me.alien.twitch.integration;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final ForgeConfigSpec clientConfig;
    public static final Config.Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> clientConfigPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientConfig = clientConfigPair.getRight();
        CLIENT = clientConfigPair.getLeft();
    }

    public static void saveClientConfig(){
        clientConfig.save();
    }

    public static class Client {
        public static ForgeConfigSpec.IntValue chargedCreeperOdds;
        public static ForgeConfigSpec.IntValue creeperOdds;
        public static ForgeConfigSpec.IntValue balloonPopOdds;
        public static ForgeConfigSpec.IntValue knockKnockOdds;
        public static ForgeConfigSpec.IntValue knockKnockBabyOdds;
        public static ForgeConfigSpec.IntValue nutOdds;
        public static ForgeConfigSpec.IntValue booOdds;
        public static ForgeConfigSpec.IntValue missionFailedOdds60s;
        public static ForgeConfigSpec.IntValue missionFailedOdds30s;
        public static ForgeConfigSpec.IntValue dropItOdds;
        public static ForgeConfigSpec.IntValue nameGenOdds;
        public static ForgeConfigSpec.IntValue araAraOdds;
        public static ForgeConfigSpec.IntValue hydrateOdds;

        public Client(ForgeConfigSpec.Builder builder){
            builder.push("twitch-integration-odds");
            {
                chargedCreeperOdds = builder.defineInRange("chargedCreeperOdds", 5, -1, 100);
                creeperOdds = builder.defineInRange("creeperOdds", 40, -1, 100);
                balloonPopOdds = builder.defineInRange("balloonPopOdds", 50, -1, 100);
                knockKnockOdds = builder.defineInRange("knockKnockOdds", 100, -1, 100);
                knockKnockBabyOdds = builder.defineInRange("knockKnockBabyOdds", 20, -1, 100);
                nutOdds = builder.defineInRange("nutOdds", 20, -1, 100);
                booOdds = builder.defineInRange("booOdds", 70, -1, 100);
                missionFailedOdds60s = builder.defineInRange("missionFailedOdds60s", 40, -1, 100);
                missionFailedOdds30s = builder.defineInRange("missionFailedOdds30s", 100, -1, 100);
                dropItOdds = builder.defineInRange("dropItOdds", 20, -1, 100);
                nameGenOdds = builder.defineInRange("nameGenOdds", 100, -1, 100);
                araAraOdds = builder.defineInRange("araAraOdds", 50, -1, 100);
                hydrateOdds = builder.defineInRange("hydrateOdds", 100, -1, 100);
            }
            builder.pop();
        }
    }
}
