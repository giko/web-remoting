package org.kluge.server;

/**
 * Created by giko on 1/1/15.
 */
public interface RemotingSupervisor<T> {
    void send(T data);
    void connect(RemotingClient<T> client);
    SharingSession<T> getConnection();
}
