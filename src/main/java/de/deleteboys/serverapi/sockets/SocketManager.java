package de.deleteboys.serverapi.sockets;

import java.net.Socket;
import java.util.ArrayList;

public class SocketManager {

    private ArrayList<SocketUser> socketUsers = new ArrayList<>();

    public SocketUser registerSocket(Socket s) {
        SocketUser socketUser = new SocketUser(s);
        socketUsers.add(socketUser);
        return socketUser;
    }

    public SocketUser getSocketUser(Socket s) {
        for (SocketUser socketUser : socketUsers) {
            if(socketUser.getSocket().equals(s)) {
                return socketUser;
            }
        }
        return null;
    }

    public void removeSocket(SocketUser socketUser) {
        socketUsers.remove(socketUser);
    }

    public ArrayList<SocketUser> getSocketUsers() {
        return socketUsers;
    }
}
