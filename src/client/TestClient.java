package client;

import com.ndsucsci.objects.SearchResult;
import com.ndsucsci.objects.UpdateFile;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/12/15.
 */
public class TestClient {


    public static void main(String[] args) {

        new ClientRegisterThread("", 9090, new ClientRegisterThread.RegisterCallback() {
            @Override
            public void onRegistered(String computerUUID) {
                System.out.println("Register Computer UUID: " + computerUUID);
                pingComputer(computerUUID);
            }
        }).start();

    }

    static void pingComputer(final String uuid) {

        new ClientPingThread("", 9091, uuid).start();

        new ClientSearchThread("", 9090, "test", new ClientSearchThread.SearchCallback() {
            @Override
            public void searchResults(ArrayList<SearchResult> searchResults) {
                System.out.println("Total Search Results: " + searchResults.size());
                addFile(uuid);
            }
        }).start();

    }

    static void addFile(String uuid) {

        ArrayList<UpdateFile> files = new ArrayList<>();
        UpdateFile file = new UpdateFile("test.txt", "1KB", true);
        files.add(file);

        new ClientUpdateFileThread("", 9090, files, uuid, new ClientUpdateFileThread.UpdateFilesCallback() {
            @Override
            public void onUpdate(boolean updated) {
                System.out.println("Updated Files: " + updated);
            }
        }).start();

    }

}
