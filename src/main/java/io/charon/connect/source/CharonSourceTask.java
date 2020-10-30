package io.charon.connect.source;

import io.charon.conatiner.NettyContainer;
import io.charon.conatiner.TextWebSocketHandler;
import io.charon.connect.CharonConnectorConfig;
import io.charon.connect.CharonConstant;
import io.charon.connect.CharonMessageProcessor;
import io.charon.connect.util.Version;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * NettySourceTask
 *
 * @author HeiShu
 */
public class CharonSourceTask extends SourceTask {

    public static final Logger LOGGER = LoggerFactory.getLogger(CharonSourceTask.class);

    CharonConnectorConfig connectorConfig;

    String kafkaTopic;

    BlockingQueue<CharonMessageProcessor> msgQueue = new LinkedBlockingQueue<>();

    NettyContainer container;

    @Override
    public String version() {
        return Version.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        LOGGER.info("Netty source task starting ...");
        connectorConfig = new CharonConnectorConfig(props);
        kafkaTopic = connectorConfig.getString(CharonConstant.KAFKA_TOPIC_CONFIG);
        int nettyPort = connectorConfig.getInt(CharonConstant.CHARON_PORT);
        String webSocketPath = connectorConfig.getString(CharonConstant.WS_PATH);
        container = new NettyContainer(nettyPort, new TextWebSocketHandler(webSocketPath, msgQueue));
        container.start();
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new ArrayList<>();
        CharonMessageProcessor message = msgQueue.take();
        Collections.addAll(records, message.getRecords(kafkaTopic));
        return records;
    }

    @Override
    public void stop() {
        LOGGER.info("Netty source task stop.");
    }

}
