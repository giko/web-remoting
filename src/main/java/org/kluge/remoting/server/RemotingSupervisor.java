package org.kluge.remoting.server;

import java.util.Optional;
import java.util.Set;

/**
 * Created by giko on 1/1/15.
 */
public interface RemotingSupervisor<T> {
    void send(T data);

    void connect(RemotingClient<T> client);
    
    void connect(BatchableRemotingClient client);

    void disconnectFromClient(BatchableRemotingClient client);

    void disconnectFromClient();

    Optional<RemotingClient<T>> getClient();

    Set<BatchableRemotingClient> getBatchableRemotingClients();

    default void supervise() {
        getClient().ifPresent(client -> client.getSession().addSupervisor(this));
    }

    default void unSupervise() {
        getClient().ifPresent(client -> {
            client.getSession().removeSupervisor(this);
            client.sendMessage(new TextMessage("Administrator disconnected", "Disconnected", "info"));
        });
    }
}
