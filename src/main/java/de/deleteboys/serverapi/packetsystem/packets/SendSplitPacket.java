package de.deleteboys.serverapi.packetsystem.packets;

import com.google.gson.JsonObject;
import de.deleteboys.serverapi.main.ServerApi;
import de.deleteboys.serverapi.packetsystem.Packet;
import de.deleteboys.serverapi.sockets.SocketUser;

public class SendSplitPacket extends Packet {

    public String uuid;
    public int index;
    public int size;
    public String originalPacket;

    public SendSplitPacket() {
        super("SplitPacket");
    }

    @Override
    public void read(SocketUser s, JsonObject jsonObject) {
        if(jsonObject.has("id") && jsonObject.has("index") && jsonObject.has("size") && jsonObject.has("originalPacket")) {
            String id = jsonObject.get("id").getAsString();
            int index = jsonObject.get("index").getAsInt();
            int size = jsonObject.get("size").getAsInt();
            String originalPacket = jsonObject.get("originalPacket").getAsString();
            if(ServerApi.getMethods().getSplitPacket().containsKey(id)) {
                String oldInput = ServerApi.getMethods().getSplitPacket().get(id);
                String newInput = s.getRsa().decrypt(originalPacket);
                ServerApi.getMethods().getSplitPacket().put(id,oldInput+newInput);
                if(index == size) {
                    ServerApi.getMethods().handelPacketInput(ServerApi.getMethods().getSplitPacket().get(id),s.getSocket());
                    ServerApi.getMethods().getSplitPacket().remove(id);
                }
            } else {
                String newInput = s.getRsa().decrypt(originalPacket);
                ServerApi.getMethods().getSplitPacket().put(id,newInput);
            }
        }
    }

    @Override
    public JsonObject write(SocketUser s) {
        JsonObject packet = createBasePacket();
        packet.addProperty("id",uuid);
        packet.addProperty("index",index);
        packet.addProperty("size",size);
        packet.addProperty("originalPacket",originalPacket);
        return packet;
    }

    public SendSplitPacket input(String uuid, int index, int size, String originalPacket) {
        this.uuid = uuid;
        this.index = index;
        this.size = size;
        this.originalPacket = originalPacket;
        return this;
    }
}
