package org.kluge.server;

/**
 * Created by giko on 12/30/14.
 */
public interface RemoteOperation<T> {
    void execute(RemotingClient client, T data);
}
