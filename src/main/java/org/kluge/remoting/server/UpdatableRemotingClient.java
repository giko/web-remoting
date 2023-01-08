package org.kluge.remoting.server;

/** Created by giko on 1/12/15. */
public interface UpdatableRemotingClient<T> extends RemotingClient<T> {
  void setInfo(UserInfo info);
}
