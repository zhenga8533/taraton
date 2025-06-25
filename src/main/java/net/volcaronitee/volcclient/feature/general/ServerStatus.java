package net.volcaronitee.volcclient.feature.general;

public class ServerStatus {
    public final static ServerStatus INSTANCE = new ServerStatus();

    private int ping = 0;
    private int fps = 0;
    private int tps = 0;
    private int cps = 0;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private int angle = 0;
    private float day = 0.0f;
}
