package clientservermessages;

/**
 * Created by Trevor on 10/23/15.
 */
public class DownloadFileRequest extends DataMessage {
    public DownloadFileRequest() {
        super();
    }

    public String getFileName() {
        return new String(getDataBytes());
    }

    public static byte[] createMessage(String fileName) {
        return DataMessage.createMessage(fileName.getBytes());
    }
}
