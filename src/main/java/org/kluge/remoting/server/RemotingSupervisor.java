package org.kluge.remoting.server;

import java.util.Optional;

/**
 * Created by giko on 1/1/15.
 */
public interface RemotingSupervisor<T> {
    void send(T data);
    void connect(RemotingClient<T> client);
    void disconnectFromClient();
    Optional<RemotingClient<T>> getClient();

    default void supervise(){
        getClient().ifPresent(client -> client.getSession().addSupervisor(this));
    }

    default void unSupervise(){
       getClient().ifPresent(client -> {
           client.getSession().removeSupervisor(this);
           client.sendMessage(new TextMessage("Administrator disconnected", "Disconnected", "info"));
       });
    }
}
