package org.kluge.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.kluge.ClientInfo;
import org.kluge.server.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by giko on 12/30/14.
 */
public class SocketIORemotingServer implements RemotingServer<String> {
    public static int clientsCounter = 0;

    protected Map<RemotingSupervisor<String>, RemotingClient> activeConnections = new HashMap<>();
    protected Map<RemotingClient, SharingSession<String>> activeSharings = new HashMap<>();
    protected Map<SocketIOClient, RemotingSupervisor<String>> supervisors = new WeakHashMap<>();
    protected Map<SocketIOClient, RemotingClient<String>> clients = new WeakHashMap<>();

    protected  SocketIOServer socketIOServer;
    
    private List<ClientInfo> getClientInfos() {
        return clients.values().stream().map(ClientInfo::new).collect(Collectors.toList());
    }

    public SocketIORemotingServer(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        socketIOServer.addDisconnectListener(client -> {
            supervisors.remove(client);
            clients.remove(client);
        });
        
        socketIOServer.addEventListener("supervisor", LoginRequest.class, (client1, data2, ackSender1) -> {
            supervisors.put(client1, new SocketIORemotingSupervisor(client1));
        });
        socketIOServer.addEventListener("client", LoginRequest.class, (client1, data2, ackSender1) -> {
            clients.put(client1, new SocketIORemotingClient(client1));
        });

        socketIOServer.addEventListener("remote", String.class, (client, data, ackSender) -> {
            RemotingSupervisor<String> supervisor = supervisors.get(client);
            RemotingClient<String> remotingClient = clients.get(socketIOServer.getClient(UUID.fromString(data)));
            
            supervisor.connect(remotingClient);
            
            TextMessage greetingMessage = new TextMessage();
            greetingMessage.setTitle("Connected");
            greetingMessage.setMessage("Administrator connected");
            greetingMessage.setType("info");
            remotingClient.sendMessage(greetingMessage);

            activeConnections.put(supervisor, remotingClient);
        });
        socketIOServer.addEventListener("watch", Void.class, (client, data, ackSender) -> {
            RemotingSupervisor<String> supervisor = supervisors.get(client);
            supervisor.getConnection().addSupervisor(supervisor);
        });
        socketIOServer.addEventListener("unwatch", Void.class, (client, data, ackSender) -> {
            SharingSession<String> session = activeSharings.get(activeConnections.get(supervisors.get(client)));
            if (session == null){
                throw new IllegalStateException("No session found for unwatch operation");
            }
            
            
        });

        socketIOServer.addEventListener("showmessage", TextMessage.class,
                createRemoteOperationListener(RemotingClient::sendMessage));

        socketIOServer.addEventListener("requestreload", Void.class,
                createRemoteOperationListener((client, data1) -> client.refresh()));

        socketIOServer.addEventListener("screendata", String.class,
                (client, data, ackSender) -> {
                    activeSharings.get(client).broadcast("<link type=\"text/css\" rel=\"stylesheet\" href=\"http://192.168.10.241:8081/web/css/leaforgchart.css\"><link type=\"text/css\" rel=\"stylesheet\" href=\"http://192.168.10.241:8081/web/css/leafgwt.css\">"
                                    .concat(data));
                });
    }
    
    <T> RemoteOperationDataListener<T> createRemoteOperationListener(RemoteOperation<T> operation){
        return new RemoteOperationDataListener<>(this, operation);
    }

    @Override public Set<RemotingClient> getRemotingClients() {
        return new LinkedHashSet<>(clients.values());
    }

    @Override public Set<RemotingSupervisor<String>> getSupervisors() {
        return new LinkedHashSet<>(supervisors.values());
    }

    @Override public Set<SharingSession<String>> getSessions() {
        return new LinkedHashSet<>(activeSharings.values());
    }

    @Override public void addToSession(RemotingSupervisor<String> supervisor, RemotingClient client) {
        activeSharings.get(client).addSupervisor(supervisor);
    }

    @Override public void addRemotingClient(RemotingClient client) {

    }
}
