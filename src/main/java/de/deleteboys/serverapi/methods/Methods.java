package de.deleteboys.serverapi.methods;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.deleteboys.serverapi.eventsystem.EventManager;
import de.deleteboys.serverapi.eventsystem.events.SocketConnectEvent;
import de.deleteboys.serverapi.main.ServerApi;
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
import java.util.Base64;

public class Methods {

    public Gson gson = new Gson();

    public void socketDisconnected(Socket socket) {
        Logger.info("Socket Disconnected " + socket.getInetAddress());
    }

    public void socketConnected(SocketUser socketUser) {
        ServerApi.getEventManager().fireEvent(new SocketConnectEvent(socketUser));
        Logger.info("Socket Connected " + socketUser.getSocket().getInetAddress());
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

}
