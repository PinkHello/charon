package io.charon.connect;

/**
 * NettyMessage
 *
 * @author HeiShu
 */
public class CharonMessage {

    public CharonMessage() {
    }

    public CharonMessage(String ip, String content, String channel) {
        this.ip = ip;
        this.content = content;
        this.channel = channel;
    }

    private String ip;

    private String content;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    private String channel;
}
