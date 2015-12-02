package clientservermessages;

import com.ndsucsci.objects.SearchResult;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/10/15.
 */
public class SearchResponse extends DataMessage {

    public SearchResponse() {
        super();
    }

    public ArrayList<SearchResult> getSearchResults() {

        ArrayList<byte[]> blocks = DataMessage.getBlocks(getDataBytes());
        ArrayList<SearchResult> results = new ArrayList<>();

        for(int i=0;i<blocks.size();i++) {
            results.add(new SearchResult(blocks.get(i)));
        }

        return results;
    }

    public static byte[] createMessage(ArrayList<SearchResult> searchResults) throws IOException {
        ArrayList<byte[]> blocks = new ArrayList<>();
        for(int i=0;i<searchResults.size();i++) {
            blocks.add(searchResults.get(i).getBytes());
        }

        return DataMessage.createMessage(DataMessage.createBlocks(blocks));
    }

}
