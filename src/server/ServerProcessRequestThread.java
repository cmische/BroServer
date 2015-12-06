package server;

import networking.Bro;
import networking.BroMessage;
import networking.requests.*;
import networking.responses.BroMessageResponse;
import networking.responses.ErrorResponse;
import networking.responses.GetBrosResponse;
import networking.responses.SignInResponse;
import objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerProcessRequestThread extends Thread {

    Socket clientSocket = null;

    public ServerProcessRequestThread(Socket socket) {
        super("ServerProcessRequestThread");
        clientSocket = socket;
    }


    public void run() {

        // Print out Client
        System.out.println("Request From Client: " + clientSocket.getInetAddress().getHostAddress());

        try {
            OutputStream outToClient = clientSocket.getOutputStream();
            InputStream inFromClient = clientSocket.getInputStream();

            // Get request
            ServerRequest serverRequest = new ServerRequest();
            serverRequest.getBytesFromInput(inFromClient);

            // Process Request
            byte[] response = new byte[0];
            switch (serverRequest.getRequestType()) {
                case SignUp:
                    //create request
                    SignUpRequest signUpRequest = new SignUpRequest(serverRequest);

                    // Registers Computer
                    boolean broNameExists = BroServer.existingBroName(signUpRequest.getBroName());

                    //check if valid broName
                    if (broNameExists) {
                        response = SignInResponse.createErrorMessage("Bro Name Already Exists.");
                        System.out.println("Bro Name Exists");
                    } else {
                        String newUUID = BroServer.signUpUser(signUpRequest.getBroName(), signUpRequest.getPassword(),
                                signUpRequest.getGcmId());

                        // Respond with new UUID;
                        response = SignInResponse.createSuccessMessage(newUUID, signUpRequest.getBroName());

                        System.out.println("Registered UUID: " + newUUID);
                    }

                    break;
                case SignInCreds:

                    // Create request
                    SignInCredsRequest signInCredsRequest = new SignInCredsRequest(serverRequest);

                    // Create response
                    response = BroServer.signInCred(signInCredsRequest.getBroName(),
                            signInCredsRequest.getPassword());


                    break;
                case GetBros:
                    //create request
                    GetBrosRequest getBrosRequest = new GetBrosRequest(serverRequest);

                    //get list of bros
                    ArrayList<Bro> bros = BroServer.getBros(getBrosRequest.getToken());

                    // response filled with bros
                    response = GetBrosResponse.createSuccessMessage(bros.toArray(new Bro[0]));


                    break;
                case AddBro:
                    //create request
                    AddBroRequest addBroRequest = new AddBroRequest(serverRequest);

                    //try creating bro
                    Bro bro = BroServer.createBro(addBroRequest.getBroName());

                    //try adding bro
                    if(bro != null) {
                        String resultAdd = BroServer.addBro(addBroRequest.getToken(), bro);
                        if(resultAdd == null) {
                            response = getBroResponse(addBroRequest.getToken());
                        } else {
                            response = ErrorResponse.createMessage(false, resultAdd.getBytes());
                        }
                    } else {
                        response = ErrorResponse.createMessage(false, "Bro Not Created".getBytes());
                    }

                    break;
                case UpdateLocation:
                    //create request
                    UpdateLocationRequest updateLocationRequest = new UpdateLocationRequest(serverRequest);

                    //update user location
                    boolean updated = BroServer.updateLocation(updateLocationRequest.getToken(),
                            updateLocationRequest.getBroLocation());

                    System.out.println("Updated location successfully = " + updated);

                    response = ErrorResponse.createMessage(updated, new byte[0]);

                    break;
                case RemoveBro:

                    //create request
                    RemoveBroRequest removeBroRequest = new RemoveBroRequest(serverRequest);

                    //try to remove bro
                    boolean removed = BroServer.removeBro(removeBroRequest.getToken(), removeBroRequest.getBroName());

                    System.out.println("Removed bro successfully = " + removed);
                    if(removed) {
                        response = getBroResponse(removeBroRequest.getToken());
                    } else {
                        response = ErrorResponse.createMessage(false, "Bro Not Created".getBytes());
                    }
                    break;
                case BlockBro:
                    //create request
                    BlockBroRequest blockBroRequest = new BlockBroRequest(serverRequest);

                    //try to block bro
                    boolean blocked = BroServer.blockBro(blockBroRequest.getToken(), blockBroRequest.getBroName());

                    System.out.println("Blocked bro successfully = " + blocked);
                    if(blocked) {
                        response = getBroResponse(blockBroRequest.getToken());
                    } else {
                        response = ErrorResponse.createMessage(false, "Bro Not Created".getBytes());
                    }
                    break;
                case SignInToken:
                    // Create request
                    SignInTokenRequest signInTokenRequest = new SignInTokenRequest(serverRequest);

                    // Create response
                    response = BroServer.signInToken(signInTokenRequest.getToken());
                    break;
                case SendBroMessage:

                    //create request
                    SendBroMessageRequest sendBroMessageRequest = new SendBroMessageRequest(serverRequest);

                    //try to send Bro message
                    boolean messageSent = BroServer.sendBroMessage(sendBroMessageRequest.getBroName(),
                            sendBroMessageRequest.getBroMessage());

                    if (messageSent) {
                        response = ErrorResponse.createMessage(true, new byte[0]);
                        System.out.println("Your message has sent");
                    } else {
                        response = ErrorResponse.createMessage(false, "Failed to send message".getBytes());
                        System.out.println("Failed to send message");
                    }

                    break;
                case GetBroMessage:

                    //create request
                    GetBroMessageRequest getBroMessageRequest = new GetBroMessageRequest(serverRequest);

                    //try to get message
                    Message message = BroServer.getBroMessage(getBroMessageRequest.getMessageId());

                    //respond with message
                    if (message != null) {
                        response = BroMessageResponse.createSuccessMessage(new BroMessage(message.messageTitle,
                                message.messageDetails, message.audioBytes, message.extension));
                    } else {
                        response = BroMessageResponse.createErrorMessage("Failed to get message");
                    }

                    break;
            }

            // Send response
            outToClient.write(response);
            outToClient.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] getBroResponse(String token) throws Exception {
        byte[] response;

        ArrayList<Bro> broList = BroServer.getBros(token);
        if (broList != null) {
            response = GetBrosResponse.createSuccessMessage(broList.toArray(new Bro[broList.size()]));
            System.out.println("Response Created.");
        } else {
            response = GetBrosResponse.createSuccessMessage(new Bro[0]);
            System.out.println("Failed to get bro response. Bro list is null");
        }

        return response;
    }

}
