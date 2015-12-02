package client;

/**
 * Created by Trevor on 10/22/15.
 */
import com.ndsucsci.clientservermessages.DataMessage;
import com.ndsucsci.clientservermessages.DownloadFileRequest;
import com.ndsucsci.clientservermessages.DownloadFileResponse;

import java.io.*;
import java.net.Socket;

public class ClientSendFileThread extends Thread {
    Socket peerSocket = null;
    public ClientSendFileThread(Socket peerSocket) {
        this.peerSocket = peerSocket;
    }

    public void run() {
        try {
            InputStream is = peerSocket.getInputStream();
            OutputStream os = peerSocket.getOutputStream();

            //read for filename
            DownloadFileRequest dfr = new DownloadFileRequest();
            dfr.getBytesFromInput(is);

            System.out.println(dfr.getFileName());

            File sendFile = new File("share/" + dfr.getFileName());
            byte[] message;
            if(sendFile.exists()) {
                System.out.println("Found File");
                InputStream fileStream = new FileInputStream(sendFile);

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = fileStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();

                message = DownloadFileResponse.createMessage(true, buffer.toByteArray());
                System.out.println("File sent.");
            } else {
                System.out.println("File Not Found");
                message = DownloadFileResponse.createMessage(false, new byte[0]);
            }

            os.write(message, 0, message.length);

            peerSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
