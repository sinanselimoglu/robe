package io.robe.auth;

public interface ServiceEntry {
    public enum Method {
        GET,
        PUT,
        POST,
        DELETE,
        OPTIONS;
    }
    public String getPath();

    public Method getMethod();
}
