package de.deleteboys.serverapi.sockets;

import com.google.gson.JsonObject;
import de.deleteboys.serverapi.eventsystem.events.SocketDisconnectEvent;
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
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        rsa = new RSA();
                        ServerApi.getServerApi().getMethods().socketConnected(ServerApi.getServerApi().getSocketManager().getSocketUser(socket));
                        while (true) {
                            String line = reader.readLine();
                            if (line != null) {
                                if (ServerApi.getServerApi().getMethods().isJson(line)) {
                                    try {
                                        Logger.logPacketsGet(socket.getInetAddress() + " " + line);
                                        ServerApi.getServerApi().getMethods().handelPacketInput(line,socket);
                                    } catch (Exception e) {
                                        ServerApi.getServerApi().getEventManager().fireEvent(new SocketDisconnectEvent(ServerApi.getServerApi().getSocketManager().getSocketUser(socket)));
                                        stop();
                                    }
                                } else {
                                    String decryptedString = rsa.decrypt(line);
                                    Logger.logPacketsGet(socket.getInetAddress() + " Encrypted: " + line + " Decrypted: " + decryptedString);
                                    ServerApi.getServerApi().getMethods().handelPacketInput(decryptedString,socket);
                                }
                            } else {
                                ServerApi.getServerApi().getEventManager().fireEvent(new SocketDisconnectEvent(ServerApi.getServerApi().getSocketManager().getSocketUser(socket)));
                                stop();
                            }
                        }
                    } catch (SocketException e) {
                        ServerApi.getServerApi().getEventManager().fireEvent(new SocketDisconnectEvent(ServerApi.getServerApi().getSocketManager().getSocketUser(socket)));
                        stop();
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
