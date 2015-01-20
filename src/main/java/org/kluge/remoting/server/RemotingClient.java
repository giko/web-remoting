package org.kluge.remoting.server;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by giko on 12/29/14.
 */
public interface RemotingClient<T> extends BatchableRemotingClient {
    void startSharing();

    void stopSharing();

    void disconnect();

    UUID getUUID();

    Optional<UserInfo> getInfo();

    SharingSession<T> getSession();
}
