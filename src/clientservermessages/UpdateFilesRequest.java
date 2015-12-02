package clientservermessages;

import com.ndsucsci.objects.UpdateFile;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/10/15.
 */
public class UpdateFilesRequest {

    ServerRequest serverRequest;
    ArrayList<UpdateFile> files;
    String computerUUID;

    public UpdateFilesRequest(ServerRequest request) {
        serverRequest = request;


        byte[] data = new byte[serverRequest.getDataBytes().length];
        for(int i=0;i<data.length;i++) {
            data[i] = serverRequest.getDataBytes()[i];
        }

        ArrayList<byte[]> blocks = DataMessage.getBlocks(data);
        computerUUID = new String (blocks.get(0));
        processFiles(blocks.get(1));

    }

    private void processFiles(byte[] data) {
        files = new ArrayList<>();

        ArrayList<byte[]> blocks = DataMessage.getBlocks(data);

        for(int i=0;i<blocks.size();i++) {
            files.add(new UpdateFile(blocks.get(i)));
        }

    }

    public ArrayList<UpdateFile> getUpdateFiles() {
        return files;
    }

    public String getComputerUUID() {
        return computerUUID;
    }

    public static byte[] createMessage(ArrayList<UpdateFile> files, String computerUUID) throws IOException {

        ArrayList<byte[]> updateBlocks = new ArrayList<>();

        ArrayList<byte[]> fileBlocks = new ArrayList<>();

        for(int i=0;i<files.size();i++) {
            fileBlocks.add(files.get(i).toBytes());
        }

        updateBlocks.add(computerUUID.getBytes());
        updateBlocks.add(DataMessage.createBlocks(fileBlocks));

        return ServerRequest.createMessage(DataMessage.createBlocks(updateBlocks), ServerRequest.ServerRequestType.UpdateList);
    }



}
