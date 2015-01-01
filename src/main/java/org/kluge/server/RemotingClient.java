package org.kluge.server;

import java.util.UUID;

/**
 * Created by giko on 12/29/14.
 */
public interface RemotingClient<T> {
    void startSharing();
    void stopSharing();
    void sendMessage(TextMessage message);
    void refresh();
    UUID getUUID();
    SharingSession<T> getSession();
}
