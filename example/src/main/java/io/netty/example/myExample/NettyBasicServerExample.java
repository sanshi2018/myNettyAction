package io.netty.example.myExample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyBasicServerExample {
    public void bind(int port){
        //netty的服务端编程要从EventLoopGroup开始，
        // 我们要创建两个EventLoopGroup，
        // 一个是boss专门用来接收连接，可以理解为处理accept事件，
        // 另一个是worker，可以关注除了accept之外的其它事件，处理子任务。
        //上面注意，boss线程一般设置一个线程，设置多个也只会用到一个，而且多个目前没有应用场景，
        // worker线程通常要根据服务器调优，如果不写默认就是cpu的两倍。
        EventLoopGroup bossGroup=new NioEventLoopGroup();

        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {
            //服务端要启动，需要创建ServerBootStrap，
            // 在这里面netty把nio的模板式的代码都给封装好了
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    //配置Server的通道，相当于NIO中的ServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) //设置ServerSocketChannel对应的Handler
                    //childHandler表示给worker那些线程配置了一个处理器，
                    // 这个就是上面NIO中说的，把处理业务的具体逻辑抽象出来，放到Handler里面
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new NormalInBoundHandler("NormalInBoundA",false))
                                    .addLast(new NormalInBoundHandler("NormalInBoundB",false))
                                    .addLast(new NormalInBoundHandler("NormalInBoundC",true));
                            socketChannel.pipeline()
                                    .addLast(new NormalOutBoundHandler("NormalOutBoundA"))
                                    .addLast(new NormalOutBoundHandler("NormalOutBoundB"))
                                    .addLast(new NormalOutBoundHandler("NormalOutBoundC"));
//                                    .addLast(new ExceptionHand);
                        }
                    });
            //绑定端口并同步等待客户端连接
            ChannelFuture channelFuture=bootstrap.bind(port).sync();
            System.out.println("Netty Server Started,Listening on :"+port);
            //等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放线程资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyBasicServerExample().bind(8080);
    }
}
