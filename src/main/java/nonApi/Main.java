package nonApi;

import de.deleteboys.serverapi.main.ServerApi;

public class Main {

    public static void main(String[] args) {
        ServerApi serverApi = new ServerApi(8877);
        serverApi.startServer();
        try {
            Thread.sleep(1000);
            ServerApi.saveCurrentLog();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
