package de.deleteboys.serverapi.packetsystem.packets;

import com.google.gson.JsonObject;
import de.deleteboys.serverapi.main.ServerApi;
import de.deleteboys.serverapi.packetsystem.Packet;
import de.deleteboys.serverapi.sockets.SocketUser;

public class RSAPacket extends Packet {

    public String key;

    public RSAPacket() {
        super("RSAPacket");
    }

    @Override
    public void read(SocketUser s, JsonObject jsonObject) {
        if(jsonObject.has("publicKey")) {
            s.setClientPublicKey(ServerApi.getMethods().stringToPublicKey(jsonObject.get("publicKey").getAsString()));
        }
    }

    @Override
    public void write(SocketUser s) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("packet", this.getPacketName());
        jsonObject.addProperty("publicKey", key);
        ServerApi.getMethods().sendJson(s.getSocket(),jsonObject);
    }

    public RSAPacket init(String key) {
        this.key = key;
        return this;
    }

}
