package io.charon.connect.sink;

import io.charon.conatiner.GroupContainer;
import io.charon.conatiner.NettyContainer;
import io.charon.conatiner.TextWebSocketHandler;
import io.charon.connect.CharonConnectorConfig;
import io.charon.connect.CharonConstant;
import io.charon.connect.CharonMessage;
import io.charon.connect.util.GsonUtil;
import io.charon.connect.util.Version;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author wuqiantai
 */
public class CharonSinkTask extends SinkTask {

    public static final Logger LOGGER = LoggerFactory.getLogger(CharonSinkTask.class);

    CharonConnectorConfig connectorConfig;

    int nettyPort;

    NettyContainer container;

    @Override
    public String version() {
        return Version.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        LOGGER.info("Netty sink task starting ...");
        connectorConfig = new CharonConnectorConfig(props);
        nettyPort = connectorConfig.getInt(CharonConstant.CHARON_PORT);
        String webSocketPath = connectorConfig.getString(CharonConstant.WS_PATH);
        container = new NettyContainer(nettyPort, new TextWebSocketHandler(webSocketPath));
        container.start();

    }

    /**
     * 写出到 netty
     *
     * @param sinkRecords
     */
    @Override
    public void put(Collection<SinkRecord> sinkRecords) {
        for (SinkRecord record : sinkRecords) {
            String groupId = String.valueOf(record.key());
            try {
                CharonMessage nettyMessage = GsonUtil.GsonToBean(record.value().toString(), CharonMessage.class);
                GroupContainer.send(groupId, nettyMessage);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void stop() {
        LOGGER.info("Netty sink task stop.");
    }

}
