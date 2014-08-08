package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.util.Magic;

public class MagicServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {

        ByteBuf in = (ByteBuf) msg;
        try {
            ByteBuf out = ctx.alloc().buffer(in.readableBytes());
            while (in.isReadable()) {
                out.writeByte(Magic.doMagic(in.readByte()));
            }
            ctx.writeAndFlush(out);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
