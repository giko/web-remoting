package org.kluge.remoting.server.http;

import de.hasait.clap.shadeddeps.oaclang3.ObjectUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.StringUtil;
import org.kluge.remoting.server.AbstractRemotingSupervisor;
import org.kluge.remoting.server.RemotingServer;
import org.kluge.remoting.server.TextMessage;

/**
 * Created by giko on 1/20/15.
 */
public class HttpRemotingSupervisor extends AbstractRemotingSupervisor<String> {
    protected final Server httpServer;
    protected RemotingServer<String> server;

    public HttpRemotingSupervisor(Server httpServer, RemotingServer<String> server) {
        this.httpServer = httpServer;
        httpServer.setHandler(new AbstractHandler() {
            @Override
            public void handle(String s, Request request, jakarta.servlet.http.HttpServletRequest httpServletRequest, jakarta.servlet.http.HttpServletResponse httpServletResponse) {
                server.getRemotingClients()
                        .stream()
                        .filter(client -> client.getInfo() != null)
                        .filter(stringRemotingClient ->
                                ObjectUtils.defaultIfNull(stringRemotingClient.getInfo().getLocation(), "")
                                        .startsWith(httpServletRequest.getParameter("location")))
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
