package networking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class Bro {
    public int id;
    public String broName;
    public int totalTimeSecs;
    public BroLocation location;

    public Bro(int id, String broName, int totalTimeSecs, BroLocation broLocation) {
        this.id = id;
        this.broName = broName;
        this.totalTimeSecs = totalTimeSecs;
        this.location = broLocation;
    }

    public Bro(byte[] bytes) {
        ArrayList<byte[]> broBlocks = DataMessage.getBlocks(bytes);

        id = Integer.parseInt(new String(broBlocks.get(0)));
        broName = new String(broBlocks.get(1));
        totalTimeSecs = Integer.parseInt(new String(broBlocks.get(2)));
        location = new BroLocation(broBlocks.get(3));

    }

    public byte[] getBytes() throws IOException {
        ArrayList<byte[]> broBlocks = new ArrayList<>();

        broBlocks.add((id + "").getBytes());
        broBlocks.add(broName.getBytes());
        broBlocks.add((totalTimeSecs + "").getBytes());
        broBlocks.add(location.getBytes());

        return DataMessage.createBlocks(broBlocks);
    }

}
