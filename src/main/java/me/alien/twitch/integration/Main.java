package me.alien.twitch.integration;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import me.alien.twitch.integration.util.Level;
import me.alien.twitch.integration.util.Action;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.alien.twitch.integration.Listener.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {

    public static final JSONObject credentials = new JSONObject(Loader.leadFile(Main.class.getResourceAsStream("/credentials.json")));
    public static final JSONObject redemptions = new JSONObject(Loader.leadFile(Main.class.getResourceAsStream("/redemtions.json")));
    public static final OAuth2Credential credential = new OAuth2Credential("twitch", credentials.getString("user_ID"));
    public final CredentialManager credentialManager = CredentialManagerBuilder.builder().build();
    public final JSONObject mysql = credentials.getJSONObject("mysql");
    public static Connection conn = null;
    public static final Map<String, Integer> viewerPoints = new HashMap<>();

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "twitch_integration";
    public static String chat;
    public static Level minecraftChat = Level.NONE;
    public static boolean twitchChat = false;
    public static int graceTime = 0;
    public static boolean grace = false;
    public static int graceTimeOrig = 0;
    public static int time = 0;
    public static boolean disableShit = false;
    public static String channelId = null;
    public static final ArrayList<Action> ActionList = new ArrayList<>();

    public Main() {
        MinecraftForge.EVENT_BUS.register(new Listener(this));
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.clientConfig);

        credentialManager.registerIdentityProvider(new TwitchIdentityProvider(credentials.getString("bot_ID"), credentials.getString("bot_Secreat"), ""));

        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withEnablePubSub(true)
                .withEnableHelix(true)
                .withEnableTMI(true)
                .withChatAccount(credential)
                .withCredentialManager(credentialManager)
                .build();
    }
}
