import java.util.Date;
import java.util.List;

/**
 * Created by leoding on 3/31/2016.
 */
// information extracted from a single tweet line
public class Tweet {
    private long timestamp;         // timestamp of the tweet
    private List<String> nodeNames; // all the tags

    public Tweet(long timestamp, List<String> nodeNames) {
        this.timestamp = timestamp;
        this.nodeNames = nodeNames;
    }

    public long getTimestamp() { return timestamp; }
    public List<String> getNodeNames() { return nodeNames; }

    public void print() {
        System.out.print("oneTweet: " + convertDate(timestamp) + "(" + timestamp + "), ");
        System.out.print(nodeNames + "\n");
    }

    public String convertDate(long sec) {
        Date date = new Date(sec * 1000);
        return date.toString();
    }
}
