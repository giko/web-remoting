package org.kluge;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.DefaultExceptionListener;
import org.kluge.server.RemotingServer;
import org.kluge.server.socketio.SocketIORemotingServer;

/**
 * Created by giko on 12/25/14.
 */
public class Main {

    public static void main(String[] args){
        Configuration config = new Configuration();
        config.setHostname("192.168.10.241");
        config.setPort(8083);
        config.setMaxHttpContentLength(Integer.MAX_VALUE);
        config.setMaxFramePayloadLength(Integer.MAX_VALUE);
        config.getSocketConfig().setReuseAddress(true);
        config.setExceptionListener(new DefaultExceptionListener());

        final SocketIOServer server = new SocketIOServer(config);

        RemotingServer remotingServer = new SocketIORemotingServer(server);

        try {
            server.startAsync().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("An error occurred!", e);
        }
    }
}
