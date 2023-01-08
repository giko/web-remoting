package org.kluge.remoting.server;

import java.util.Set;

/** Created by giko on 12/29/14. */
public interface RemotingServer<T> {
  Set<RemotingClient<T>> getRemotingClients();

  Set<RemotingSupervisor<T>> getSupervisors();

  Set<SharingSession<T>> getSessions();

  void addRemotingClient(RemotingClient<T> client);

  void addSuprevisor(RemotingSupervisor<T> supervisor);
}
