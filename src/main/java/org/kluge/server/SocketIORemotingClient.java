package org.kluge.server;

import com.corundumstudio.socketio.SocketIOClient;

/**
 * Created by giko on 12/30/14.
 */
public class SocketIORemotingClient implements RemotingClient {
    private SocketIOClient socketIOClient;

    public SocketIOClient getSocketIOClient() {
        return socketIOClient;
    }

    public SocketIORemotingClient(SocketIOClient socketIOClient) {
        if (socketIOClient == null) {
            throw new NullPointerException();
        }
        this.socketIOClient = socketIOClient;
    }

    @Override public void startSharing() {
        socketIOClient.sendEvent("startsharing");
    }

    @Override public void stopSharing() {
        socketIOClient.sendEvent("stopsharing");
    }

    @Override public void sendMessage(TextMessage message) {
        socketIOClient.sendEvent("message", message);
    }

    @Override public void refresh() {
        socketIOClient.sendEvent("refresh");
    }
}
