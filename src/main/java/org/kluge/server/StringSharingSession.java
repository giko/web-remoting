package org.kluge.server;

import java.util.Set;

/**
 * Created by giko on 1/1/15.
 */
public class StringSharingSession implements SharingSession<String> {
    RemotingClient client;
    Set<RemotingSupervisor<String>> supervisors;
    
    public StringSharingSession(RemotingClient client) {
        this.client = client;
    }

    @Override public void broadcast(String data) {
        supervisors.forEach(supervisor -> supervisor.send(data));
    }

    @Override public RemotingClient getClient() {
        return client;
    }

    @Override public Set<RemotingSupervisor<String>> getSupervisors() {
        return supervisors;
    }

    @Override public void addSupervisor(RemotingSupervisor supervisor) {
        supervisors.add(supervisor);
    }
}
