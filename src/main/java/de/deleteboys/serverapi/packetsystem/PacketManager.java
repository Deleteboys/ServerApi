package de.deleteboys.serverapi.packetsystem;

import com.google.gson.JsonObject;
import de.deleteboys.serverapi.main.ServerApi;
import de.deleteboys.serverapi.packetsystem.packets.RSAPacket;
import de.deleteboys.serverapi.packetsystem.packets.SendSplitPacket;
import de.deleteboys.serverapi.sockets.SocketUser;

import java.util.ArrayList;
import java.util.UUID;

public class PacketManager {

    private ArrayList<Packet> packets = new ArrayList<>();

    public void init() {
        registerPackets(RSAPacket.class);
        registerPackets(SendSplitPacket.class);
    }

    public Packet getPacketByName(String name) {
        for (Packet packet : packets) {
            if (packet.getPacketName().equals(name)) {
                return packet;
            }
        }
        return null;
    }

    public void registerPackets(Class<? extends Packet> clazz) {
        try {
            Packet packet = clazz.newInstance();
            if (!packets.contains(packet)) {
                packets.add(packet);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet packet, SocketUser s) {
        JsonObject jsonPacket = packet.write(s);
        String packetAsString = ServerApi.getMethods().gson.toJson(jsonPacket);
        if (packet instanceof SendSplitPacket) {
            ServerApi.getMethods().sendSplitPacket(packet.write(s),s);
            return;
        }
        if (s.getClientPublicKey() != null) {
            if (ServerApi.getMethods().getNumberOfChars(packetAsString) > 117) {
                String[] splitList = packetAsString.split("(?<=\\G.{" + 117 + "})");
                String uuid = UUID.randomUUID().toString();
                for (int i = 0; i < splitList.length; i++) {
                    try {
                        Thread.sleep(100);
                        sendPacket(new SendSplitPacket().input(uuid, i + 1, splitList.length, splitList[i]), s);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return;
            }
            ServerApi.getMethods().encryptAndSendPacket(s, jsonPacket);
        } else {
            ServerApi.getMethods().sendJson(s.getSocket(), jsonPacket);
        }
    }

    public ArrayList<Packet> getPackets() {
        return packets;
    }
}
