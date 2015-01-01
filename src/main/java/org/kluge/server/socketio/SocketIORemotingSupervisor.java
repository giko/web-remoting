package org.kluge.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import org.kluge.server.RemotingClient;
import org.kluge.server.RemotingSupervisor;
import org.kluge.server.SharingSession;

/**
 * Created by giko on 1/1/15.
 */
public class SocketIORemotingSupervisor implements RemotingSupervisor<String> {
    private SocketIOClient client;
    private RemotingClient<String> activeClient;
    
    public SocketIORemotingSupervisor(SocketIOClient client) {
        this.client = client;
    }

    @Override public void send(String data) {
        client.sendEvent("screen", data);
    }

    @Override public void connect(RemotingClient<String> client) {
        this.activeClient = client;
    }

    @Override public SharingSession<String> getConnection() {
        return activeClient.getSession();
    }
}
