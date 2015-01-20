package org.kluge.remoting.server;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by giko on 1/1/15.
 */
public abstract class StringSharingSession implements SharingSession<String> {
    RemotingClient<String> client;
    Set<RemotingSupervisor<String>> supervisors = new HashSet<>();

    public StringSharingSession(RemotingClient<String> client) {
        this.client = client;
    }

    @Override
    public void broadcast(String data) {
        supervisors.forEach(supervisor -> supervisor.send(transform(data)));
    }

    protected abstract String transform(String data);

    @Override
    public RemotingClient getClient() {
        return client;
    }

    @Override
    public Set<RemotingSupervisor<String>> getSupervisors() {
        return new HashSet<>(supervisors);
    }

    @Override
    public void addSupervisor(RemotingSupervisor<String> supervisor) {
        supervisors.add(supervisor);
        updateBroadcastState();
    }

    @Override
    public void removeSupervisor(RemotingSupervisor<String> supervisor) {
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
