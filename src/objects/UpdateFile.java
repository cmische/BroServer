package objects;

import com.ndsucsci.clientservermessages.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/10/15.
 */
public class UpdateFile {
    public String filename;
    public String filesize;
    public boolean add;
    public UpdateFile(String filename, String filesize, boolean add) {
        this.filename = filename;
        this.filesize = filesize;
        this.add = add;
    }
    public UpdateFile(byte[] data) {

        ArrayList<byte[]> blocks = DataMessage.getBlocks(data);
        filename = new String(blocks.get(0));
        filesize = new String(blocks.get(1));
        add = blocks.get(2)[0] == (byte)1;

    }
    public byte[] toBytes() throws IOException {
        ArrayList<byte[]> blocks = new ArrayList<>();
        blocks.add(filename.getBytes());
        blocks.add(filesize.getBytes());
        blocks.add(new byte[] {(byte)(add ? 1 : 0)});
        return DataMessage.createBlocks(blocks);
    }
}