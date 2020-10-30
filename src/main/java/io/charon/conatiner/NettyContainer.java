package io.charon.conatiner;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nick.yin
 * @date 2020/10/29
 * @since 2020/10/29
 */
public class NettyContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyContainer.class);

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors() * 2;

    private static EventLoopGroup BOSS = new NioEventLoopGroup(AVAILABLE_PROCESSORS);

    private static EventLoopGroup WORKER = new NioEventLoopGroup(AVAILABLE_PROCESSORS);

    private int port;

    private TextWebSocketHandler webSocketHandler;

    public NettyContainer(int port, TextWebSocketHandler webSocketHandler) {
        this.port = port;
        this.webSocketHandler = webSocketHandler;
    }

    public ServerBootstrap start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(BOSS, WORKER)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.TRACE))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.TRACE))
                                // HttpRequestDecoder和HttpResponseEncoder的一个组合，针对http协议进行编解码
                                .addLast(new HttpServerCodec())
                                // 分块向客户端写数据，防止发送大文件时导致内存溢出， channel.write(new ChunkedFile(new File("bigFile.mkv")))
                                .addLast(new ChunkedWriteHandler())
                                // 将HttpMessage和HttpContents聚合到一个完成的 FullHttpRequest或FullHttpResponse中,具体是FullHttpRequest对象还是FullHttpResponse对象取决于是请求还是响应
                                // 需要放到HttpServerCodec这个处理器后面
                                .addLast(new HttpObjectAggregator(10240))
                                // webSocket 数据压缩扩展，当添加这个的时候WebSocketServerProtocolHandler的第三个参数需要设置成true
                                .addLast(new WebSocketServerCompressionHandler())
                                // 自定义处理器 - 处理 web socket 文本消息
                                .addLast(webSocketHandler)
                                // 服务器端向外暴露的 web socket 端点，当客户端传递比较大的对象时，maxFrameSize参数的值需要调大
                                .addLast(new WebSocketServerProtocolHandler(webSocketHandler.getPath(), null, true, 10485760));
                    }
                })
                .bind(port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                LOGGER.info("Websocket container start successful. Listen at :{} ......", port);
            } else {
                LOGGER.error("Websocket container start failed.", future.cause());
                System.exit(-1);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            BOSS.shutdownGracefully().syncUninterruptibly();
            WORKER.shutdownGracefully().syncUninterruptibly();
        }));
        return bootstrap;
    }
}
