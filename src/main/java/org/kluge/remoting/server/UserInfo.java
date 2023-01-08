package org.kluge.remoting.server;

import lombok.Data;

/** Created by giko on 1/12/15. */
@Data
public class UserInfo {
  private String name;
  private String location;
  private String fullLocation;
  private Integer mouseX;
  private Integer mouseY;
  private Integer scrollX;
  private Integer scrollY;

  private Boolean isActive;
  private Long ping;

  public Integer getMouseXAbsolute() {
    return mouseX + scrollX;
  }

  public Integer getMouseYAbsolute() {
    return mouseY + scrollY;
  }
}
