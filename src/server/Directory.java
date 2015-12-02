package server;

import com.ndsucsci.objects.SearchResult;
import com.ndsucsci.objects.UpdateFile;
import com.ndsucsci.objects.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by closestudios on 10/10/15.
 */
public class Directory implements Serializable{

    HashMap<String,ArrayList<DirectoryFile>> directory;

    public Directory() {
        directory = new HashMap<>();
    }

    public boolean updateFile(UpdateFile file, String computerUUID) {
        ArrayList<DirectoryFile> userFiles = new ArrayList<DirectoryFile>();
//        if(!directory.containsKey(computerUUID)) {
//            userFiles = new ArrayList<DirectoryFile>();
//        } else {
//            userFiles = directory.get(computerUUID);
//        }

        if(file.add) {
            // Make sure it isn't already there
            for(int i=0;i<userFiles.size();i++) {
                if(userFiles.get(i).filename.equals(file.filename) && userFiles.get(i).filesize.equals(file.filesize)) {
                    return false;
                }
            }

            // Add it!
            userFiles.add(new DirectoryFile(file.filename, file.filesize));
        } else {
            boolean found = false;
            for(int i=0;i<userFiles.size();i++) {
                if(userFiles.get(i).filename.equals(file.filename) && userFiles.get(i).filesize.equals(file.filesize)) {
                    found = true;
                    userFiles.remove(i);
                    break;
                }
            }
            if(!found) {
                return false;
            }
        }

        directory.put(computerUUID, userFiles);
        return true;
    }

    public ArrayList<SearchResult> searchFiles(String query, ArrayList<User> users) {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        Object[] keys = directory.keySet().toArray();
        for(int i=0;i<keys.length;i++) {
            ArrayList<DirectoryFile> files = directory.get(keys[i]);
            for(int j=0;j<files.size();j++) {
                // Check if contains the query
                if(files.get(j).filename.toLowerCase().contains(query.toLowerCase())) {
                    if(isUserOnline(users, (String) keys[i])) {
                        // Add to search
                        searchResults.add(new SearchResult(files.get(j).filename, files.get(j).filesize, (String) keys[i]));
                    }
                }
            }
        }

        return searchResults;
    }

    public static boolean isUserOnline(ArrayList<User> users, String key) {
        for(int i=0;i<users.size();i++) {
            if(users.get(i).uuid.equals(key) && users.get(i).isOnline()) {
                return true;
            }
        }
        return false;
    }

    public class DirectoryFile implements Serializable{
        public String filename;
        public String filesize;
        public DirectoryFile() {

        }
        public DirectoryFile(String filename, String filesize) {
            this.filename = filename;
            this.filesize = filesize;
        }
    }
}
