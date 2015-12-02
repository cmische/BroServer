package clientservermessages;

/**
 * Created by closestudios on 10/10/15.
 */
public class UpdateFilesResponse extends DataMessage {


    public UpdateFilesResponse() {
        super();
    }

    public boolean getResult() {
        return getDataBytes()[0] == (byte)1;
    }

    public static byte[] createMessage(boolean success) {
        return DataMessage.createMessage(new byte[] {(byte)(success ? 1 : 0)});
    }

}
