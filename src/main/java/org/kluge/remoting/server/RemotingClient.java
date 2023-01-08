package org.kluge.remoting.server;

/**
 * Created by giko on 12/29/14.
 */
public interface RemotingClient<T> extends BatchableRemotingClient {
    void startSharing();

    void stopSharing();

    void disconnect();

    UserInfo getInfo();

    SharingSession<T> getSession();
}
