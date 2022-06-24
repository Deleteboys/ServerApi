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
    private int splitPacketDelay = 10;

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
        String packetAsString = ServerApi.getServerApi().getMethods().gson.toJson(jsonPacket);
        if (packet instanceof SendSplitPacket) {
            ServerApi.getServerApi().getMethods().sendSplitPacket(packet.write(s), s);
            return;
        }
        if (s.getClientPublicKey() != null) {
            if (packetAsString.getBytes().length > 117) {
                String[] splitList = packetAsString.split("(?<=\\G.{" + 100 + "})");
                String uuid = UUID.randomUUID().toString();
                if (ServerApi.getServerApi().getPacketSplitType() == PacketSplitType.DEFAULT) {
                    for (int i = 0; i < splitList.length; i++) {
                        try {
                            sendPacket(new SendSplitPacket().input(uuid, i + 1, splitList.length, splitList[i], ServerApi.getServerApi().getPacketSplitType()), s);
                            Thread.sleep(getSplitPacketDelay());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else if(ServerApi.getServerApi().getPacketSplitType() == PacketSplitType.ONELARGE) {
                    sendPacket(new SendSplitPacket().input(uuid, ServerApi.getServerApi().getPacketSplitType(), splitList), s);
                }
                return;
            }
            ServerApi.getServerApi().getMethods().encryptAndSendPacket(s, jsonPacket);
        } else {
            ServerApi.getServerApi().getMethods().sendJson(s.getSocket(), jsonPacket);
        }
    }

    public ArrayList<Packet> getPackets() {
        return packets;
    }

    public int getSplitPacketDelay() {
        return splitPacketDelay;
    }

    public void setSplitPacketDelay(int splitPacketDelay) {
        this.splitPacketDelay = splitPacketDelay;
    }
}
