/**
 * Created by leoding on 3/31/2016.
 */
import java.util.*;

public class Graph {
    private Map<String, Node> nameNode; // Hash map to search by name
    private int nodeCnt;                // totoal nodes in graph
    private int neighbourCnt;           // total number of edges

    public Graph() {
        nameNode     = new HashMap<String, Node>();
        nodeCnt      = 0;
        neighbourCnt = 0;
    }


    // input:  one tweet extraction from one tweet line
    // output: none
    // function: build relationship between all tags within a tweet
    //           and add relationship into graph
    public void addOneTweet(long tweetID, Tweet oneTweet) {
        long curTime = oneTweet.getTimestamp();
        List<String> nodeNames = oneTweet.getNodeNames();
        for (int i = 0; i < nodeNames.size()-1; i++) {
            for (int j = i+1; j < nodeNames.size(); j++) {
                addNode(tweetID, curTime, nodeNames.get(i), nodeNames.get(j));
            }
        }
    }


    // input: timestamp and the pair of tag that need to be connected
    // output: none
    // function: add new node into graph and connect the edge. If the node/edge
    //           is already exist, update the timestamp to latest
    public void addNode(long tweetID, long timestamp, String from, String to) {
        if (from.equals(to)) return; // no self connected nodes
        Node fromNode = nameNode.get(from);
        Node toNode = nameNode.get(to);
        if (fromNode == null) {
            fromNode = new Node(from, tweetID, timestamp);
            nameNode.put(from, fromNode);
            nodeCnt++;
        }

        if (toNode == null) {
            toNode = new Node(to, tweetID, timestamp);
            nameNode.put(to, toNode);
            nodeCnt++;
        }

        // for all nodes, update timestamp when adding connection edges
        neighbourCnt += fromNode.addNeighbour(tweetID, timestamp, toNode);
        neighbourCnt += toNode.addNeighbour(tweetID, timestamp, fromNode);
    }


    // input: timestamp and the node name that needs to be removed from graph
    // output: none
    // function: if new connection after the timestamp is available, don't remove node
    //           otherwise, remove node and keep track of total nodes count
    public void removeNode(long timestamp, String nodeName) {
        Node nodeRemove = nameNode.get(nodeName);
        if (timestamp < nodeRemove.getTimestamp()) return; // node is newer than time-to-be-removed, keep it
                                                           // because this node has other new connection
        if (nodeRemove.getTimestampCnt() > 1) { // more node is on the same timestamp
            nodeRemove.decTimestampCnt();
        } else {
            nodeCnt--;
            //System.out.println("nodeCnt-- on node " + nodeName);
            nameNode.remove(nodeName);
        }
    }


    // input: tweet info extracted from tweet line
    // output: none
    // function: based on out-of-window evicted tweet info, remove edge one by one
    //           after edge removal, remove the nodes itself
    public void removeNodes(Tweet tweet) {
        List<String> nodeNames = tweet.getNodeNames();
        for (int i = 0; i < nodeNames.size()-1; i++) {
            for (int j = i+1; j < nodeNames.size(); j++) {
                removeEdge(tweet.getTimestamp(), nodeNames.get(i), nodeNames.get(j));
            }
        }

        for (String nodeName : tweet.getNodeNames()) {
            removeNode(tweet.getTimestamp(), nodeName);
        }
    }


    // input: timestamp and the pair of node names that needs to be removed
    // output: none
    // function: remove edges between the two giving nodes, and keep counting on edges
    public void removeEdge(long timestamp, String name1, String name2) {
        Node node1 = nameNode.get(name1);
        Node node2 = nameNode.get(name2);
        //System.out.print("Remove Edge: " + timestamp + "=>" + name1 + ", " + name2 + "\n");
        neighbourCnt -= node1.removeNeighbour(timestamp, node2);
        neighbourCnt -= node2.removeNeighbour(timestamp, node1);
    }


    // input: none
    // output: the average neighbour of all nodes
    //         return 0.0 if the graph is empty
    public double getHashtagAvg() {
        if (nodeCnt == 0) return 0.0;
        return (double)neighbourCnt / (double)nodeCnt;
    }


    // print entire Graph
    public void print() {
        System.out.print("Total Edges: " + neighbourCnt + ";    ");
        System.out.print("Total Nodes: " + nodeCnt + "\n");
        List<String> keys = new ArrayList<String>();
        for (String key : nameNode.keySet()) {
            keys.add(key);
        }

        int edgeCnt = 0;
        Collections.sort(keys);
        for (String key : keys) {
            edgeCnt += nameNode.get(key).print();
        }

        if (edgeCnt != neighbourCnt || nameNode.keySet().size() != nodeCnt) {
            System.out.println("Error: not matching edge counts in graph! " + edgeCnt + " vs. " + neighbourCnt);
            System.out.println("Error: not matching node counts in graph! " + nameNode.keySet().size() + " vs. " + nodeCnt);
            System.exit(2);
        }
        System.out.print("End of Graph printing\n");
    }

}
