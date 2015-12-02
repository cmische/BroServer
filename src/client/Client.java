package client;


import com.ndsucsci.objects.SearchResult;
import com.ndsucsci.objects.UpdateFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static String uuid = null;
    private static File shareFolder = new File("share");


    public static void main(String[] args) {

        String hostName = "127.0.0.1";
        int portNo = 9090;

        //register client
        new ClientRegisterThread(hostName, portNo, new ClientRegisterThread.RegisterCallback() {
            @Override
            public void onRegistered(String computerUUID) {
                //need to cache uuid

                System.out.println("Register Computer UUID: " + computerUUID);
                uuid = computerUUID;

                //create share folder
                //get files from sharefolder
                if(!shareFolder.exists()) {
                    try {
                        shareFolder.mkdir();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //ping server and allow user to enter commands
                pingComputer(computerUUID);
                new ClientMainThread(9092).start();
                clientHasRegistered();
            }
        }).start();
    }

    private static void clientHasRegistered() {
        System.out.println("Type in help to view commands.");
        while (true) {
            //prompt user for command
            Scanner userInput = new Scanner(System.in);
            String request = userInput.nextLine().toLowerCase();

            //respond to request
            if (request.equals("help")) {
                System.out.println("Display help menu - help\nSearch for file - search\nadd file - add\ndownload file - download\n");
            } else if(request.equals("search")) {
                //find file client wants to search for
                clientSearch();
            } else if(request.equals("add")) {
                clientAddFiles();
            } else if(request.equals("download")) {
                clientDownloadFile();
            }
        }
    }

    private static void clientSearch() {
        //ask for file name then call search thread
        System.out.print("Type file to search for: ");
        Scanner userInput = new Scanner(System.in);
        new ClientSearchThread("127.0.0.1", 9090, userInput.nextLine(), new ClientSearchThread.SearchCallback() {
            @Override
            public void searchResults(ArrayList<SearchResult> searchResults) {
                System.out.println("Total Search Results: " + searchResults.size());
                System.out.println(searchResults);
            }
        }).start();
    }

    private static void clientAddFiles() {
        boolean addMore = true;
        ArrayList<UpdateFile> files = new ArrayList<>();
        System.out.println("Make sure all files being added are located in your share folder.");
        //get and add files from share folder
        if(shareFolder.exists()) {
            if(shareFolder.listFiles().length > 0) {
                for(File file : shareFolder.listFiles()) {
                    files.add(new UpdateFile(file.getName(), Long.toString(file.length()), true));
                }
            } else {
                System.out.println("Share folder is empty. You have nothing to add.");
            }

        } else {
            System.out.println("Share folder doesn't exist.");
        }

        if(files.size() > 0) {
            new ClientUpdateFileThread("127.0.0.1", 9090, files, uuid, new ClientUpdateFileThread.UpdateFilesCallback() {
                public void onUpdate(boolean updated) {
                    System.out.println("Updated Files: " + updated);
                }
            }).start();
        }
    }

    private static void clientDownloadFile() {
        //get ip address
        System.out.print("Type peer's IPAddress: ");
        Scanner userInput = new Scanner(System.in);
        String address = userInput.nextLine();

        //get filename
        System.out.println("Type file name: ");
        userInput = new Scanner(System.in);
        String fileName = userInput.nextLine();

        //create download thread
        new ClientDownloadFileThread(address, 9092, fileName).start();
    }

    private static void pingComputer(String uuid) {
        new ClientPingThread("127.0.0.1", 9091, uuid).start();
    }

}

