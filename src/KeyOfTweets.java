/**
 * Created by leoding on 3/31/2016.
 */

// The timestamp and ID if multiple tweets comes at the same second
public class KeyOfTweets {
    private long timestamp; // timestamp
    private int id;         // ID of the same timestamp tweets, starting from 1, 2, 3...

    public KeyOfTweets(long timestamp, int id) {
        this.timestamp = timestamp;
        this.id        = id;
    }

    public long getTimestamp() { return timestamp; }
    public int getKeyID() { return id; };
    public void setTimeStamp(long timestamp) { this.timestamp = timestamp; }
    public void setID(int id) { this.id = id; }

    public void print() {
        System.out.print("KeyOfTweets: (" + timestamp + ", " + id + ")\n");
    }
}
