package de.deleteboys.serverapi.packetsystem;

import com.google.gson.JsonObject;
import de.deleteboys.serverapi.sockets.SocketUser;

public abstract class Packet {

    public String packetName;

    public abstract void read(SocketUser s, JsonObject jsonObject);

    public abstract void write(SocketUser s);

    public String getPacketName() {
        return packetName;
    }

    public Packet(String packetName) {
        this.packetName = packetName;
    }
}
