package org.kluge.remoting.server;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by giko on 1/1/15.
 */
public abstract class AbstractSharingSession<T> implements SharingSession<T> {
    RemotingClient<T> client;
    Set<RemotingSupervisor<T>> supervisors = new HashSet<>();

    public AbstractSharingSession(RemotingClient<T> client) {
        this.client = client;
    }

    @Override
    public void broadcast(T data) {
        supervisors.forEach(supervisor -> supervisor.send(transform(data)));
    }

    protected abstract T transform(T data);

    @Override
    public RemotingClient<? extends T> getClient() {
        return client;
    }

    @Override
    public Set<RemotingSupervisor<T>> getSupervisors() {
        return new HashSet<>(supervisors);
    }

    @Override
    public void addSupervisor(RemotingSupervisor<T> supervisor) {
        supervisors.add(supervisor);
        updateBroadcastState();
    }

    @Override
    public void removeSupervisor(RemotingSupervisor<T> supervisor) {
        supervisors.remove(supervisor);
        updateBroadcastState();
    }

    private void updateBroadcastState() {
        if (supervisors.isEmpty()) {
            client.stopSharing();
        } else {
            client.startSharing();
        }
    }
}
