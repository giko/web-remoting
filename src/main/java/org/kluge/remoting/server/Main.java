package org.kluge.remoting.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.eclipse.jetty.server.Server;
import org.kluge.remoting.server.http.HttpRemotingSupervisor;
import org.kluge.remoting.server.socketio.SocketIORemotingServer;

/**
 * Created by giko on 12/25/14.
 */
public class Main {

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("192.168.10.241");
        config.setPort(8083);
        config.setMaxHttpContentLength(Integer.MAX_VALUE);
        config.setMaxFramePayloadLength(Integer.MAX_VALUE);
        config.getSocketConfig().setReuseAddress(true);

        Server httpServer = new Server(8085);

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
