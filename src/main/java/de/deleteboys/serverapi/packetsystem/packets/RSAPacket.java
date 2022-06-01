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
    public JsonObject write(SocketUser s) {
        JsonObject jsonObject = createBasePacket();
        jsonObject.addProperty("publicKey", key);
        return jsonObject;
    }

    public RSAPacket init(String key) {
        this.key = key;
        return this;
    }

}
