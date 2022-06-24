package de.deleteboys.serverapi.main;

import de.deleteboys.serverapi.eventsystem.EventManager;
import de.deleteboys.serverapi.methods.Logger;
import de.deleteboys.serverapi.methods.Methods;
import de.deleteboys.serverapi.packetsystem.PacketManager;
import de.deleteboys.serverapi.packetsystem.PacketSplitType;
import de.deleteboys.serverapi.sockets.SocketManager;
import de.deleteboys.serverapi.sockets.SocketUser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApi {

    private int port;
    private static Methods methods;
    private static SocketManager socketManager;
    private static PacketManager packetManager;
    private static EventManager eventManager;
    protected static Logger logger;
    private static boolean logInFile = true;
    private static boolean consoleLog = true;
    private boolean running = true;
    private PacketSplitType packetSplitType = PacketSplitType.DEFAULT;

    private static ServerApi serverApi;
    private static String logPath = "log/";

    public ServerApi(int port) {
        this.port = port;
        methods = new Methods();
        socketManager = new SocketManager();
        packetManager = new PacketManager();
        eventManager = new EventManager();
        eventManager.init();
        packetManager.init();
        logger = new Logger();
        serverApi = this;
    }

    public void startServer() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Logger.info("Starting Server...");
                    ServerSocket serverSocket = new ServerSocket(port);
                    Logger.info("Server started");
                    while (running) {
                        Socket s = serverSocket.accept();
                        SocketUser socketUser = socketManager.registerSocket(s);
                        socketUser.handleInput();
                    }
                } catch (IOException e) {
                    Logger.error("Can not start the server");
                    Logger.error(e.getMessage());
                }
                super.run();
            }
        };
        thread.start();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void saveCurrentLog() {
        logger.saveLog();
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        ServerApi.logPath = logPath;
    }

    public Methods getMethods() {
        return methods;
    }

    public SocketManager getSocketManager() {
        return socketManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public void setPacketLog(boolean state) {
        logger.setPacketLog(state);
    }

    public boolean isPacketLog() {
        return logger.isPacketLog();
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public boolean isLogInFile() {
        return logInFile;
    }

    public void setLogInFile(boolean logInFile) {
        ServerApi.logInFile = logInFile;
    }

    public boolean isConsoleLog() {
        return consoleLog;
    }

    public void setConsoleLog(boolean consoleLog) {
        ServerApi.consoleLog = consoleLog;
    }

    public static ServerApi getServerApi() {
        return serverApi;
    }

    public PacketSplitType getPacketSplitType() {
        return packetSplitType;
    }

    public void setPacketSplitType(PacketSplitType packetSplitType) {
        this.packetSplitType = packetSplitType;
    }
}
