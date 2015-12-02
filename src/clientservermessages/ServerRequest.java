package clientservermessages;

import com.ndsucsci.clientservermessages.DataMessage;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/9/15.
 */
public class ServerRequest extends DataMessage {

    public enum ServerRequestType {
        Register(0), Search(1), UpdateList(2);

        private final int value;
        private ServerRequestType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public ServerRequest() {
        super();
    }

    public ServerRequestType getRequestType() {
        if(!receivedRequest()) {
            return null;
        }

        if(getData().size() == 0) {
            return null;
        }

        return ServerRequestType.values()[getData().get(0)];
    }

    public static byte[] createMessage(byte[] data, ServerRequestType type) {
        byte[] message = new byte[data.length + 1];

        message[0] = (byte)type.getValue();

        for(int i=0;i<data.length;i++) {
            message[i+1] = data[i];
        }

        return DataMessage.createMessage(message);
    }

    @Override
    public byte[] getDataBytes() {
        byte[] data = new byte[super.getDataBytes().length - 1];

        for(int i = 0;i<super.getDataBytes().length-1;i++) {
            data[i] = super.getDataBytes()[i+1];
        }

        return data;
    }
}
