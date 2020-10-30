package io.charon.conatiner;

import io.charon.connect.CharonMessage;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author i321065
 */
public class GroupContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupContainer.class);

    private static final Map<String, Set<Channel>> groupIdAndChannels = new ConcurrentHashMap<>();

    public static void addChannel(String groupId, Channel channel) {
        Set<Channel> channels = groupIdAndChannels.get(groupId);
        if (channels == null) {
            channels = new HashSet<>();
            groupIdAndChannels.put(groupId, channels);
        }
        channels.add(channel);
    }

    public static void removeChannel(String groupId, Channel channel) {
        Set<Channel> channels = groupIdAndChannels.get(groupId);
        if (channels != null) {
            channels.remove(channel);
        }
    }

    public static void send(String groupId, CharonMessage message) {
        Set<Channel> channels = groupIdAndChannels.get(groupId);
        if (channels != null) {
            LOGGER.info("Prepare channel write => group: {}, msg: {}", message.getChannel(), message.getContent());
            channels.forEach(channel -> channel.writeAndFlush(new TextWebSocketFrame(message.getContent())));
        }
    }


}
