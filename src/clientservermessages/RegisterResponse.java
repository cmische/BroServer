package clientservermessages;

/**
 * Created by closestudios on 10/10/15.
 */
public class RegisterResponse extends DataMessage {

    public RegisterResponse() {
        super();
    }

    public String getComputerUUID() {
        return new String(getDataBytes());
    }

    public static byte[] createMessage(String uuid) {
        return DataMessage.createMessage(uuid.getBytes());
    }

}
