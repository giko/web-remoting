package org.kluge.remoting.server;

import java.util.UUID;

/** Created by giko on 1/19/15. */
public interface BatchableRemotingClient {
  UUID getUUID();

  void displayCountDown(Long time);

  void refresh();

  void refresh(Void t);

  void sendMessage(TextMessage message);

  void ping();

  void ping(Void t);
}
