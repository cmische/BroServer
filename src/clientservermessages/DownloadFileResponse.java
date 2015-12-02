package clientservermessages;

/**
 * Created by Trevor on 10/23/15.
 */
public class DownloadFileResponse extends DataMessage {
    public DownloadFileResponse() {
        super();
    }

    public boolean foundFile() {
        return getDataBytes()[0] == 1;
    }

    public byte[] getFile() {
        byte[] fileData = new byte[getDataBytes().length-1];
        for(int i=0;i<fileData.length;i++) {
            fileData[i] = getDataBytes()[i + 1];
        }
        return fileData;
    }

    public static byte[] createMessage(boolean found, byte[] data) {
        byte[] allData = new byte[data.length + 1];
        allData[0] = (byte)(found ? 1 : 0);
        for(int i=0;i<data.length;i++) {
            allData[i + 1] = data[i];
        }
        return DataMessage.createMessage(allData);
    }
}
