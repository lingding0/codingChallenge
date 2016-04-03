import java.util.Comparator;

/**
 * Created by leoding on 3/31/2016.
 */

// comparator for TreeMap Keys
public class KeyOfTweetsComparator implements Comparator<KeyOfTweets> {
    // compare timestamp of the tweets. Small timestamp first
    // for the same timestamp of multiple tweets, small ID first
    @Override
    public int compare(KeyOfTweets k1, KeyOfTweets k2) {
        if (k1.getTimestamp() < k2.getTimestamp()) {
            return -1;
        } else if (k1.getTimestamp() > k2.getTimestamp()) {
            return 1;
        } else {
            if (k1.getKeyID() < k2.getKeyID()) {
                return -1;
            } else if (k1.getKeyID() == k2.getKeyID()) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}


