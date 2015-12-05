package networking;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class Bro implements Serializable {
    public int id;
    public String broName;
    public int totalTimeSecs;
    public BroLocation location = new BroLocation();
    public boolean recentlyNearBy;

    public Bro(String broName) {
        this.broName = broName;
        this.totalTimeSecs = 0;
        this.location = null;
        this.recentlyNearBy = false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.toString().equals(toString());
    }

    @Override
    public String toString() {
        return broName;
    }

    public Bro(byte[] bytes) {
        ArrayList<byte[]> broBlocks = DataMessage.getBlocks(bytes);

        id = Integer.parseInt(new String(broBlocks.get(0)));
        broName = new String(broBlocks.get(1));
        totalTimeSecs = Integer.parseInt(new String(broBlocks.get(2)));
        if(broBlocks.size() >= 4) {
            location = new BroLocation(broBlocks.get(3));
        }

    }

    public byte[] getBytes() throws IOException {
        ArrayList<byte[]> broBlocks = new ArrayList<>();

        broBlocks.add((id + "").getBytes());
        broBlocks.add(broName.getBytes());
        broBlocks.add((totalTimeSecs + "").getBytes());
        if(location != null) {
            broBlocks.add(location.getBytes());
        }

        return DataMessage.createBlocks(broBlocks);
    }

}
