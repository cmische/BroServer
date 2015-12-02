package objects;

import com.ndsucsci.clientservermessages.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/10/15.
 */
public class SearchResult {
    public String filename;
    public String filesize;
    public String ipAddress;
    public SearchResult() {

    }
    public SearchResult(String filename, String filesize, String ipAddress) {
        this.filename = filename;
        this.filesize = filesize;
        this.ipAddress = ipAddress;
    }
    public SearchResult(byte[] data) {
        ArrayList<byte[]> blocks = DataMessage.getBlocks(data);
        filename = new String(blocks.get(0));
        filesize = new String(blocks.get(1));
        ipAddress = new String(blocks.get(2));
    }

    public byte[] getBytes() throws IOException {
        ArrayList<byte[]> blocks = new ArrayList<>();
        blocks.add(filename.getBytes());
        blocks.add(filesize.getBytes());
        blocks.add(ipAddress.getBytes());
        return DataMessage.createBlocks(blocks);
    }
}