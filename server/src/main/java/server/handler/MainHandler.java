package server.handler;

import common.Common;
import common.Config;
import common.domain.FileMessage;
import common.domain.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;

import java.nio.file.Files;
import java.nio.file.Path;

public class MainHandler extends ChannelInboundHandlerAdapter { // (1)

private final Config config;

    public MainHandler(Config config) {
        this.config = config;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Common cmd = new Common();
        String address = config.getServerRepo() + "\\" + UserPool.getUserName(ctx.channel());
        String inCorrectRequest = "Wrong command!! See help for details /help";
        if (msg instanceof MyMessage) {
            System.out.println("Client text message: " + ((MyMessage) msg).getText());
            String s = ((MyMessage) msg).getText();
            String[] strings = s.split(" ");
                if (strings[0].equals("/help")) {
                    ctx.writeAndFlush(new MyMessage((new CommandServer()).callHelpManual().toString()));
                } else if (strings[0].equals("/show")) {
                    ctx.writeAndFlush(new MyMessage((new Common()).showFiles(address)));
                } else if (strings[0].equals("/upload")) {
                    if (strings.length != 2) {
                        s = inCorrectRequest;
                    }
                } else if (strings[0].equals("/download") || strings.length == 2) {
                    Path senderFileAddress = Path.of(address + "\\" + strings[1]);
                    if (Files.exists(senderFileAddress)) {
                        Future f = ctx.writeAndFlush(cmd.sendFile(strings[1], senderFileAddress));
                        if (f.isDone()) {
                            ctx.writeAndFlush(new MyMessage("The requested file was transferred"));
                        }
                    } else {
                        ctx.writeAndFlush(new MyMessage("The specified file does not exist"));
                    }
                 } else if (strings[0].equals("/delete")) {
                    if (strings.length == 2) {
                        ctx.writeAndFlush(new MyMessage(cmd.deleteFile(strings[1], address)));
                    } else {
                        ctx.writeAndFlush(new MyMessage(inCorrectRequest));
                    }
                } else {
                    ctx.writeAndFlush(new MyMessage("Wrong command"));
                }
        } else if (msg instanceof FileMessage) {
        System.out.println("save file..");
        ctx.writeAndFlush(new MyMessage("Your file was successfully save"));
        (new Common()).receiveFile(msg, address);
        } else {
            System.out.printf("Server received wrong object!");
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
