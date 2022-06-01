package de.deleteboys.serverapi.eventsystem.events;

import de.deleteboys.serverapi.eventsystem.Event;

import java.net.Socket;

public class SocketDisconnectEvent extends Event {
    public Socket socket;

    public SocketDisconnectEvent(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
