##Insight Data Engineering - Coding Challenge Explaination - by Ling Ding
=======================================================================

### How to run the test

   * cd ./insight_testsuite
   * source run_tests.sh

     The three sets of test will run and print out the test result.

### External Java library needed to parse Json input file at ./lib/json-simple-1.1.1.jar
   
   [http://www.java2s.com/Code/JarDownload/json-simple/json-simple-1.1.1.jar.zip]

### Design Archetecture diagram
>                                     _____________________            
>                                    /                     \ 
>                                    | Input Tweet.txt     | 
>                                    \_____________________/
>                                                | 
>                                                | one line per iteration
>                                      __________V__________                           
>                                     /                     \  
>                                     |                     |  
>                one Tweet            |      Json File      |  
>                at a time -----------|      Parser         |-------- 
>                          |          |(Class TweetHashTag) |       |   
>                          |          |                     |       |  
>                          |          \_____________________/       |     
>                          |                                        |                
>                          |                                        | add new                 
>                          |                                        | node and               
>                          |                                        | edges                   
>                          |                                        |               
>                   _______V_____________                  _________V__________                 
>                  /                     \               /                     \   
>                  |                     |  Tweets to be |                     |   
>                  |   Sliding Window    |  Evicted      | Tag Relationship    |   
>                  |   Tracker           |-------------->| Graph               |   
>                  |   (class Tweets)    |  remove nodes | (class Graph)       |   
>                  |                     |  and edges    |                     |   
>                  \_____________________/               \_____________________/   
>                                                                   |               
>                                                                   |               
>                                                                   |               
>                                                                   V               
>                                                               Average Grade                    
>                                                                                   

### Class difinition and functionality

   1. TweetHashtag
      * File parser and output generator. TweetHashtag serves as the central control of the entire process.
      * It reads one line from file, parse the content for of a tweet, send to Tag Ralationship Graph(TRG) and Sliding Window Tracker(SWT).
      * TRG adds new nodes and new edges based on new tweet, while SWT add new tweet and update it's sliding window
      * Once the sliding window is moved, SWT remove out-of-window tweets one by one. SWT also send the evicted tweet to TRG for node and edge removal one by one.
      * TRG removes internal nodes and edges based on timestamp of the evicted tweets.
      * After node and edge removal, TRG provides new rolling average degree, based on its internal tracked node count and edge count.

   2. Tweets
      * Serves as Sliding Window Tracker(SWT).
      * SWT use Sorted Map to keep all the live tweets in sliding window.
      * New tweets will be added into sliding window only if it is above the lower bound of the curent sliding window
      * If new tweets timestamp is later than current upper bound of the sliding window, the sliding window will move to match its upper bound to current new timestamp
      * A list of tweets was in time window may be out of window due to the sliding of the window, all of them need to be evicted one by one
      * Once an out-of-window tweet is evicted(removed) from Sorted Map, it is send to TRG for corresponding node and edge removal.

   3. Graph
      * Serves as Tag Relationship Graph(TRG).
      * TRG keeps a Hash Map for name - node searching, and keeps internal tracking on node and edge counts.
      * TRG add/remove nodes and edges based on file parser and SWT result.

   4. Node
      * Node keeps info for each node and its edges (neighbours).
      * all the edge operations are executed by Node class

### Implementation challenge and corner cases

   1. Multiple tweets may come simultaneously. that means there could be multiple tweets share the same timestamp. The implementation needs to distinguish them in Sorted Map data structure. One extra field named "ID" is usedto distinguish the same timestamp tweets.

   2. A node may belongs to multiple tweets. The timestamp of these tweets may be same or different. When evicting a tweet, timestamps needs to be analyzed carefully to avoid improper removal of nodes and edges

### Performance analysis

   1. Sliding Window Tracker (SWT) use tree map to store in-window tweets. Add/Remove time is O(logN). Once an old tweet is out-of-window, remove the tweet from tree to save memory and future access time.

   2. Tag Relationship Graph (TRG) keeps internal node and edge count to avoid scan entire graph to get average degree. Also, out-of-window nodes and edges are removed from TRG to save memory and speed up future process time.

   3. process tweet line by line to avoid big memory usage of storing the entire tweets download file.

### Unit test, testbench is using Junit at ./testbench/TestBench.java

Other than insight provided cases, some other conditions has been manually added and verified:

   1. Multiple tweets happen at the same time.
   2. Sliding window accross mid-night.
   3. Incoming tweet may be out of sliding window lower boundary. We need to ignore this tweet but still generates an average degree.
   4. Incoming tweet may cause new sliding window contains no valid tweets at all.
   5. Incoming tweet may contains single tag that builds no relationship with other nodes
   6. All of the above conditons mixed together, and mixed with normal tweets sequence


                                                                                



