package org.kluge.server;

import java.util.Set;

/**
 * Created by giko on 12/31/14.
 */
public interface SharingSession<T> {
    void broadcast(T data);
    RemotingClient getClient();
    void addSupervisor(RemotingSupervisor<T> supervisor);
    Set<RemotingSupervisor<T>> getSupervisors();
}
