package org.kluge.remoting.server;

/**
 * Created by giko on 3/10/15.
 */
public class PingTask implements Runnable {
    protected BatchableRemotingClient client;

    public PingTask(BatchableRemotingClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        client.ping();
    }
}
