package de.deleteboys.serverapi.sockets;

import com.google.gson.JsonObject;
import de.deleteboys.serverapi.main.ServerApi;
import de.deleteboys.serverapi.methods.Logger;
import de.deleteboys.serverapi.methods.RSA;
import de.deleteboys.serverapi.packetsystem.Packet;
import de.deleteboys.serverapi.packetsystem.packets.RSAPacket;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.PublicKey;
import java.util.Base64;

public class SocketUser {

    private Socket socket;
    Thread thread;
    private RSA rsa;

    private PublicKey clientPublicKey;

    public SocketUser(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void handleInput() {
        ServerApi.getMethods().socketConnected(socket);
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        rsa = new RSA();
                        String publicKey = Base64.getEncoder().encodeToString(rsa.publicKey.getEncoded());
                        ServerApi.getPacketManager().sendPacket(new RSAPacket().init(publicKey), ServerApi.getSocketManager().getSocketUser(socket));
                        while (true) {
                            String line = reader.readLine();
                            if (line != null) {
                                if (ServerApi.getMethods().isJson(line)) {
                                    try {
                                        JsonObject jsonObject = ServerApi.getMethods().gson.fromJson(line, JsonObject.class);
                                        Logger.logPacketsGet(socket.getInputStream() + " " + line);
                                        if (jsonObject.has("packet")) {
                                            for (Packet packet : ServerApi.getPacketManager().getPackets()) {
                                                if (packet.getPacketName().equals(jsonObject.get("packet").getAsString())) {
                                                    packet.read(ServerApi.getSocketManager().getSocketUser(socket), jsonObject);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        ServerApi.getSocketManager().removeSocket(ServerApi.getSocketManager().getSocketUser(socket));
                                        ServerApi.getMethods().socketDisconnected(socket);
                                        socket.close();
                                        stop();
                                    }
                                } else {
                                    String decryptedString = rsa.decrypt(line);
                                    Logger.logPacketsGet(socket.getInetAddress() + " Encrypted: " + line + " Decrypted: " + decryptedString);
                                    if (ServerApi.getMethods().isJson(decryptedString)) {
                                        JsonObject jsonObject = ServerApi.getMethods().gson.fromJson(decryptedString, JsonObject.class);
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
                        }
                    } catch (SocketException e) {
                        ServerApi.getSocketManager().removeSocket(ServerApi.getSocketManager().getSocketUser(socket));
                        ServerApi.getMethods().socketDisconnected(socket);
                        socket.close();
                        stop();
                        Logger.error(e.getMessage());
                    }
                } catch (IOException e) {
                    Logger.error(e.getMessage());
                }
                super.run();
            }
        };
        thread.start();
    }

    public PublicKey getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(PublicKey clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public RSA getRsa() {
        return rsa;
    }
}
