package io.charon.connect;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * NettySourceConnectorConfig
 *
 * @author HeiShu
 */
public class CharonConnectorConfig extends AbstractConfig {

    public static ConfigDef config = buildConfigDef();

    public CharonConnectorConfig(Map<?, ?> originals) {
        super(config, originals);
    }

    public static ConfigDef buildConfigDef() {
        return new ConfigDef()
                .define(CharonConstant.KAFKA_TOPIC_CONFIG,
                        ConfigDef.Type.STRING,
                        "netty.connect",
                        ConfigDef.Importance.HIGH,
                        "Kafka topic to put received data.")
                .define(CharonConstant.CHARON_PORT,
                        ConfigDef.Type.INT,
                        8888,
                        ConfigDef.Importance.HIGH,
                        "WebSocket server port.")
                .define(CharonConstant.WS_PATH,
                        ConfigDef.Type.STRING,
                        "",
                        ConfigDef.Importance.HIGH,
                        "WebSocket server path.");
    }


}
