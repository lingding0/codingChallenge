#!/usr/bin/env bash

# I'll execute my programs, with the input directory tweet_input and output the files in the directory tweet_output
#python ./src/average_degree.py ./tweet_input/tweets.txt ./tweet_output/output.txt

#rm -f ./src/*.class
#javac -cp ".:/usr/share/java/junit-3.8.2.jar:/usr/share/java/json-simple-1.1.1.jar"  src/TestBench.java src/TweetHashtag.java src/Graph.java src/KeyOfTweetsComparator.java src/KeyOfTweets.java src/Node.java src/Tweet.java src/Tweets.java
#java -cp ./src:/usr/share/java/junit-3.8.2.jar:/usr/share/java/json-simple-1.1.1.jar junit.textui.TestRunner TestBench

rm -f ./src/*.class
javac -cp ".:/usr/share/java/junit-3.8.2.jar:/usr/share/java/json-simple-1.1.1.jar" src/TweetHashtag.java src/Graph.java src/KeyOfTweetsComparator.java src/KeyOfTweets.java src/Node.java src/Tweet.java src/Tweets.java
java -cp ./src:/usr/share/java/json-simple-1.1.1.jar TweetHashtag



