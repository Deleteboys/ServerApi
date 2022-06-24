package de.deleteboys.serverapi.packetsystem.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.deleteboys.serverapi.main.ServerApi;
import de.deleteboys.serverapi.packetsystem.Packet;
import de.deleteboys.serverapi.packetsystem.PacketSplitType;
import de.deleteboys.serverapi.sockets.SocketUser;

public class SendSplitPacket extends Packet {

    public String uuid;
    public int index;
    public int size;
    public String originalPacket;
    public PacketSplitType packetSplitType;
    public String[] packets;

    public SendSplitPacket() {
        super("SplitPacket");
    }

    @Override
    public void read(SocketUser s, JsonObject jsonObject) {
        if (jsonObject.has("packetSplitType")) {
            PacketSplitType splitType = PacketSplitType.valueOf(jsonObject.get("packetSplitType").getAsString());
            if (splitType == PacketSplitType.DEFAULT) {
                if (jsonObject.has("id") && jsonObject.has("index") && jsonObject.has("size") && jsonObject.has("originalPacket")) {
                    String id = jsonObject.get("id").getAsString();
                    int index = jsonObject.get("index").getAsInt();
                    int size = jsonObject.get("size").getAsInt();
                    String originalPacket = jsonObject.get("originalPacket").getAsString();
                    if (ServerApi.getServerApi().getMethods().getSplitPacket().containsKey(id)) {
                        String oldInput = ServerApi.getServerApi().getMethods().getSplitPacket().get(id);
                        String newInput = s.getRsa().decrypt(originalPacket);
                        ServerApi.getServerApi().getMethods().getSplitPacket().put(id, oldInput + newInput);
                        if (index == size) {
                            ServerApi.getServerApi().getMethods().handelPacketInput(ServerApi.getServerApi().getMethods().getSplitPacket().get(id), s.getSocket());
                            ServerApi.getServerApi().getMethods().getSplitPacket().remove(id);
                        }
                    } else {
                        String newInput = s.getRsa().decrypt(originalPacket);
                        ServerApi.getServerApi().getMethods().getSplitPacket().put(id, newInput);
                    }
                }
            } else if (splitType == PacketSplitType.ONELARGE) {
                if (jsonObject.has("id") && jsonObject.has("packetList") && jsonObject.has("packetSplitType")) {
                    JsonArray jsonArray = jsonObject.get("packetList").getAsJsonArray();
                    StringBuilder fixPacket = new StringBuilder();
                    for (JsonElement jsonElement : jsonArray) {
                        String decryptedString = s.getRsa().decrypt(jsonElement.getAsString());
                        fixPacket.append(decryptedString);
                    }
                    ServerApi.getServerApi().getMethods().handelPacketInput(fixPacket.toString(), s.getSocket());
                }
            }
        }
    }

    @Override
    public JsonObject write(SocketUser s) {
        JsonObject packet = createBasePacket();
        if (ServerApi.getServerApi().getPacketSplitType() == PacketSplitType.DEFAULT) {
            packet.addProperty("id", uuid);
            packet.addProperty("index", index);
            packet.addProperty("size", size);
            packet.addProperty("originalPacket", originalPacket);
            packet.addProperty("PacketSplitType", String.valueOf(packetSplitType));
        } else if (ServerApi.getServerApi().getPacketSplitType() == PacketSplitType.ONELARGE) {
            packet.addProperty("id", uuid);
            JsonArray jsonArray = new JsonArray();
            for (String p : packets) {
                jsonArray.add(p);
            }
            packet.add("packetList", jsonArray);
            packet.addProperty("PacketSplitType", String.valueOf(packetSplitType));
        }
        return packet;
    }

    public SendSplitPacket input(String uuid, int index, int size, String originalPacket, PacketSplitType splitType) {
        this.uuid = uuid;
        this.index = index;
        this.size = size;
        this.originalPacket = originalPacket;
        this.packetSplitType = splitType;
        return this;
    }

    public SendSplitPacket input(String uuid, PacketSplitType splitType, String[] packets) {
        this.uuid = uuid;
        this.packetSplitType = splitType;
        this.packets = packets;
        return this;
    }
}
