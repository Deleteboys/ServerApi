package de.deleteboys.serverapi.methods;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.deleteboys.serverapi.eventsystem.EventManager;
import de.deleteboys.serverapi.eventsystem.events.SocketConnectEvent;
import de.deleteboys.serverapi.main.ServerApi;
import de.deleteboys.serverapi.packetsystem.Packet;
import de.deleteboys.serverapi.packetsystem.packets.RSAPacket;
import de.deleteboys.serverapi.sockets.SocketUser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Methods {

    public Gson gson = new Gson();

    private ConcurrentHashMap<String,String> splitPacket = new ConcurrentHashMap<>();
    public void socketDisconnected(Socket socket) {
        Logger.info("Socket Disconnected " + socket.getInetAddress());
    }

    public void socketConnected(SocketUser socketUser) {
        Logger.info("Socket Connected " + socketUser.getSocket().getInetAddress());
        String publicKey = Base64.getEncoder().encodeToString(socketUser.getRsa().publicKey.getEncoded());
        ServerApi.getPacketManager().sendPacket(new RSAPacket().init(publicKey), socketUser);
        ServerApi.getEventManager().fireEvent(new SocketConnectEvent(socketUser));
    }

    public PublicKey stringToPublicKey(String key) {
        try {
            byte[] data = Base64.getDecoder().decode((key.getBytes()));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            return fact.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSplitPacket(JsonObject jsonObject, SocketUser socketUser) {
        try {
            String originalPacket = jsonObject.get("originalPacket").getAsString();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketUser.getSocket().getOutputStream()));
            String encryptedPacket = socketUser.getRsa().encrypt(originalPacket, socketUser.getClientPublicKey());
            JsonObject newPacket = new JsonObject();
            newPacket.addProperty("packet",jsonObject.get("packet").getAsString());
            newPacket.addProperty("id",jsonObject.get("id").getAsString());
            newPacket.addProperty("index",jsonObject.get("index").getAsString());
            newPacket.addProperty("size",jsonObject.get("size").getAsString());
            newPacket.addProperty("originalPacket",encryptedPacket);
            String packetAsString = gson.toJson(newPacket);
            Logger.logPacketsSend(packetAsString);
            writer.write(packetAsString);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void encryptAndSendPacket(SocketUser socketUser,JsonObject jsonObject) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketUser.getSocket().getOutputStream()));
            String packet = gson.toJson(jsonObject);
            String encryptedPacket = socketUser.getRsa().encrypt(packet,socketUser.getClientPublicKey());
            Logger.logPacketsSend("Encrypted: " + encryptedPacket + " Decrypted:" + packet);
            writer.write(encryptedPacket);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isJson(String input) {
        try {
            JsonObject jsonObject = gson.fromJson(input, JsonObject.class);
            return jsonObject != null;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    public void sendJson(Socket s, JsonObject jsonObject) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            String packet = gson.toJson(jsonObject);
            Logger.logPacketsSend(packet);
            writer.write(packet);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConcurrentHashMap<String, String> getSplitPacket() {
        return splitPacket;
    }

    public int getNumberOfChars(String input) {
        int j = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != ' ') {
                j++;
            }
        }
        return j;
    }

    public void handelPacketInput(String input, Socket socket) {
        if (ServerApi.getMethods().isJson(input)) {
            JsonObject jsonObject = ServerApi.getMethods().gson.fromJson(input, JsonObject.class);
            if (jsonObject.has("packet")) {
                for (Packet packet : ServerApi.getPacketManager().getPackets()) {
                    if (packet.getPacketName().equals(jsonObject.get("packet").getAsString())) {
                        packet.read(ServerApi.getSocketManager().getSocketUser(socket), jsonObject);
                    }
                }
            }
        }
    }
}
