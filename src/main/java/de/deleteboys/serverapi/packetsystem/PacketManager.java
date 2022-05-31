package de.deleteboys.serverapi.packetsystem;

import de.deleteboys.serverapi.packetsystem.packets.RSAPacket;
import de.deleteboys.serverapi.sockets.SocketUser;

import java.util.ArrayList;

public class PacketManager {

    private ArrayList<Packet> packets = new ArrayList<>();

    public void init() {
        registerPackets(RSAPacket.class);
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
        packet.write(s);
    }

    public ArrayList<Packet> getPackets() {
        return packets;
    }
}
