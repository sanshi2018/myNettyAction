package io.netty.example.myExample;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class NormalOutBoundHandler extends ChannelOutboundHandlerAdapter {
    private final String name;

    public NormalOutBoundHandler(String name) {
        this.name = name;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutBoundHandler:"+name);
        super.write(ctx, msg, promise);
    }
}
