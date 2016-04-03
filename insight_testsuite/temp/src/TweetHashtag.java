/**
 * Created by leoding on 3/31/2016.
 */

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class TweetHashtag {
    public static final boolean DBG_MODE = false;

    private Graph graph;
    private Tweets tweets;
    private JSONParser parser;
    private long windowSize;

    public TweetHashtag() {
        graph      = new Graph();        // graph to represent hashtag relationship
        tweets     = new Tweets();       // A list of tweets that extracted from input file
        parser     = new JSONParser();   // Json parser to extract info
        windowSize = 60;                 // 60 second sliding window size
    }

    public Graph getGraph() { return this.graph; }
    public Tweets getTweets() { return this.tweets; }

    // input:  extraction from one tweet line, including timestamp and the array of tags
    // output: void
    // steps to handle new tweet line:
    //    1. add new tweet into graph
    //    2. add new tweet into tweets where we keeps all in-window tweets
    //    3. evict all out-of-window tweets from old tweet window
    //    4. remove corresponding nodes and edges from graph once a tweet is out-of-window
    public void addOneTweet(long tweetID, Tweet oneTweet) {
        if (DBG_MODE) System.out.print("Adding ");
        if (DBG_MODE) oneTweet.print();

        if (oneTweet.getTimestamp() <= tweets.getLatestTimestamp() - windowSize) return; // out of window, ignore
        graph.addOneTweet(tweetID, oneTweet);
        tweets.addOneTweet(oneTweet); // add tweet and update timing window
        //tweets.print();
        while (tweets.needsEviction(windowSize)) {
            Tweet removedTweet = tweets.removeTweet();
            if (DBG_MODE) System.out.print("Removing ");
            if (DBG_MODE) removedTweet.print();
            graph.removeNodes(removedTweet);
        }
    }


    // input:  Json hash map object
    // output: epoch time in seconds, used as timestamp for nodes and edges
    //         return null if the timestamp cannot be found or error happens
    public Long extractTime(JSONObject tweet) {
        Object createTime = tweet.get("created_at");
        if (createTime == null) return null;
        String timeStr = createTime.toString();
        DateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        Date date = null;
        try {
            date = df.parse(timeStr);
        } catch (Exception e){ //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return date.getTime()/1000;
    }


    // input:  Json hash map object
    // output: A list of tags without duplicates
    //         return null if the tags cannot be found or error happens
    public List<String> extractNodeNames(JSONObject tweet) {
        JSONObject entities = (JSONObject)tweet.get("entities");
        if (entities == null) return null;
        JSONArray tagArray  = (JSONArray)entities.get("hashtags");
        if (tagArray == null) return null;

        List<String> nodeNames = new ArrayList<String>();

        for (Object tag : tagArray) {
            JSONObject oneTag = (JSONObject)tag;
            String name = oneTag.get("text").toString();
            if (!nodeNames.contains(name)) nodeNames.add(name);
        }
        return nodeNames;
    }


    // input:  A line of tweet read from file
    // output: the extracted tweet
    //         return null if any of the fields is missing or error happens
    public Tweet extractInfo(String line) {
        Object obj = null;
        try {
            obj = parser.parse(line);
        } catch (Exception e){ //Catch exception if any
            System.err.println("Warning: " + e.getMessage());
        }

        if (obj == null) return null;

        JSONObject oneTweet = (JSONObject)obj;
        Long timestamp = extractTime(oneTweet);
        List<String> nodeNames = extractNodeNames(oneTweet);
        if (timestamp == null || nodeNames == null) return null;
        return new Tweet(timestamp, nodeNames);
    }


    // input:  none
    // output: hash tag average from graph
    public double getHashtagAvg() { return graph.getHashtagAvg(); }


    // this method is for unit test only
    public List<String> tweetHashtag(String infilename, String outfilename){

        JSONParser parser = new JSONParser();
        String inputFileName = infilename;

        List<String> res = new ArrayList<String>();

        BufferedWriter bufferedWriter = null;
        NumberFormat formatter = new DecimalFormat("#0.00");
        formatter.setRoundingMode(RoundingMode.FLOOR);

        try {
            FileInputStream fstream = new FileInputStream(inputFileName);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int lineCnt = 0;

            File outFile = new File(outfilename);
            // check if file exist, otherwise create the file before writing
            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            Writer writer = new FileWriter(outFile);
            bufferedWriter = new BufferedWriter(writer);
            long tweetID = 0;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) 	{
                // process tweet one by one
                lineCnt++;
                if (strLine.length() == 0) continue; // skip empty line
                if (strLine.substring(0, 2).equals("//")) continue; // skip comment line (for debugging)
                Tweet oneTweet = extractInfo(strLine);
                if (oneTweet == null) continue;
                addOneTweet(tweetID++, oneTweet); //
                if (DBG_MODE) getGraph().print();
                if (DBG_MODE) getTweets().print();

                if (DBG_MODE) System.out.print("***** avg = " + formatter.format(getHashtagAvg()) + " ****** lineCnt = " + lineCnt + "\n");
                bufferedWriter.write(formatter.format(getHashtagAvg()) + "\n");
                res.add(formatter.format(getHashtagAvg()).toString());

                if (DBG_MODE) System.out.print("----- END of Processing One Tweet Line ------\n\n");
            }
            //Close the input stream
            in.close();
        } catch (Exception e){ //Catch exception if any
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        } finally{
            try{
                if(bufferedWriter != null) bufferedWriter.close();
            } catch(Exception ex){
            }
        }

        return res;
    }


    //
    // main program
    //
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        String inputFileName = "./tweet_input/tweets.txt";
        TweetHashtag twt = new TweetHashtag();

        BufferedWriter bufferedWriter = null;
        NumberFormat formatter = new DecimalFormat("#0.00");
        formatter.setRoundingMode(RoundingMode.FLOOR);

        try {
            FileInputStream fstream = new FileInputStream(inputFileName);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int lineCnt = 0;

            File outFile = new File("./tweet_output/output.txt");
            // check if file exist, otherwise create the file before writing
            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            Writer writer = new FileWriter(outFile);
            bufferedWriter = new BufferedWriter(writer);
            long tweetID = 0;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) 	{
                // process tweet one by one
                lineCnt++;
                if (strLine.length() == 0) continue; // skip empty line
                if (strLine.substring(0, 2).equals("//")) continue; // skip comment line (for debugging)
                Tweet oneTweet = twt.extractInfo(strLine);
                if (oneTweet == null) continue;
                twt.addOneTweet(tweetID++, oneTweet); //
                if (DBG_MODE) twt.getGraph().print();
                if (DBG_MODE) twt.getTweets().print();

                if (DBG_MODE) System.out.print("***** avg = " + formatter.format(twt.getHashtagAvg()) + " ****** lineCnt = " + lineCnt + "\n");
                bufferedWriter.write(formatter.format(twt.getHashtagAvg()) + "\n");

                if (DBG_MODE) System.out.print("----- END of Processing One Tweet Line ------\n\n");
            }
            //Close the input stream
            in.close();
        } catch (Exception e){ //Catch exception if any
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        } finally{
            try{
                if(bufferedWriter != null) bufferedWriter.close();
            } catch(Exception ex){
            }
        }
    }

}

