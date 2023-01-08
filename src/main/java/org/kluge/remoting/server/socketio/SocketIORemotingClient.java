package org.kluge.remoting.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import org.kluge.remoting.server.SharingSession;
import org.kluge.remoting.server.TextMessage;
import org.kluge.remoting.server.UpdatableRemotingClient;
import org.kluge.remoting.server.UserInfo;

import java.util.UUID;

/**
 * Created by giko on 12/30/14.
 */
public class SocketIORemotingClient<T> implements UpdatableRemotingClient<T> {
    private final SocketIOClient socketIOClient;
    private final SharingSession<T> session;
    private UserInfo info;

    public SocketIORemotingClient(
            SocketIOClient socketIOClient, SharingSessionFactory<T> sessionFactory) {
        if (socketIOClient == null) {
            throw new NullPointerException();
        }
        this.socketIOClient = socketIOClient;
        this.session = sessionFactory.createSession(this);
    }

    public SocketIOClient getSocketIOClient() {
        return socketIOClient;
    }

    @Override
    public void startSharing() {
        socketIOClient.sendEvent("startbroadcast");
    }

    @Override
    public void stopSharing() {
        socketIOClient.sendEvent("stopbroadcast");
    }

    @Override
    public void sendMessage(TextMessage message) {
        socketIOClient.sendEvent("message", message);
    }

    @Override
    public void ping() {
        socketIOClient.sendEvent("pingDom", String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void ping(Void t) {
        ping();
    }

    @Override
    public void refresh() {
        socketIOClient.sendEvent("reload");
    }

    @Override
    public void refresh(Void t) {
        refresh();
    }

    @Override
    public void disconnect() {
        session.getSupervisors();
    }

    @Override
    public void displayCountDown(Long time) {
        socketIOClient.sendEvent("countdown", time);
    }

    @Override
    public UUID getUUID() {
        return socketIOClient.getSessionId();
    }

    @Override
    public UserInfo getInfo() {
        return info;
    }

    @Override
    public void setInfo(UserInfo info) {
        if (this.info != null) {
            info.setPing(this.info.getPing());
        }
        this.info = info;
    }

    @Override
    public SharingSession<T> getSession() {
        return session;
    }
}
