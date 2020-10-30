package io.charon.conatiner;

import io.charon.connect.DumpProcessor;
import io.charon.connect.CharonMessage;
import io.charon.connect.CharonMessageProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

/**
 * @author nick.yin
 * @date 2020/10/29
 * @since 2020/10/29
 */
@ChannelHandler.Sharable
public class TextWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextWebSocketHandler.class);

    private static final AttributeKey<String> GROUP_ID_ATTR = AttributeKey.valueOf("groupId");

    private String path;

    private BlockingQueue<CharonMessageProcessor> msgQueue;

    private boolean sinkMod;

    public String getPath() {
        return this.path;
    }

    public TextWebSocketHandler(String path, BlockingQueue<CharonMessageProcessor> msgQueue) {
        this.path = path;
        this.msgQueue = msgQueue;
        this.sinkMod = false;
    }

    public TextWebSocketHandler(String path) {
        this.path = path;
        this.sinkMod = true;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        Channel channel = ctx.channel();
        String groupId = channel.attr(GROUP_ID_ATTR).get();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = inetSocketAddress.getHostName();
        CharonMessage nettyMessage = new CharonMessage(ip, msg.text(), groupId);
        CharonMessageProcessor dumpProcessor = new DumpProcessor(groupId, nettyMessage);
        msgQueue.add(dumpProcessor);
        LOGGER.info("Channel receive => group: {}, ip: {}, msg: {}{}",
                nettyMessage.getChannel(),
                nettyMessage.getIp(),
                nettyMessage.getContent());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Channel handler error.", cause);
        ctx.close();

    }

    /**
     * 当连接断开时，需要将此channel移除当前groupId的组
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        GroupContainer.removeChannel(channel.attr(GROUP_ID_ATTR).get(), channel);
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg != null && msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String origin = request.headers().get("Origin");
            String uri = request.uri();
            if (Objects.isNull(origin) || Objects.isNull(uri) || !uri.contains("?")) {
                LOGGER.warn("origin: {}, uri:{}, listen path:{}", origin, request, path);
                ctx.close();
                return;
            }
            String compareUri = uri.substring(0, uri.indexOf("?"));
            if (!compareUri.equals(path)) {
                LOGGER.warn("origin: {}, uri:{}, listen path:{}", origin, request, path);
                ctx.close();
                return;
            }
            String groupId = parseUri(uri);
            if (Objects.isNull(groupId)) {
                ctx.close();
                return;
            }
            if (sinkMod) {
                GroupContainer.addChannel(groupId, ctx.channel());
            }
            ctx.channel().attr(GROUP_ID_ATTR).set(groupId);
            request.setUri(getPath());
        }

        super.channelRead(ctx, msg);
    }

    private String parseUri(String uri) {
        String[] uriArray = uri.split("\\?");
        if (null != uriArray && uriArray.length > 1) {
            String[] paramsArray = uriArray[1].split("=");
            if (null != paramsArray && paramsArray.length > 1) {
                return paramsArray[1];
            }
        }
        return null;
    }

}
