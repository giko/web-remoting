package org.kluge.remoting.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import org.kluge.remoting.server.AbstractRemotingSupervisor;
import org.kluge.remoting.server.BatchableRemotingClient;

/**
 * Created by giko on 1/1/15.
 */
public class SocketIORemotingSupervisor<T> extends AbstractRemotingSupervisor<T> {
    private final SocketIOClient client;

    public SocketIORemotingSupervisor(SocketIOClient client) {
        this.client = client;
    }

    @Override
    public void disconnectFromClient(BatchableRemotingClient client) {
        super.disconnectFromClient(client);
        this.client.sendEvent("disconnected_from", client.getUUID().toString());
    }

    @Override
    public void connect(BatchableRemotingClient client) {
        this.client.sendEvent("connected_to", client.getUUID().toString());
        super.connect(client);
    }

    @Override
    public void send(T data) {
        client.sendEvent("screen", data);
    }

    @Override
    public void disconnectFromClient() {
        super.disconnectFromClient();
        client.sendEvent("disconnected");
    }
}
