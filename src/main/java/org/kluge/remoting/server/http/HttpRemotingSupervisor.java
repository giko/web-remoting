package org.kluge.remoting.server.http;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.StringUtil;
import org.kluge.remoting.server.AbstractRemotingSupervisor;
import org.kluge.remoting.server.RemotingServer;
import org.kluge.remoting.server.TextMessage;
import org.kluge.remoting.server.UserInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Created by giko on 1/20/15.
 */
public class HttpRemotingSupervisor extends AbstractRemotingSupervisor<String> {
    protected RemotingServer<String> server;
    protected Server httpServer;

    public HttpRemotingSupervisor(Server httpServer, RemotingServer<String> server) {
        this.httpServer = httpServer;
        httpServer.setHandler(new AbstractHandler() {
            @Override
            public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse) throws IOException, ServletException {
                server.getRemotingClients().stream()
                        .filter(stringRemotingClient ->
                                stringRemotingClient.getInfo().orElseGet(UserInfo::new).getLocation()
                                        .startsWith(httpServletRequest.getParameter("location")))
                        .collect(Collectors.toList())
                        .forEach(stringRemotingClient -> {
                            if (!StringUtil.isBlank(request.getParameter("countdown"))) {
                                stringRemotingClient.displayCountDown(Long.valueOf(request.getParameter("countdown")));
                            } else {
                                stringRemotingClient.sendMessage(
                                        new TextMessage(request.getParameter("message"), request.getParameter("title"), "info"));
                            }
                        });
            }
        });
    }

    @Override
    public void send(String data) {

    }
}
