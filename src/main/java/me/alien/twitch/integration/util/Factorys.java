package me.alien.twitch.integration.util;

import me.alien.twitch.integration.Main;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.domain.ChannelPointsUser;

import static me.alien.twitch.integration.Main.channelId;

public class Factorys {
    public static ChannelPointsRedemption redemptionFactory(EventUser user, String redemptionId){
        return redemptionFactory(user, redemptionId, 0);
    }
    public static ChannelPointsRedemption redemptionFactory(EventUser user, String redemptionId, int cost){
        ChannelPointsUser cpu = new ChannelPointsUser();
        cpu.setId(user.getId());
        cpu.setLogin(user.getName());
        cpu.setDisplayName(user.getName());

        ChannelPointsReward cpr = new ChannelPointsReward();
        cpr.setCost(cost);

        ChannelPointsRedemption fakeRedemption = new ChannelPointsRedemption();
        fakeRedemption.setId(redemptionId);
        fakeRedemption.setChannelId(channelId);
        fakeRedemption.setUser(cpu);
        fakeRedemption.setReward(cpr);

        return fakeRedemption;
    }
}
