package org.kluge.remoting.server;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by giko on 12/29/14.
 */
public interface RemotingClient<T> {
    void startSharing();
    void stopSharing();
    void sendMessage(TextMessage message);
    void refresh();
    void disconnect();
    UUID getUUID();
    Optional<UserInfo> getInfo();
    SharingSession<T> getSession();
}
