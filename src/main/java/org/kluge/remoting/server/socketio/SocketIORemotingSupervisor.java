package org.kluge.remoting.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import org.kluge.remoting.server.RemotingClient;
import org.kluge.remoting.server.RemotingSupervisor;
import org.kluge.remoting.server.SharingSession;
import org.kluge.remoting.server.TextMessage;

import java.util.Optional;

/**
 * Created by giko on 1/1/15.
 */
public class SocketIORemotingSupervisor implements RemotingSupervisor<String> {
    private SocketIOClient client;
    private Optional<RemotingClient<String>> activeClient = Optional.empty();
    
    public SocketIORemotingSupervisor(SocketIOClient client) {
        this.client = client;
    }

    @Override public void send(String data) {
        client.sendEvent("screen", data);
    }

    @Override public void connect(RemotingClient<String> client) {
        this.activeClient = Optional.of(client);
        client.sendMessage(new TextMessage("Administrator connected", "Connected", "info"));
    }

    @Override public void disconnectFromClient() {
        unSupervise();
        this.activeClient = Optional.empty();
        client.sendEvent("disconnected");
    }

    @Override public Optional<RemotingClient<String>> getClient() {
        return activeClient;
    }
}
