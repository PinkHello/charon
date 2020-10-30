package io.charon.connect;

import org.apache.kafka.connect.source.SourceRecord;

/**
 * NettyMessageProcessor
 *
 * @author HeiShu
 */
public interface CharonMessageProcessor {

    /**
     * getRecords
     *
     * @param kafkaTopic
     * @return
     */
    SourceRecord[] getRecords(String kafkaTopic);
}
