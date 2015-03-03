package org.kluge.remoting.server;

import java.util.Objects;

/**
 * Created by giko on 1/12/15.
 */
public class UserInfo {
    private String name;
    private String location;
    private Integer x;
    private Integer y;
    private Boolean isActive;

    public Boolean isActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(name, userInfo.name) &&
                Objects.equals(location, userInfo.location) &&
                Objects.equals(isActive, userInfo.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location, isActive);
    }
}
