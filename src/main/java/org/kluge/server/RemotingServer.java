package org.kluge.server;

import java.util.Set;

/**
 * Created by giko on 12/29/14.
 */
public interface RemotingServer<T> {
    Set<RemotingClient> getRemotingClients();
    Set<RemotingSupervisor<T>> getSupervisors();
    Set<SharingSession<T>> getSessions();
    void addToSession(RemotingSupervisor<T> supervisor, RemotingClient client);
    void addRemotingClient(RemotingClient client);
}
