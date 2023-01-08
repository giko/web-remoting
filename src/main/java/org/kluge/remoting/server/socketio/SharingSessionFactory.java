package org.kluge.remoting.server.socketio;

import org.kluge.remoting.server.RemotingClient;
import org.kluge.remoting.server.SharingSession;

/** Created by giko on 1/20/15. */
public interface SharingSessionFactory<T> {
  SharingSession<T> createSession(RemotingClient<T> remotingClient);
}
