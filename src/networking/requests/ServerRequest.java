package networking.requests;

import networking.DataMessage;

public class ServerRequest extends DataMessage {


    byte[] cachedRequestBytes;

    public enum ServerRequestType {
        SignUp(0), SignInCreds(1), GetBros(2), AddBro(3), UpdateLocation(4), RemoveBro(5),
        BlockBro(6), SignInToken(7), SendBroMessage(8), GetBroMessage(9);

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

        if(getDataBytes().length == 0) {
            return null;
        }

        return ServerRequestType.values()[getDataBytes()[0]];
    }

    public static byte[] createMessage(byte[] data, ServerRequestType type) {
        byte[] message = new byte[data.length + 1];

        message[0] = (byte)type.getValue();

        for(int i=0;i<data.length;i++) {
            message[i+1] = data[i];
        }

        return DataMessage.createMessage(message);
    }

    public byte[] getRequestBytes() {
        if(!receivedRequest()) {
            return null;
        }

        if(cachedRequestBytes == null) {
            cachedRequestBytes = new byte[getDataBytes().length - 1];
            for(int i=0;i<getDataBytes().length - 1;i++) {
                cachedRequestBytes[i] = getDataBytes()[i + 1];
            }
        }

        return cachedRequestBytes;
    }

}
