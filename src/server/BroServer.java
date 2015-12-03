package server;

import networking.Bro;
import networking.BroLocation;
import networking.responses.SignInResponse;
import objects.User;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class BroServer {

    static ArrayList<User> users;
//    static Directory directory;
    static boolean userLock = false;
//    static boolean directoryLock = false;

    public static void main(String[] args) {


        try {
            // Init Users
            loadUsers();

            // Init Directory
//            loadDirectory();
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

        // Create Ping (Hello Thread)
//        ServerPingThread serverPingThread = new ServerPingThread(9091);
//        serverPingThread.start();


    }

    public static ArrayList<Bro> getBros(String uuid) throws IOException, InterruptedException {
        // Check for user
        for (User user: users) {
            if(user.uuid == uuid) {
                return user.getBros();
            }
        }

        return new ArrayList();
    }

//    public static ArrayList<SearchResult> searchFiles(String query) {
//        return directory.searchFiles(query, users);
//    }
//
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
                SignInResponse.createSuccessMessage(currentUser.uuid, currentUser.broName);
            }
        }

        System.out.println("Token Expired");
        return SignInResponse.createErrorMessage("Expired token.");
    }

//    public static boolean pingUser(String uuid, String ipAddress) throws IOException, InterruptedException {
//        for(int i=0;i<users.size();i++) {
//            if(users.get(i).uuid.equals(uuid)) {
//                users.get(i).pingUser(ipAddress);
//                saveUsers();
//                return true;
//            }
//        }
//        return false;
//    }
//
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
                return bro;
            }
        }
        return null;
    }

    public static boolean addBro(String uuid, Bro bro) {

        for(User user: users) {
            if (user.uuid.equals(uuid)) {
                user.addBro(bro);
                return true;
            }
        }
        return false;
    }

    public static boolean removeBro(String uuid, String broName) {
        //remove bro
        for (User user: users) {
            if (user.uuid.equals(uuid)) {
                for (Bro bro: user.getBros()) {
                    if (bro.broName.equals(broName)) {
                        user.getBros().remove(bro);
                    }
                }
            }
        }
        return false;
    }

    public static boolean blockBro(String uuid, String broName) {
        //block bro
        for (User user: users) {
            if (user.uuid.equals(uuid)) {
                for (Bro bro: user.getBros()) {
                    if (bro.broName.equals(broName)) {
                        user.blockBro(bro);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean updateLocation(String uuid, BroLocation location) {
        for (User user: users) {
            if (user.uuid.equals(uuid)) {
                user.location = location;
                //find bros in area
                checkForBrosInArea(user, location);
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
            for (Bro bro: user.getBros()) {
                if (matchUser.broName.equals(bro.broName)) {
                    matches.add(matchUser);
                }
                //check if users are all found
                if (matches.size() == user.getBros().size()) {
                    break outerLoop;
                }
            }
        }

        //check user locations for nearby bros
        for(Bro bro : user.getBros()) {
            for (int i = 0; i < matches.size(); i++)  {
                if(matches.get(i).broName.equals(bro.broName)) {
                    if (matches.get(i).nearBy(user.location) && bro.recentlyNearBy) {
                        bro.recentlyNearBy = true;
                        if (user.location.pingTime != null) {
                            bro.totalTimeSecs += ((location.pingTime.getTime() - user.location.pingTime.getTime())/1000);
                        }
                        user.location.pingTime = new Date();
                    } else {
                        user.location.pingTime = null;
                        bro.recentlyNearBy = false;
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

    public static void loadUsers() throws IOException, ClassNotFoundException {
        File file = new File("users.bytes");
        if(file.exists()) {
            System.out.println("Loaded Users!");
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            ArrayList<User> c = (ArrayList<User>) s.readObject();
            s.close();
            users = c;
        } else {
            System.out.println("New Users!");
            users = new ArrayList<>();
        }
    }
//
//    public static void loadDirectory() throws IOException, ClassNotFoundException {
//        File file = new File("directory.bytes");
//        if(file.exists()) {
//            System.out.println("Loaded Directory!");
//            FileInputStream f = new FileInputStream(file);
//            ObjectInputStream s = new ObjectInputStream(f);
//            Directory c = (Directory) s.readObject();
//            s.close();
//            directory = c;
//        } else {
//            System.out.println("New Directory!");
//            directory = new Directory();
//        }
//    }
//
//
//
//
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
//
//    public static boolean saveDirectory() throws IOException, InterruptedException {
//        while(directoryLock) {
//            Thread.sleep(10);
//        }
//        directoryLock = true;
//        File file = new File("directory.bytes");
//        FileOutputStream f = new FileOutputStream(file);
//        ObjectOutputStream s = new ObjectOutputStream(f);
//        s.writeObject(directory);
//        s.close();
//        directoryLock = false;
//        return true;
//    }

}
