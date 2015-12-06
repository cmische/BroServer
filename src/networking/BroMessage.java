package networking;

import java.io.IOException;
import java.util.ArrayList;


public class BroMessage {

    public String messageTitle;
    public String messageDetails;
    public byte[] audioBytes;
    public String extension;

    public BroMessage() {

    }

    public BroMessage(String messageTitle, String messageDetails, byte[] audioBytes, String extension) {
        this.messageTitle = messageTitle;
        this.messageDetails = messageDetails;
        this.audioBytes = audioBytes;
        this.extension = extension;
    }

    public BroMessage(byte[] bytes) {
        ArrayList<byte[]> messageBlocks = DataMessage.getBlocks(bytes);

        messageTitle = new String(messageBlocks.get(0));
        messageDetails = new String(messageBlocks.get(1));
        audioBytes = messageBlocks.get(2);
        extension = new String(messageBlocks.get(3));

    }

    public byte[] getBytes() throws IOException {
        ArrayList<byte[]> messageBlocks = new ArrayList<>();

        messageBlocks.add((messageTitle).getBytes());
        messageBlocks.add((messageDetails).getBytes());
        messageBlocks.add(audioBytes);
        messageBlocks.add((extension).getBytes());

        return DataMessage.createBlocks(messageBlocks);
    }


}
