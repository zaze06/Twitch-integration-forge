package me.alien.twitch.integration;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {

    public final JSONObject credentials = new JSONObject(Loader.leadFile(getClass().getResourceAsStream("/credentials.json")));
    public final JSONObject redemptions = new JSONObject(Loader.leadFile(getClass().getResourceAsStream("/redemtions.json")));
    public final OAuth2Credential credential = new OAuth2Credential("twitch", credentials.getString("user_ID"));
    public final CredentialManager credentialManager = CredentialManagerBuilder.builder().build();
    public final JSONObject mysql = credentials.getJSONObject("mysql");
    public Connection conn = null;
    public final Map<String, Integer> viewerPoints = new HashMap<>();

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "twitch_integration";
    public static String chat;

    public Main() {
        MinecraftForge.EVENT_BUS.register(new Listener(this));
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.clientConfig);

        credentialManager.registerIdentityProvider(new TwitchIdentityProvider(credentials.getString("bot_ID"), credentials.getString("bot_Secreat"), ""));
    }
}
