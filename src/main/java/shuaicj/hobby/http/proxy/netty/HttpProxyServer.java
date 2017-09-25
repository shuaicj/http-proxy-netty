package shuaicj.hobby.http.proxy.netty;

import javax.annotation.PostConstruct;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A simple http proxy server.
 *
 * @author shuaicj 2017/09/21
 */
@Component
@Slf4j
public class HttpProxyServer {

    private final int port;

    public HttpProxyServer(@Value("${proxy.port}") int port) {
        this.port = port;
    }

    @PostConstruct
    public void start() {
        new Thread(() -> {
            logger.info("HttpProxyServer started on port: {}", port);
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        // .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new HttpProxyChannelInitializer())
                        .bind(port).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("shit happens", e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }).start();
    }
}
