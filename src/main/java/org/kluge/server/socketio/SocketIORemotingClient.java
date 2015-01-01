package org.kluge.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import org.kluge.server.RemotingClient;
import org.kluge.server.SharingSession;
import org.kluge.server.StringSharingSession;
import org.kluge.server.TextMessage;

import java.util.UUID;

/**
 * Created by giko on 12/30/14.
 */
public class SocketIORemotingClient implements RemotingClient<String> {
    private SocketIOClient socketIOClient;
    private SharingSession<String> session;

    public SocketIOClient getSocketIOClient() {
        return socketIOClient;
    }

    public SocketIORemotingClient(SocketIOClient socketIOClient) {
        if (socketIOClient == null) {
            throw new NullPointerException();
        }
        this.socketIOClient = socketIOClient;
        this.session = new StringSharingSession(this);
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

    @Override public UUID getUUID() {
        return socketIOClient.getSessionId();
    }

    @Override public SharingSession<String> getSession() {
        return session;
    }
}
