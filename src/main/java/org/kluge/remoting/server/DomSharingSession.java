package org.kluge.remoting.server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/** Created by giko on 1/16/15. */
public class DomSharingSession extends AbstractSharingSession<String> {
  public DomSharingSession(RemotingClient<String> client) {
    super(client);
  }

  @Override
  protected String transform(String data) {
    Document document = Jsoup.parse(data);
    document.outputSettings().prettyPrint(false);

    var userInfo = client.getInfo();
    if (userInfo != null) {
      document
          .select("head")
          .append("<base href=\"" + userInfo.getLocation() + "/\" target=\"_blank\">");
      document
          .select("body")
          .append(
              "<img id=\"remoting-arrow\" style=\"z-index:2000;position: absolute; left:"
                  + (userInfo.getMouseXAbsolute())
                  + "px;top:"
                  + userInfo.getMouseYAbsolute()
                  + "px;\""
                  + " src=\"https://github.com/giko/web-remoting/raw/master/clientlib/src/cursor.png\""
                  + " width=\"11px\" id=\"pointer\">");
      document
          .select("[href], [src]")
          .forEach(
              element -> {
                if (!element.attr("href").startsWith("http")
                    && !element.attr("href").startsWith("data:image")) {
                  element.attr("href", userInfo.getLocation() + "/" + element.attr("href"));
                }
                if (!element.attr("src").startsWith("http")
                    && !element.attr("src").startsWith("data:image")) {
                  element.attr("src", userInfo.getLocation() + "/" + element.attr("src"));
                }
              });
    }
    document.select("iframe").remove();
    document.select("script").remove();
    return document.outerHtml();
  }
}
