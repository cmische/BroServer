package client;

import java.net.ServerSocket;

/**
 * Created by Trevor on 10/21/15.
 */
import java.net.Socket;

public class ClientMainThread extends Thread {

    private int portNo;
    ServerSocket clientSocket = null;
    boolean keepRunning = true;

    public ClientMainThread(int portNo) {
        this.portNo = portNo;
    }

    public void run() {
        try {
            clientSocket = new ServerSocket(portNo);
            System.out.println("Client listening for peers.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        while(keepRunning) {
            try {
                //listen for incoming connections
                Socket acceptedSocket = clientSocket.accept();
                System.out.println("Peer to Peer Connected.");
                new ClientSendFileThread(acceptedSocket).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
