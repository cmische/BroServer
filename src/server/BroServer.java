package server;

import networking.Bro;
import networking.BroLocation;
import networking.BroMessage;
import networking.responses.SignInResponse;
import objects.Message;
import objects.User;

import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class BroServer {

    static ArrayList<User> users;
    static ArrayList<Message> messages;
    static boolean userLock = false;
    static boolean messageLock = false;

    public static void main(String[] args) {


        try {
            // Init Users
            loadUsers();
            // Init messages
            loadMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        try {
            System.out.println("Starting Server On IP: " + InetAddress.getLocalHost().getHostAddress().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Create (Main Connection Thread)
        ServerMainThread serverMainThread = new ServerMainThread(9090);
        serverMainThread.start();
    }

    public static ArrayList<Bro> getBros(String uuid) throws IOException, InterruptedException {
        // Check for user
        for (User user: users) {
            if(user.uuid.equals(uuid)) {
                System.out.println("Return " + user.getBros(users).size() + " bros");
                return user.getBros(users);
            }
        }
        System.out.println("User not found. Can't return bros");
        return new ArrayList();
    }

    public static String signUpUser(String name, String password, String gcm) throws IOException, InterruptedException {
        // Create UUID for new computer
        String uuid = UUID.randomUUID().toString();
        while(repeatUUID(uuid)) {
            uuid = UUID.randomUUID().toString();
        }

        // Create User
        users.add(new User(uuid, name, password, gcm));
        saveUsers();

        return uuid;
    }

    public static byte[] signInCred(String name, String password) throws IOException {

        for(int i=0;i<users.size();i++) {
            User currentUser = users.get(i);
            if(currentUser.broName.equals(name)) {
                if(currentUser.password.equals(password)) {
                    System.out.println("Logging User In");
                    return SignInResponse.createSuccessMessage(currentUser.uuid, currentUser.broName);
                } else {
                    System.out.println("Invalid Password");
                    return SignInResponse.createErrorMessage("Invalid password.");
                }
            }
        }

        System.out.println("Invalid Username");
        return SignInResponse.createErrorMessage("Invalid username");
    }

    public static byte[] signInToken(String token) throws IOException {

        for(int i=0;i<users.size();i++) {
            User currentUser = users.get(i);
            if(currentUser.uuid.equals(token)) {
                System.out.println("Logging User In");
                return SignInResponse.createSuccessMessage(currentUser.uuid, currentUser.broName);
            }
        }

        System.out.println("Token Expired");
        return SignInResponse.createErrorMessage("Expired token.");
    }

    public static boolean existingBroName(String name) {

        for(int i=0;i<users.size();i++) {
            if(users.get(i).broName.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static Bro createBro(String broName) {

        for(User user: users) {
            if (user.broName.equals(broName)) {
                Bro bro = new Bro(user.broName);
                bro.location = user.location;
                return bro;
            }
        }
        System.out.println("User not found. Can't create Bro");
        return null;
    }

    public static String addBro(String uuid, Bro bro) throws IOException, InterruptedException {

        int requestUserID = -1;
        int addUserID = -1;

        for(int i=0;i<users.size();i++) {
            if(users.get(i).uuid.equals(uuid)) {
                requestUserID = i;
                if(addUserID != -1) {
                    break;
                }
            }
            if(users.get(i).broName.equals(bro.broName)) {
                addUserID = i;
                if(requestUserID != -1) {
                    break;
                }
            }
        }

        if(users.get(addUserID).blocked.contains(users.get(requestUserID).broName)) {
            return "User has blocked you";
        }

        if (!users.get(requestUserID).getBros(users).contains(bro)) {
            users.get(requestUserID).addBro(bro);
            saveUsers(); //save user with new bros
            return null;
        }

        return "Error";
    }



    public static boolean removeBro(String uuid, String broName) throws IOException, InterruptedException {
        //remove bro
        for (User user: users) {
            if (user.uuid.equals(uuid)) {
                for (Bro bro: user.getBros(users)) {
                    if (bro.broName.equals(broName)) {
                        user.getBros(users).remove(bro);
                        saveUsers();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean blockBro(String uuid, String broName) throws IOException, InterruptedException {
        //block bro
        User theBro = null;
        for (User user: users) {
            if (user.uuid.equals(uuid)) {
                user.blockBro(broName);
                theBro = user;
            }
        }
        for (User user: users) {
            if (user.broName.equals(broName)) {
                user.getBlock(theBro.broName);
            }
        }

        saveUsers();

        return theBro != null;
    }

    public static boolean updateLocation(String uuid, BroLocation location) {
        for (User user: users) {
            if (user.uuid.equals(uuid)) {
                location.pingTime = new Date();
//                System.out.println("lat:" + location.latitude + "long:" + location.longitude);
                //find bros in area
                checkForBrosInArea(user, location);
                user.location = location;
                return true;
            }
        }
        return false;
    }

    private static boolean checkForBrosInArea(User user, BroLocation location) {
        //array to hold matches between user and bros
        ArrayList<User> matches = new ArrayList<User>();

        //match bros to users
        outerLoop:
        for (User matchUser: users) {
            //check for matching user
            for (Bro bro: user.getBros(users)) {
                if (matchUser.broName.equals(bro.broName)) {
                    matches.add(matchUser);
                }
                //check if users are all found
                if (matches.size() == user.getBros(users).size()) {
                    break outerLoop;
                }
            }
        }

        //check user locations for nearby bros
        for(Bro bro : user.getBros(users)) {
            for (int i = 0; i < matches.size(); i++)  {
                if(matches.get(i).broName.equals(bro.broName)) {
                    BroLocation broCurrentLocation = matches.get(i).location; //get bro location
                    bro.location = broCurrentLocation;
                    boolean nearby = matches.get(i).broIsNearby(user.location); //get if bro is nearby
                    if (nearby && bro.recentlyNearBy) {
                        bro.recentlyNearBy = true;
                        if (user.location.pingTime != null) {
                            bro.totalTimeSecs += ((location.pingTime.getTime() - user.location.pingTime.getTime())/1000);
                        }
                    } else {
                        bro.recentlyNearBy = nearby;
                    }
                    break;
                }
            }

        }


        return false;
    }

    public static boolean repeatUUID(String uuid) {
        for(int i=0;i<users.size();i++) {
            if(users.get(i).uuid.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean repeatMessageId(String messageID) {
        for(int i=0;i<messages.size();i++) {
            if(messages.get(i).messageID.equals(messageID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean sendBroMessage(String broName, BroMessage broMessage) throws IOException, InterruptedException {
        //create unique message id
        String messageId = UUID.randomUUID().toString();
        while(repeatMessageId(messageId)) {
            messageId = UUID.randomUUID().toString();
        }
        //create message to store
        Message message = new Message(broMessage, messageId);

        //store message
        messages.add(message);
        saveMessages(); // save new message

        //send to bro
        //notify bro using gcm
        for (User user: users) {
            if (user.broName.equals(broName)) {
                System.out.println("Start GCMThread.");
                new GCMThread(80, user.gcm, message.messageID).start();
                return true;
            }
        }

        return false;
    }

    public static Message getBroMessage(String messageId) throws IOException, InterruptedException{
        //retrieve message
        for (Message message: messages) {
            if (message.messageID.equals(messageId)) {
                messages.remove(message); //remove message already read
                saveMessages(); //save newly changed list
                return message;
            }
        }
        return null;
    }

//    public static void lotsOfUsers() {
//        for (int i = 0; i<200000; i++) {
//            User user = new User();
//            while(repeatMess)
//            user.location =
//        }
//    }

    public static void loadUsers() throws IOException, ClassNotFoundException {
        File file = new File("users.bytes");
        if(file.exists()) {
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            ArrayList<User> c = (ArrayList<User>) s.readObject();
            System.out.println("Loaded " + c.size() + " Users!");
            s.close();
            users = c;
        } else {
            System.out.println("0 Users!");
            users = new ArrayList<>();
        }
    }

    public static boolean saveUsers() throws IOException, InterruptedException {
        while(userLock) {
            Thread.sleep(10);
        }
        userLock = true;
        File file = new File("users.bytes");
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(users);
        s.close();
        userLock = false;
        return true;
    }

    public static void loadMessages() throws IOException, ClassNotFoundException {
        File file = new File("messages.bytes");
        if(file.exists()) {
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            ArrayList<Message> c = (ArrayList<Message>) s.readObject();
            System.out.println("Loaded " + c.size() + " messages!");
            s.close();
            messages = c;
        } else {
            System.out.println("0 messages!");
            messages = new ArrayList<>();
        }
    }

    public static boolean saveMessages() throws IOException, InterruptedException {
        while(messageLock) {
            Thread.sleep(10);
        }
        messageLock = true;
        File file = new File("messages.bytes");
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(messages);
        s.close();
        messageLock = false;
        return true;
    }

}
