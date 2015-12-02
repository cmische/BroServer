package networking;


public interface ServerApiCalls {

    public interface NetworkCall {
        void onComplete(ServerApiCalls serverApiCalls);
    }


    // Sign In
    void signUp(String broName, String password, SignInCallback callback);
    void signIn(String broName, String password, SignInCallback callback);
    void signIn(String token, SignInCallback callback);

    interface SignInCallback {
        void onSuccess(String token, String broName);
        void onError(String error);
    }

    // Bro Calls
    void getBros(String token, BroCallback callback);
    void addBro(String token, String broName, BroCallback callback);
    void removeBro(String token, String broName, BroCallback callback);
    void blockBro(String token, String broName, BroCallback callback);

    interface BroCallback {
        void onSuccess(Bro[] bros);
        void onError(String error);
    }

    // Ping Calls
    void onUpdateLocation(String token, BroLocation location, UpdateCallback callback);

    interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }

    // Send Calls
    void sendBroMessage(String token, String broName, BroMessage message, UpdateCallback callback);
    void getBroMessage(String token, String messageId, BroMessageCallback callback);

    interface BroMessageCallback {
        void onSuccess(BroMessage message);
        void onError(String error);
    }

}
