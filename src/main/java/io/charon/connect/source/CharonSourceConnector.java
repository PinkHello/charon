package io.charon.connect.source;

import io.charon.connect.CharonConnectorConfig;
import io.charon.connect.CharonConstant;
import io.charon.connect.util.Version;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * NettySourceConnector
 *
 * @author HeiShu
 */
public class CharonSourceConnector extends SourceConnector {

    public static final Logger LOGGER = LoggerFactory.getLogger(CharonSourceConnector.class);

    private Map<String, String> properties;
    CharonConnectorConfig connectorConfig;

    @Override
    public void start(Map<String, String> props) {
        properties = props;
        connectorConfig = new CharonConnectorConfig(props);
        String kafkaTopic = connectorConfig.getString(CharonConstant.KAFKA_TOPIC_CONFIG);
        if (Objects.isNull(kafkaTopic) || kafkaTopic == "") {
            throw new RuntimeException("kafka.topic config is empty.");
        }
        int nettyPort = connectorConfig.getInt(CharonConstant.CHARON_PORT);
        if (nettyPort < 0) {
            throw new RuntimeException("netty.port config less zero.");
        }
        String webSocketPath = connectorConfig.getString(CharonConstant.WS_PATH);
        if (Objects.isNull(webSocketPath)) {
            throw new RuntimeException("websocket.path config is null.");
        }
        LOGGER.info("Netty source connector started.");
    }

    @Override
    public Class<? extends Task> taskClass() {
        return CharonSourceTask.class;
    }

    @Override public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> configs = new ArrayList<>(1);
        Map<String, String> config = new HashMap<>(properties);
        configs.add(config);
        return configs;
    }

    @Override
    public void stop() {
        LOGGER.info("Netty source connector stop.");
    }

    @Override
    public ConfigDef config() {
        return CharonConnectorConfig.config;
    }

    @Override
    public String version() {
        return Version.getVersion();
    }
}
