package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by closestudios on 10/9/15.
 */
public class ServerMainThread extends Thread {

    int serverPort;

    public ServerMainThread(int port) {
        super("ServerMainThread");
        serverPort = port;
    }


    public void run() {

        // Create Server Socket
        ServerSocket server = null;
        boolean serverRunning = true;

        try {
            server = new ServerSocket(serverPort);
            System.out.println("Started Server Listening to Port: " + serverPort);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + serverPort);
            System.exit(-1);
        }

        while (serverRunning) {
            Socket acceptedSocket = null;
            try {
                acceptedSocket = server.accept();
                new ServerProcessRequestThread(acceptedSocket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
