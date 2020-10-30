# charon

`Charon` 是一款 `Kafka Connect Component`, 包含了 `Charon Source Connect`, `Charon Sink Connect`, 可以选择性使用.

`Charon Source Connect V1.0.0` 当前仅仅开放出了 `WebSocket` 能力给外部系统和设备生产频道消息。可以应用于日志收集、物联网数据、埋点...等等其他场景，直接将频道信息落入 `Kafka`。

`Charon Sink Connect V1.0.0` 当前仅仅开放出了 `WebSocket` 能力给外部系统和设备订阅频道消息。直接将信息推送给订阅指定频道消息的订阅者。

规划 可支持 HTTP 、MQTT、MODBUS 等等其他协议接入。