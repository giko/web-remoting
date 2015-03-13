package org.kluge.remoting.server;

/**
 * Created by giko on 12/30/14.
 */
public interface RemoteOperation<K> {
    void execute(BatchableRemotingClient client, K data);
}
