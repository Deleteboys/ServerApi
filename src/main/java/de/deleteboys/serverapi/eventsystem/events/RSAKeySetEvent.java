package de.deleteboys.serverapi.eventsystem.events;

import de.deleteboys.serverapi.eventsystem.Event;
import de.deleteboys.serverapi.sockets.SocketUser;

public class RSAKeySetEvent extends Event {

    public SocketUser socketUser;

    public RSAKeySetEvent(SocketUser socketUser) {
        this.socketUser = socketUser;
    }

    public SocketUser getSocketUser() {
        return socketUser;
    }
}
