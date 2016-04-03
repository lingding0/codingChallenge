/**
 * Created by leoding on 3/31/2016.
 */
import java.util.*;

// Graph node, carries name and its neighbours with there relationship build time
public class Node {
    private long timeStamp;              // the latest update edge of this node
    private int  timestampCnt;           // number of conflict on current timestamp
    private long tweetID;                // only increase timestampCnt for different tweetID
    private Map<Node, Long> neighbours;  // neighbours of the node
    private String name;                 // tag/name of the node

    public Node(String name, long tweetID, long timeStamp) {
        this.name      = name;
        this.timeStamp = timeStamp;
        this.timestampCnt = 1;
        this.tweetID   = tweetID;
        neighbours     = new HashMap<Node, Long>();
    }


    // get tag of this node
    public String getName() { return this.name; }
    public int getTimestampCnt() { return this.timestampCnt; }
    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }
    public long getTimestamp() { return this.timeStamp; }
    public long getTweetID() { return this.tweetID; }
    public void setTweetID(long tweetID) { this.tweetID = tweetID; }


    // input: none
    // output: all the neighbours of this node
    public Set<Node> getNeighbours() { return this.neighbours.keySet(); }


    // input: timestamp and the new neighbour needs to be connected
    // output: number of edge added into graph
    // function: add one neighbour from current node pointing to the other neighbour
    //           if they are already connected, just increase the neighbour count
    //           or, make the new connection
    public int addNeighbour(long tweetID, long timestamp, Node newNeighbour) {
        //System.out.println("adding neighbour: (" + tweetID + ", " + timestamp + "), " + this.getTimestamp() + ", " + getName() + " ---> " + newNeighbour.getName());
        if (timestamp == this.getTimestamp() && tweetID != getTweetID()) {
            this.timestampCnt++;
        } else if (timestamp > this.getTimestamp()) {
            setTimeStamp(timestamp); // update Node timestamp
            timestampCnt = 1;
        } // else, keep old timestamp and its count

        setTweetID(tweetID);

        Long time = neighbours.get(newNeighbour);
        if (time != null) { // update time stamp of edge only
            neighbours.put(newNeighbour, Math.max(time, timestamp));
            return 0;
        } else {
            neighbours.put(newNeighbour, timestamp);
            return 1;
        }
    }


    // input: timestamp and the node to be evicted
    // output: number of edge that is removed from this node
    // function: remove the expired node from graph. If newer connections are made
    //           after the connections to be removed, keep the connection
    public int removeNeighbour(long timestamp, Node expiredNode) {
        // if two tweets share the same time stamp and the edge is
        // removed in the previous removal, the next edge removal may
        // not find the same age with same timestamp at all
        if (!neighbours.containsKey(expiredNode)) return 0;
        long time = neighbours.get(expiredNode);
        if (time > timestamp) return 0;

        neighbours.remove(expiredNode);
        //if (DBG_MODE) System.out.println("EdgeCnt--: " + this.getName() + " --- " + expiredNode.getName());
        return 1;
    }


    public void decTimestampCnt() { this.timestampCnt--; }

    // print function
    public int print() {
        System.out.print(name + ", ");
        System.out.print(convertDate(timeStamp) + ", " + timeStamp + ", (" + timestampCnt + ", " + tweetID + ") ");
        System.out.print("[");
        List<String> neighbList = new ArrayList<String>();
        for (Node neighb : neighbours.keySet()) {
            neighbList.add(neighb.getName());
        }

        Collections.sort(neighbList);
        for (String s : neighbList) {
            System.out.print(s + ", ");
        }
        System.out.print("]\n");
        return neighbList.size();
    }


    // easy reading printing
    public String convertDate(long sec) {
        Date date = new Date(sec * 1000);
        return date.toString();
    }

}
