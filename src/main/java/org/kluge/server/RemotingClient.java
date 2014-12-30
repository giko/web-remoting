package org.kluge.server;

/**
 * Created by giko on 12/29/14.
 */
public interface RemotingClient {
    void startSharing();
    void stopSharing();
    void sendMessage(TextMessage message);
    void refresh();
}
