package client;

import com.ndsucsci.clientservermessages.UpdateFilesRequest;
import com.ndsucsci.clientservermessages.UpdateFilesResponse;
import com.ndsucsci.objects.UpdateFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/10/15.
 */
public class ClientUpdateFileThread extends Thread {

    Socket socket = null;
    String host;
    int port;
    ArrayList<UpdateFile> files;
    String computerUUID;
    UpdateFilesCallback callback;

    public ClientUpdateFileThread(String host, int port, ArrayList<UpdateFile> files, String computerUUID, UpdateFilesCallback callback) {
        super("ClientUpdateFileThread");
        this.host = host;
        this.port = port;
        this.files = files;
        this.computerUUID = computerUUID;
        this.callback = callback;
    }

    public interface UpdateFilesCallback {
        void onUpdate(boolean updated);
    }

    public void run() {

        // Print out what you are doing
        System.out.println("Update Files");

        try {

            socket = new Socket(host, port);

            OutputStream outToServer = socket.getOutputStream();
            InputStream inFromServer = socket.getInputStream();

            // Send Register Message
            outToServer.write(UpdateFilesRequest.createMessage(files, computerUUID));
            outToServer.flush();

            // Get response
            UpdateFilesResponse updateFilesResponse = new UpdateFilesResponse();
            updateFilesResponse.getBytesFromInput(inFromServer);

            callback.onUpdate(updateFilesResponse.getResult());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
