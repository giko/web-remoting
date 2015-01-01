package org.kluge.server.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import org.kluge.server.RemoteOperation;

/**
 * Created by giko on 12/30/14.
 */
public class RemoteOperationDataListener<T> implements DataListener<T> {
    RemoteOperation<T> operation;
    SocketIORemotingServer server;

    public RemoteOperationDataListener(SocketIORemotingServer server, RemoteOperation<T> operation) {
        this.operation = operation;
        this.server = server;
    }

    @Override public void onData(SocketIOClient client, T data, AckRequest ackSender) throws Exception {
        operation.execute(server.activeConnections.get(client), data);
    }
}
