package de.deleteboys.serverapi.eventsystem.eventreaders;

import de.deleteboys.serverapi.eventsystem.Event;
import de.deleteboys.serverapi.eventsystem.EventReader;
import de.deleteboys.serverapi.eventsystem.events.SocketDisconnectEvent;
import de.deleteboys.serverapi.main.ServerApi;
import de.deleteboys.serverapi.methods.Logger;

import java.io.IOException;
import java.net.Socket;

public class SocketDisconnectReader extends EventReader {

    @Override
    public void onEvent(Event event) {
        if (event instanceof SocketDisconnectEvent) {
            try {
                Socket socket = ((SocketDisconnectEvent) event).getSocket();
                ServerApi.getSocketManager().removeSocket(ServerApi.getSocketManager().getSocketUser(socket));
                ServerApi.getMethods().socketDisconnected(socket);
                socket.close();
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        }
    }
}
