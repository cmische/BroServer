package server;

import client.ClientRegisterThread;
import networking.Bro;
import networking.responses.SignInResponse;
import objects.User;
import server.Directory;
import server.ServerMainThread;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
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
            if(currentUser.name.equals(name)) {
                if(currentUser.password.equals(password)) {
                    System.out.println("Logging User In");
                    return SignInResponse.createSuccessMessage(currentUser.uuid, currentUser.name);
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
                SignInResponse.createSuccessMessage(currentUser.uuid, currentUser.name);
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
            if(users.get(i).name.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static boolean createBro(String broName) {

        for(User user: users) {
            if (user.name.equals(broName)) {
                Bro bro = new Bro()
            }
        }

        return false;
    }

    public static boolean addBro(String uuid) {
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
