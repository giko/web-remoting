package org.kluge.remoting.server;

/**
 * Created by giko on 1/19/15.
 */
public interface BatchableRemotingClient {
    void displayCountDown(Long time);

    void refresh();

    void refresh(Void t);

    void sendMessage(TextMessage message);
}
