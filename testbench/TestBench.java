/**
 * Created by leoding on 4/2/2016.
 */

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class TestBench extends TestCase {
    private List<String> inFileName;
    private List<String> outFileName;

    public TestBench(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        inFileName = new ArrayList<String>();
        inFileName.add("./tweet_input/tweets.txt");
        inFileName.add("./tweet_input/testExample.txt");
        inFileName.add("./tweet_input/testfileSimple.txt");

        outFileName = new ArrayList<String>();
        outFileName.add("./tweet_output/tweets.txt");
        outFileName.add("./tweet_output/testExample.txt");
        outFileName.add("./tweet_output/testfileSimple.txt");

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void twoTweets() {
        List<String> expectedAvg = new ArrayList<String>();
        TweetHashtag t = new TweetHashtag();
        expectedAvg.add("1.00");
        expectedAvg.add("2.33");

        List<String> actualAvg = t.tweetHashtag(inFileName.get(0), outFileName.get(0));
        assertEquals(expectedAvg, actualAvg);
    }

    public void testExample() {
        List<String> expectedAvg = new ArrayList<String>();
        TweetHashtag t = new TweetHashtag();
        expectedAvg.add("1.00");
        expectedAvg.add("2.00");
        expectedAvg.add("2.00");
        expectedAvg.add("2.00");
        expectedAvg.add("2.00");
        expectedAvg.add("1.66");
        expectedAvg.add("2.00");
        expectedAvg.add("2.00");
        expectedAvg.add("1.66");

        List<String> actualAvg = t.tweetHashtag(inFileName.get(1), outFileName.get(1));
        assertEquals(expectedAvg, actualAvg);
    }

    public void testStress() {
        List<String> expectedAvg = new ArrayList<String>();
        TweetHashtag t = new TweetHashtag();
        expectedAvg.add("2.00");
        expectedAvg.add("2.40");
        expectedAvg.add("3.14");
        expectedAvg.add("3.14");
        expectedAvg.add("3.00");
        expectedAvg.add("3.00");
        expectedAvg.add("3.00");
        expectedAvg.add("3.14");
        expectedAvg.add("3.42");
        expectedAvg.add("2.66");
        expectedAvg.add("3.33");
        expectedAvg.add("5.00");
        expectedAvg.add("3.66");
        expectedAvg.add("3.66");
        expectedAvg.add("1.00");
        expectedAvg.add("1.33");
        expectedAvg.add("1.66");
        expectedAvg.add("0.00");
        expectedAvg.add("1.00");
        List<String> actualAvg = t.tweetHashtag(inFileName.get(2), outFileName.get(2));
        assertEquals(expectedAvg, actualAvg);
    }

    public static void main(String[] args){
        TestBench basicTest = new TestBench("BasicTest");
        try {
            basicTest.setUp();
        } catch(Exception e) {
            System.err.print("***setup failed!");
        }

        System.out.println("#### Testing two Tweets Only ###");
        basicTest.twoTweets();
        System.out.println("#### Testing Insight Example on Webpage ###");
        basicTest.testExample();
        System.out.println("#### Testing Stress Add-Removal edges###");
        basicTest.testStress();

        System.out.println("    TEST PASSED!");

    }

}

