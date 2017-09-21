package shuaicj.hobby.http.proxy.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * The channel initializer.
 *
 * @author shuaicj 2017/09/21
 */
public class HttpProxyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private long taskCount;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.INFO),
                new HttpProxyClientHandler("task-" + taskCount++)
        );
    }
}
