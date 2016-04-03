/**
 * Created by leoding on 3/31/2016.
 */
import java.util.*;

public class Tweets {
    private SortedMap<KeyOfTweets, List<String>> tweets;
    private Map<Long, Integer> sameTimeTweets;
    private long latestTimestamp;

    public Tweets() {
        tweets = new TreeMap<KeyOfTweets, List<String>>(new KeyOfTweetsComparator());
        sameTimeTweets = new HashMap<Long, Integer>();
        latestTimestamp = 0;
    }

    public void updateLatestTimestamp(long newTimestamp) { latestTimestamp = Math.max(latestTimestamp, newTimestamp); }

    public long getLatestTimestamp() { return latestTimestamp; }

    public long getEarliestTimestamp() { return tweets.firstKey().getTimestamp(); }

    // return any one tweet with that timestamp
    // public List<String> getNodeNames(long timestamp) { return tweets.get(timestamp); }
    public List<String> getEarliestNodeNames() {
        return tweets.get(tweets.firstKey());
    }

    // get the first List of nodeNames and remove the tweet
    public Tweet removeTweet() {
        List<String> names2remove = getEarliestNodeNames();
        long timestamp = getEarliestTimestamp();
        tweets.remove(tweets.firstKey());
        return new Tweet(timestamp, names2remove);
    }


    public void addOneTweet(Tweet oneTweet) {
        // move timing window anyway, even for signal tag tweet
        updateLatestTimestamp(oneTweet.getTimestamp());
        if (oneTweet.getNodeNames().size() < 2) return; // single or empty tag, do nothing

        // to deal with multiple tweets at the same second
        Integer sameSecCnt = sameTimeTweets.get(oneTweet.getTimestamp());
        if (sameSecCnt == null) sameSecCnt = 0;
        sameSecCnt++;
        sameTimeTweets.put(oneTweet.getTimestamp(), sameSecCnt);

        KeyOfTweets key = new KeyOfTweets(oneTweet.getTimestamp(), sameSecCnt);
        tweets.put(key, oneTweet.getNodeNames());
    }

    public boolean needsEviction(long windowSize) {
        if (tweets.isEmpty()) return false;
        return getEarliestTimestamp() + windowSize <= latestTimestamp;
    }

    public String convertDate(long sec) {
        Date date = new Date(sec * 1000);
        return date.toString();
    }

    public void print() {
        System.out.print("latestTimeStamp = " + latestTimestamp + "\n");
        for (Map.Entry<KeyOfTweets, List<String>> entry : tweets.entrySet()) {
            KeyOfTweets key = entry.getKey();
            List<String> value = entry.getValue();
            System.out.print(convertDate(key.getTimestamp()) + ", " + key.getTimestamp() + "(" + key.getKeyID() + ") " + "=> ");
            System.out.print(value);
            System.out.print("\n");
        }
    }
}