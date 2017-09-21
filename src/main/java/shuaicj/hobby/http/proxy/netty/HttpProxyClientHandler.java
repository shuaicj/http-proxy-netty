package shuaicj.hobby.http.proxy.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;

/**
 * Handle data from client.
 *
 * @author shuaicj 2017/09/21
 */
@Slf4j
public class HttpProxyClientHandler extends ChannelInboundHandlerAdapter {

    private final String id;
    private Channel clientChannel;
    private Channel remoteChannel;

    private final HttpProxyClientHeader header = new HttpProxyClientHeader();

    public HttpProxyClientHandler(String id) {
        this.id = id;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        clientChannel = ctx.channel();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (header.isComplete()) {
            forward(msg);
            return;
        }
        ByteBuf in = (ByteBuf) msg;
        header.digest(in);
        if (header.isComplete()) {
            logger.info(id + " {}", header);
            clientChannel.config().setAutoRead(false);
            if (header.isHttps()) { // if https, respond 200 to create tunnel
                clientChannel.writeAndFlush("HTTP/1.1 200 Connection Established\r\n\r\n");
            }
            Bootstrap b = new Bootstrap();
            b.group(clientChannel.eventLoop()) // use the same EventLoop
                    .channel(clientChannel.getClass())
                    .handler(new HttpProxyRemoteHandler(id, clientChannel))
                    .option(ChannelOption.AUTO_READ, false);
            ChannelFuture f = b.connect(header.getHost(), header.getPort());
            remoteChannel = f.channel();
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ByteBuf out;
                    if (header.isHttps()) {
                        out = in; // forward remaining bytes, no header
                    } else {
                        out = Unpooled.copiedBuffer(header.getBytes(), in); // forward header and remaining bytes
                    }
                    forward(out);
                } else {
                    clientChannel.close();
                }
            });
        }
    }

    private void forward(Object in) {
        if (remoteChannel.isActive()) {
            remoteChannel.writeAndFlush(in).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    clientChannel.config().setAutoRead(true); // continue reading client if write to remote successfully
                } else {
                    remoteChannel.close();
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        flushAndClose(remoteChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        logger.error(id + " shit happens", e);
        flushAndClose(clientChannel);
    }

    private void flushAndClose(Channel ch) {
        if (ch != null && ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
