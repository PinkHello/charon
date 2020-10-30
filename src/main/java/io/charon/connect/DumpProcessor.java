package io.charon.connect;

import io.charon.connect.util.GsonUtil;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;

/**
 * DumpProcessor
 *
 * @author HeiShu
 */
public class DumpProcessor implements CharonMessageProcessor {
    private String groupId;

    private CharonMessage message;

    public DumpProcessor(String groupId, CharonMessage message) {
        this.groupId = groupId;
        this.message = message;
    }

    @Override
    public SourceRecord[] getRecords(String kafkaTopic) {
        return new SourceRecord[]{
                new SourceRecord(
                        null,
                        null,
                        kafkaTopic,
                        Schema.STRING_SCHEMA,
                        groupId,
                        Schema.STRING_SCHEMA,
                        GsonUtil.gsonStr(message))
        };
    }
}
