package de.deleteboys.serverapi.eventsystem;

import de.deleteboys.serverapi.eventsystem.eventreaders.SocketDisconnectReader;

import java.util.ArrayList;

public class EventManager {

    private ArrayList<EventReader> eventReaders = new ArrayList<>();

    public void init() {
        registerEventReader(SocketDisconnectReader.class);
    }

    public void registerEventReader(Class<? extends EventReader> clazz) {
        try {
            EventReader eventReader = clazz.newInstance();
            if (!eventReaders.contains(eventReader)) {
                eventReaders.add(eventReader);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void fireEvent(Event event) {
        for (EventReader eventReader : eventReaders) {
            eventReader.onEvent(event);
        }
    }

    public void removeEventReader(EventReader eventReader) {
        eventReaders.remove(eventReader);
    }

    public ArrayList<EventReader> getEventReaders() {
        return eventReaders;
    }
}
