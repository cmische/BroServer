package networking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DataMessage {


    byte[] dataSize;
    int dataLengthReceived;
    ArrayList<Byte> allData;
    byte[] cachedData;

    public DataMessage() {
        dataSize = new byte[4];
        dataLengthReceived = 0;
        allData = new ArrayList<>();
    }

    public void getBytesFromInput(InputStream is) throws Exception {
        byte[] data = new byte[1024];
        int noDataCheck = 0;
        while(!receivedRequest() && noDataCheck < 5) {
            int length = is.read(data, 0, data.length);
            if(length == 0) {
                noDataCheck ++;
            } else if(length == -1) {
                noDataCheck = 10;
            }
            receivedBytes(data, length);
        }
        if(noDataCheck >= 5) {
            throw new Exception("Disconnected");
        }
    }

    void receivedBytes(byte[] bytes, int dataLength) {

        int processedBytes = 0;

        while(processedBytes < dataLength && dataLengthReceived < 4) {
            dataSize[dataLengthReceived] = bytes[processedBytes];
            dataLengthReceived ++;
            processedBytes ++;
        }

        while(processedBytes < dataLength) {
            allData.add(bytes[processedBytes]);
            processedBytes ++;
        }

        System.out.println("Received " + allData.size() + "/" + getDataLength());

    }

    int getDataLength() {
        if(dataLengthReceived < 4) {
            return 99999;
        }
        int length = java.nio.ByteBuffer.wrap(dataSize).getInt();
        return length;
    }

    ArrayList<Byte> getData() {
        if(!receivedRequest()) {
            return null;
        }
        return allData;
    }

    public byte[] getDataBytes() {
        if(!receivedRequest()) {
            return null;
        }

        if(cachedData == null) {
            cachedData = new byte[getData().size()];
            for(int i=0;i<cachedData.length;i++) {
                cachedData[i] = getData().get(i);
            }
        }

        return cachedData;
    }

    public boolean receivedRequest() {
        if(dataLengthReceived < 4) {
            return false;
        }

        return getDataLength() == allData.size();
    }

    public static byte[] createMessage(byte[] data) {
        byte[] message = new byte[data.length + 4];

        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(data.length).array();

        for(int i=0;i<4;i++) {
            message[i] = lengthBytes[i];
        }

        for(int i=lengthBytes.length;i<4 + data.length;i++) {
            message[i] = data[i - 4];
        }

        return message;
    }

    public static ArrayList<byte[]> getBlocks(byte[] data) {
        ArrayList<byte[]> blocks = new ArrayList<>();

        int processingData = 0;
        System.out.println("Processing Blocks: " + data.length);

        while(processingData < data.length) {

            // Get block length
            byte[] blockLength = new byte[4];
            blockLength[0] = data[processingData];
            processingData ++;
            blockLength[1] = data[processingData];
            processingData ++;
            blockLength[2] = data[processingData];
            processingData ++;
            blockLength[3] = data[processingData];
            processingData ++;

            int blockSize = java.nio.ByteBuffer.wrap(blockLength).getInt();

            byte[] block = new byte[blockSize];
            for(int i=0;i<block.length;i++) {
                block[i] = data[processingData];
                processingData ++;
            }
            blocks.add(block);
        }

        return blocks;
    }

    public static byte[] createBlocks(ArrayList<byte[]> blocks) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        for(int i=0;i<blocks.size();i++) {

            byte[] blockLength = ByteBuffer.allocate(4).putInt(blocks.get(i).length).array();
            outputStream.write(blockLength);
            outputStream.write(blocks.get(i));
        }

        return outputStream.toByteArray();
    }

}
