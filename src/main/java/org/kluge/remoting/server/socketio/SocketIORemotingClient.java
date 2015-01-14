package org.kluge.remoting.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import org.kluge.remoting.server.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by giko on 12/30/14.
 */
public class SocketIORemotingClient implements UpdatableRemotingClient<String> {
    private SocketIOClient socketIOClient;
    private SharingSession<String> session;
    private Optional<UserInfo> info = Optional.empty();

    public SocketIORemotingClient(SocketIOClient socketIOClient) {
        if (socketIOClient == null) {
            throw new NullPointerException();
        }
        this.socketIOClient = socketIOClient;
        this.session = new StringSharingSession(this);
    }

    public SocketIOClient getSocketIOClient() {
        return socketIOClient;
    }

    @Override public void startSharing() {
        socketIOClient.sendEvent("startbroadcast");
    }

    @Override public void stopSharing() {
        socketIOClient.sendEvent("stopbroadcast");
    }

    @Override public void sendMessage(TextMessage message) {
        socketIOClient.sendEvent("message", message);
    }

    @Override public void refresh() {
        socketIOClient.sendEvent("refresh");
    }

    @Override public void disconnect() {
        session.getSupervisors();
    }

    @Override public UUID getUUID() {
        return socketIOClient.getSessionId();
    }

    @Override public Optional<UserInfo> getInfo() {
        return info;
    }

    @Override public void setInfo(UserInfo info) {
        this.info = Optional.of(info);
    }

    @Override public SharingSession<String> getSession() {
        return session;
    }
}
