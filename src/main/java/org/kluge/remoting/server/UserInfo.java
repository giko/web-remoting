package org.kluge.remoting.server;

/**
 * Created by giko on 1/12/15.
 */
public class UserInfo {
    private String name;
    private String location;
    private Integer x;
    private Integer y;

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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UserInfo userInfo = (UserInfo) o;

        if (!location.equals(userInfo.location))
            return false;
        if (!name.equals(userInfo.name))
            return false;
        if (x != null ? !x.equals(userInfo.x) : userInfo.x != null)
            return false;
        if (y != null ? !y.equals(userInfo.y) : userInfo.y != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + (x != null ? x.hashCode() : 0);
        result = 31 * result + (y != null ? y.hashCode() : 0);
        return result;
    }
}
