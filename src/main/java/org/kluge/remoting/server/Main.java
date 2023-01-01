package org.kluge.remoting.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import de.hasait.clap.CLAP;
import de.hasait.clap.CLAPResult;
import de.hasait.clap.CLAPValue;
import de.hasait.clap.shadeddeps.oaclang3.ObjectUtils;
import org.eclipse.jetty.server.Server;
import org.kluge.remoting.server.http.HttpRemotingSupervisor;
import org.kluge.remoting.server.socketio.SocketIORemotingServer;

/**
 * Created by giko on 12/25/14.
 */
public class Main {

    public static void main(String[] args) {
        CLAP clap = new CLAP();
        CLAPValue<String> hostNameOption = clap.addOption1(String.class,'b', "bind", false, "Bind hostname", "bind");
        CLAPValue<Integer> httpPortOption = clap.addOption1(Integer.class, 'p', "httpPort", false, "Http port", "httpPort");
        CLAPValue<Integer> websocketPortOption = clap.addOption1(Integer.class, 'w', "wsPort", false, "Websocket port", "wsPort");

        CLAPResult result = clap.parse(args);

        Configuration config = new Configuration();
        config.setHostname(ObjectUtils.defaultIfNull(result.getValue(hostNameOption), "0.0.0.0"));
        config.setPort(ObjectUtils.defaultIfNull(result.getValue(websocketPortOption), 8082));
//        config.setMaxHttpContentLength(Integer.MAX_VALUE);
//        config.setMaxFramePayloadLength(Integer.MAX_VALUE);
        config.getSocketConfig().setReuseAddress(true);

        Server httpServer = new Server(ObjectUtils.defaultIfNull(result.getValue(httpPortOption), 8085));
        final SocketIOServer server = new SocketIOServer(config);

        RemotingServer<String> remotingServer = new SocketIORemotingServer<>(server, String.class,
                DomSharingSession::new);

        new HttpRemotingSupervisor(httpServer, remotingServer);

        try {
            httpServer.start();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred!", e);
        }

        try {
            server.startAsync().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("An error occurred!", e);
        }
    }
}
