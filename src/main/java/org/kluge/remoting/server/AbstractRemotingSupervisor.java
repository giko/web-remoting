package org.kluge.remoting.server;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/** Created by giko on 1/16/15. */
public abstract class AbstractRemotingSupervisor<T> implements RemotingSupervisor<T> {
  private final Set<BatchableRemotingClient> clients = new HashSet<>();
  private final Boolean isSilent;
  private Optional<RemotingClient<T>> activeClient = Optional.empty();

  public AbstractRemotingSupervisor(Boolean isSilent) {
    this.isSilent = isSilent;
  }

  public void connect(RemotingClient<T> client) {
    this.activeClient = Optional.of(client);
    if (!isSilent) {
      client.sendMessage(new TextMessage("Administrator connected", "Connected", "info"));
    }
  }

  public void connect(BatchableRemotingClient client) {
    clients.add(client);
  }

  public void disconnectFromClient(BatchableRemotingClient client) {
    if (activeClient.equals(Optional.of(client))) {
      unSupervise();
      this.activeClient = Optional.empty();
    }
    clients.remove(client);
  }

  @Override
  public void disconnectFromClient() {
    activeClient.ifPresent(
        client -> {
          unSupervise();
          this.activeClient = Optional.empty();
        });
  }

  public Set<BatchableRemotingClient> getBatchableRemotingClients() {
    return clients;
  }

  public Optional<RemotingClient<T>> getClient() {
    return activeClient;
  }

  public void unSupervise() {
    getClient()
        .ifPresent(
            client -> {
              client.getSession().removeSupervisor(this);
              if (!isSilent) {
                client.sendMessage(
                    new TextMessage("Administrator disconnected", "Disconnected", "info"));
              }
            });
  }
}
