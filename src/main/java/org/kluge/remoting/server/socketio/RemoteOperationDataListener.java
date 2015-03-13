package org.kluge.remoting.server.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import org.kluge.remoting.server.RemoteOperation;

/**
 * Created by giko on 12/30/14.
 */
public class RemoteOperationDataListener<T, K> implements DataListener<K> {
    final RemoteOperation<K> operation;
    final SocketIORemotingServer<T> server;

    public RemoteOperationDataListener(SocketIORemotingServer<T> server, RemoteOperation<K> operation) {
        this.operation = operation;
        this.server = server;
    }

    @Override
    public void onData(SocketIOClient client, K data, AckRequest ackSender) throws Exception {
        server.supervisors.get(client).getBatchableRemotingClients().forEach(remotingClient -> operation.execute(remotingClient, data));
    }
}
