package org.kluge.remoting.server;

/**
 * Created by giko on 12/30/14.
 */
public interface RemoteOperation<T, K> {
    void execute(RemotingClient<T> client, K data);
}
