package org.kluge.remoting.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import org.kluge.remoting.server.AbstractRemotingSupervisor;

/**
 * Created by giko on 1/1/15.
 */
public class SocketIORemotingSupervisor<T> extends AbstractRemotingSupervisor<T> {
    private SocketIOClient client;

    public SocketIORemotingSupervisor(SocketIOClient client) {
        this.client = client;
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
