package de.deleteboys.serverapi.main;

import de.deleteboys.serverapi.methods.Logger;
import de.deleteboys.serverapi.methods.Methods;
import de.deleteboys.serverapi.packetsystem.PacketManager;
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

    protected static Logger logger;
    private boolean running = true;

    private static String logPath = "log/";

    public ServerApi(int port) {
        this.port = port;
        methods = new Methods();
        socketManager = new SocketManager();
        packetManager = new PacketManager();
        packetManager.init();
        logger = new Logger();
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

    public static void saveCurrentLog() {
        logger.saveLog();
    }

    public static String getLogPath() {
        return logPath;
    }

    public static void setLogPath(String logPath) {
        ServerApi.logPath = logPath;
    }

    public static Methods getMethods() {
        return methods;
    }

    public static SocketManager getSocketManager() {
        return socketManager;
    }

    public static PacketManager getPacketManager() {
        return packetManager;
    }
}
