package de.deleteboys.serverapi.eventsystem.eventreaders;

import de.deleteboys.serverapi.eventsystem.Event;
import de.deleteboys.serverapi.eventsystem.EventReader;
import de.deleteboys.serverapi.eventsystem.events.SocketDisconnectEvent;
import de.deleteboys.serverapi.main.ServerApi;
import de.deleteboys.serverapi.methods.Logger;
import de.deleteboys.serverapi.sockets.SocketUser;

import java.io.IOException;
import java.net.Socket;

public class SocketDisconnectReader extends EventReader {

    @Override
    public void onEvent(Event event) {
        if (event instanceof SocketDisconnectEvent) {
            try {
                SocketUser socketUser = ((SocketDisconnectEvent) event).getSocketUser();
                ServerApi.getSocketManager().removeSocket(socketUser);
                ServerApi.getMethods().socketDisconnected(socketUser.getSocket());
                socketUser.getSocket().close();
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        }
    }
}
