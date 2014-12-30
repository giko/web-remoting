package org.kluge.server;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.kluge.ClientInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by giko on 12/30/14.
 */
public class SocketIORemotingServer implements RemotingServer {
    public static int clientsCounter = 0;

    protected Map<SocketIOClient, RemotingClient> activeConnections = new HashMap<>();
    protected Map<SocketIOClient, SocketIOClient> activeSharings = new HashMap<>();

    Map<Integer, SocketIOClient> clients = new HashMap<>();
    protected  SocketIOServer socketIOServer;
    
    private List<ClientInfo> getClientInfos() {
        return socketIOServer.getAllClients().stream().map(ClientInfo::new).collect(Collectors.toList());
    }

    public SocketIORemotingServer(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        socketIOServer.addConnectListener(client -> {
            ++clientsCounter;
            client.set("id", clientsCounter);
            clients.put(clientsCounter, client);
            client.sendEvent("clients", getClientInfos());
        });

        socketIOServer.addDisconnectListener(clients::remove);
        socketIOServer.addEventListener("remote", Integer.class, (client, data, ackSender) -> {
            System.out.println("Connected to client " + data);
            RemotingClient remotingClient = new SocketIORemotingClient(clients.get(data));

            TextMessage greetingMessage = new TextMessage();
            greetingMessage.setTitle("Connected");
            greetingMessage.setMessage("Administrator connected");
            greetingMessage.setType("info");
            remotingClient.sendMessage(greetingMessage);

            activeConnections.put(client, remotingClient);
        });
        socketIOServer.addEventListener("startbroadcast", String.class, (client, data, ackSender) -> {
            activeConnections.get(client).startSharing();
            activeSharings.put(((SocketIORemotingClient) activeConnections.get(client)).getSocketIOClient(), client);
        });
        socketIOServer.addEventListener("stoptbroadcast", String.class, (client, data, ackSender) -> {
            activeConnections.get(client).stopSharing();
            activeSharings.remove(((SocketIORemotingClient) activeConnections.get(client)).getSocketIOClient());
        });

        socketIOServer.addEventListener("showmessage", TextMessage.class,
                new RemoteOperationDataListener<TextMessage>(this, RemotingClient::sendMessage));

        socketIOServer.addEventListener("requestreload", TextMessage.class,
                new RemoteOperationDataListener<>(this, (client, data1) -> {client.refresh();}));

        socketIOServer.addEventListener("screendata", String.class,
                (client, data, ackSender) -> {
                    activeSharings.get(client).sendEvent("screen",
                            "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://192.168.10.241:8081/web/css/leaforgchart.css\"><link type=\"text/css\" rel=\"stylesheet\" href=\"http://192.168.10.241:8081/web/css/leafgwt.css\">"
                                    .concat(data));
                });
    }
}
