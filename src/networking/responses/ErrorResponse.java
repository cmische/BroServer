package networking.responses;

import networking.DataMessage;

import java.io.IOException;

public class ErrorResponse extends DataMessage {

    byte[] cachedSuccesData;

    public boolean getSuccess() {
        return super.getDataBytes().length > 0 && super.getDataBytes()[0] == 1;
    }

    public String getError() {
        return new String(getDataBytes());
    }

    @Override
    public byte[] getDataBytes() {
        if(!receivedRequest() || !getSuccess()) {
            return null;
        }

        if(cachedSuccesData == null) {
            cachedSuccesData = new byte[super.getDataBytes().length];
            for(int i=1;i<super.getDataBytes().length;i++) {
                cachedSuccesData[i-1] = super.getDataBytes()[i];
            }
        }

        return cachedSuccesData;
    }

    public static byte[] createErrorMessage(String error) throws IOException {
        return createMessage(false, error.getBytes());
    }

    public static byte[] createMessage(boolean success, byte[] data) throws IOException {

        byte[] message = new byte[data.length + 1];
        message[0] = (byte)(success ? 1 : 0);
        for(int i=0;i<data.length;i++) {
            message[i+1] = data[i];
        }

        return DataMessage.createMessage(message);
    }

}
