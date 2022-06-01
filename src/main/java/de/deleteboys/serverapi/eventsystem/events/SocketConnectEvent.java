package de.deleteboys.serverapi.eventsystem.events;

import de.deleteboys.serverapi.eventsystem.Event;
import de.deleteboys.serverapi.sockets.SocketUser;

import java.net.Socket;

public class SocketConnectEvent extends Event {

    public SocketUser socketUser;

    public SocketConnectEvent(SocketUser socketUser) {
        this.socketUser = socketUser;
    }

    public SocketUser getSocketUser() {
        return socketUser;
    }
}
