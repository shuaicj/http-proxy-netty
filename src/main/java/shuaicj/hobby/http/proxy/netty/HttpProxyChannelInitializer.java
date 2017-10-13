package shuaicj.hobby.http.proxy.netty;

import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The channel initializer.
 *
 * @author shuaicj 2017/09/21
 */
@Component
public class HttpProxyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired private AtomicLong taskCounter;
    @Autowired private ApplicationContext appCtx;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.DEBUG),
                appCtx.getBean(HttpProxyClientHandler.class, "task-" + taskCounter.getAndIncrement())
        );
    }
}
