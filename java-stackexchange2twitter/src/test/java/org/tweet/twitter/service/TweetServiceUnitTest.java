package org.tweet.twitter.service;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.common.service.LinkService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.social.twitter.api.Tweet;
import org.stackexchange.util.TwitterTag;
import org.tweet.test.TweetFixture;

import com.google.common.collect.Lists;

public class TweetServiceUnitTest {

    private TweetService instance;

    @Before
    public final void before() {
        instance = new TweetService();
        instance.linkService = new LinkService();
    }

    // tests

    // check validity

    @Test
    public final void givenTweetScenario1_whenCheckingValidity_thenValid() {
        assertTrue(instance.isTweetFullValid("RT @jtrnix: StarCluster 0.94 has been released! Tons of bug fixes/improvements and several new features: http://t.co/UY1eqvgnSe #EC2 #HPC #..."));
    }

    @Test
    public final void givenTweetScenario2_whenCheckingValidity_thenValid() {
        assertTrue(instance.isTweetFullValid("nrepl.el has new maintainers (me, @technomancy, @hugoduncan and @sanityinc) and a new home https://t.co/cHa0XbIEOR Please,RT #clojure #emacs"));
    }

    @Test
    public final void givenTweetScenario3_whenCheckingValidity_thenValid() {
        assertTrue(instance.isTweetFullValid("RT @zend: The latest release of Zend Studio - 10.1.0 - makes it easier than ever to work with #PHP libraries and Zend Server http://t.co/3k..."));
    }

    @Test
    public final void givenTweetScenario4_whenCheckingValidity_thenValid() {
        assertTrue(instance.isTweetFullValid("RT @sofisticlara: Awesome! Regular Expressions Guide for SEO & Analytics (http://t.co/9ZURwNKDiZ) via @SEOTakeaways @seohimanshu #Regex #SE..."));
    }

    // language checks - for tweeting

    @Test
    public final void givenUserLanguageIsRejectedForTweeting_whenCheckingTweet1_thenRejected() {
        final Tweet testTweet = TweetFixture.createTweet(3, "it", "en");
        final boolean pass = instance.passesLanguageChecksForTweeting(testTweet, randomAlphabetic(5));
        assertThat(pass, is(false));
    }

    @Test
    public final void givenUserLanguageIsUndecidedForTweeting_whenCheckingTweet1_thenRejected() {
        final Tweet testTweet = TweetFixture.createTweet(3, "zmv", "en");
        final boolean pass = instance.passesLanguageChecksForTweeting(testTweet, randomAlphabetic(5));
        assertThat(pass, is(false));
    }

    @Test
    public final void givenUserLanguageIsAcceptedForTweeting_whenCheckingTweet1_thenAccepted() {
        final Tweet testTweet = TweetFixture.createTweet(3, "en", "en");
        final boolean pass = instance.passesLanguageChecksForTweeting(testTweet, randomAlphabetic(5));
        assertThat(pass, is(true));
    }

    // language checks - for analysis

    @Test
    public final void givenUserLanguageIsRejectedForAnalysis_whenCheckingTweet1_thenRejected() {
        final Tweet testTweet = TweetFixture.createTweet(3, "es", "en");
        final boolean pass = instance.passesLanguageChecksForAnalysis(testTweet, randomAlphabetic(5));
        assertThat(pass, is(false));
    }

    @Test
    public final void givenUserLanguageIsUndecidedForAnalysis_whenCheckingTweet1_thenRejected() {
        final Tweet testTweet = TweetFixture.createTweet(3, "zmv", "en");
        final boolean pass = instance.passesLanguageChecksForAnalysis(testTweet, randomAlphabetic(5));
        assertThat(pass, is(false));
    }

    @Test
    public final void givenUserLanguageIsAcceptedForAnalysis_whenCheckingTweet1_thenAccepted() {
        final Tweet testTweet = TweetFixture.createTweet(3, "en", "en");
        final boolean pass = instance.passesLanguageChecksForAnalysis(testTweet, randomAlphabetic(5));
        assertThat(pass, is(true));
    }

    // is it worth retweeting

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario4_thenRejected() {
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink("#PHP \"@MarceloLopez84: Heres proof!! pic.twitter.com/HDHf2t1W4A\""));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario5_thenRejected() {
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink("#dark #cloud http://instagram.com/p/b6EFkGPgok/"));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario6_thenRejected() {
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink("#PHP EXCLUSIVE: Gareth Bale wants to join Manchester United http://www.dailystar.co.uk/sport/football/327439/EXCLUSIVE-Gareth-Bale-wants-to-join-Manchester-United \u2026 (Daily Star)"));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario7_thenRejected() {
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink("RT if you want to use minion case like this #iPhone #iOS #DespicableMe2 pic.twitter.com/IviFckCgba"));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario8_thenRejected() {
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink("Spelersbus #GAE pic.twitter.com/2USMMHDbyJ"));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario9_thenRejected() {
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink("#Google http://t.co/VmandYLIu4"));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario10_thenRejected() {
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink("RT this if you want me to follow you :) add me on #Facebook too https://t.co/j9YzugsgRo  x"));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario11_thenRejected() {
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink("#BITCOIN #ALGORITHM TRADING #PROFITS #TRADES #EASYTRADING #BOTS #FOREX http://goo.gl/fb/Ou5FT  - via @censorednewsnow"));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario12_thenRejected() {
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink("Black is the new White #Oklahoma #Racial #Math @Talkmaster pic.twitter.com/v1UeNKjf3X"));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario13_thenRejected() {
        final String text = "#ANDROID USERS GO HERE ->https://play.google.com/store/music/album?id=Bh24gh6kjkmpirwjb3r3axtpvg4&tid=song-T5nduydhjkaastuk6vcaymtutde … @nwdragonwing @YungSicco @step_big #SUPPORT #INDIE #MUSIC #PRO2COLRECORDS";
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink(text));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario14_thenRejected() {
        final String text = "What do all of those #facebook likes mean? Dig deeper into social with analytics: http://bit.ly/UtM2ao  Free whitepaper (sponsored)";
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink(text));
    }

    @Test
    public final void givenTextShouldNotBeTweeted_whenCheckingOnScenario15_thenRejected() {
        final String text = "PHP and #MySQL for Beginners #udemy #PHP #MySQL #beginner #database Only $21 http://ow.ly/pj4Ki";
        assertFalse(instance.isTweetWorthRetweetingByTextWithLink(text));
    }

    // pre-process

    @Test
    public final void givenTweet_whenPreProcessingTweet_thenNoExceptions() {
        instance.hashtagWordsFullTweet(randomAlphabetic(50), Lists.<String> newArrayList());
    }

    @Test
    public final void givenNoWordsToHash_whenPreProcessingTweet_thenNoChanges() {
        final String originalTweet = randomAlphabetic(50);
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.<String> newArrayList());
        assertThat(originalTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenSomeWordsToHash_whenTweetDoesNotContainTheWord_thenNoChanges() {
        final String originalTweet = "word1 word2 word3";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("otherword"));
        assertThat(originalTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenSomeWordsToHash_whenTweetDoesContainTheWord_thenTweetChanged1() {
        final String originalTweet = "word1 word2 word3";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("word2"));
        assertThat(originalTweet, not(equalTo(processedTweet)));
    }

    @Test
    public final void givenSomeWordsToHash_whenTweetDoesContainTheWord_thenTweetChanged2() {
        final String originalTweet = "Testing with Guava";
        final String targetResultTweet = "Testing with #Guava";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("guava"));
        assertThat(targetResultTweet, equalTo(processedTweet));
    }

    @Test
    @Ignore("ignored for now - the max lenght functionality was disabled because it was counting to many characters in the link")
    public final void givenSomeWordsToHash_whenTweetIsToLongToHash_thenNoChange() {
        final String prefix = randomAlphabetic(122);
        final String originalTweet = prefix + "Testing with Guava";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("guava"));
        assertThat(originalTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenRealCaseScenario1_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "Hibernate + Spring using multiple datasources? - http://stackoverflow.com/questions/860918/hibernate-spring-using-multiple-datasources";
        final String targetTweet = "Hibernate + #Spring using multiple datasources? - http://stackoverflow.com/questions/860918/hibernate-spring-using-multiple-datasources";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("spring"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenRealCaseScenario2_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "How are Anonymous (inner) classes used in Java? - http://stackoverflow.com/questions/355167/how-are-anonymous-inner-classes-used-in-java";
        final String targetTweet = "How are Anonymous (inner) classes used in #Java? - http://stackoverflow.com/questions/355167/how-are-anonymous-inner-classes-used-in-java";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("java"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenRealCaseScenario3_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "What's the best Web interface for Git repositories? - http://stackoverflow.com/questions/438163/whats";
        final String targetTweet = "What's the best Web interface for #Git repositories? - http://stackoverflow.com/questions/438163/whats";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("git"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenRealCaseScenario4_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "Programmatically showing a View from an Eclipse Plug-in - http://stackoverflow.com/questions/171824/programmatically-showing";
        final String targetTweet = "Programmatically showing a View from an #Eclipse Plug-in - http://stackoverflow.com/questions/171824/programmatically-showing";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("eclipse"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenRealCaseScenario5_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "Static return type of Scala macros - http://stackoverflow.com/questions/13669974/static-return-type-of-scala-macros";
        final String targetTweet = "Static return type of #Scala macros - http://stackoverflow.com/questions/13669974/static-return-type-of-scala-macros";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("scala"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenRealCaseScenario6_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "Tweet: 25+ Highly Useful #jQuery Plugins Bringing Life back to HTML Tables http://t.co/smTFbBO8l4";
        final String targetTweet = "Tweet: 25+ Highly Useful #jQuery Plugins Bringing Life back to HTML Tables http://t.co/smTFbBO8l4";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("jquery"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenRealCaseScenario7_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "Fast vector math in #Clojure / Incanter - http://stackoverflow.com/questions/3814048/fast-vector-math-in-clojure-incanter";
        final String targetTweet = "Fast vector math in #Clojure / #Incanter - http://stackoverflow.com/questions/3814048/fast-vector-math-in-clojure-incanter";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList(TwitterTag.clojure.name(), "incanter"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenRealCaseScenario8_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "Problem installing Maven plugin (m2eclipse) in Eclipse (Galileo) - http://stackoverflow.com/questions/2802";
        final String targetTweet = "Problem installing #Maven plugin (m2eclipse) in Eclipse (Galileo) - http://stackoverflow.com/questions/2802";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("maven"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenRealCaseScenario9_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "\"string could not resolved\" error in eclipse for C++ - http://stackoverflow.com/questions/7905025/string-could-not-resolved-error-in-eclipse-for-c";
        final String targetTweet = "\"string could not resolved\" error in eclipse for C++ - http://stackoverflow.com/questions/7905025/string-could-not-resolved-error-in-eclipse-for-c";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("eclipse"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    @Ignore("corner case")
    public final void givenRealCaseScenario10_whenHashtagsAreAdded_thenCorrect() {
        final String originalTweet = "Understanding EJB3/JPA container-level transactions and isolation level - http://stackoverflow.com/questions/4136852/understanding-ejb3-jpa-container-level-transactions-and-isolation-level";
        final String targetTweet = "Understanding #EJB3/#JPA container-level transactions and isolation level - http://stackoverflow.com/questions/4136852/understanding-ejb3-jpa-container-level-transactions-and-isolation-level";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("ejb3", "jpa"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenTweetWouldGoOverLimitIfHashtagsWereAddedScenario1_whenHashtagsAreAdded_thenCorrectlyNotChanged() {
        final String originalTweet = "\"Infuse fun math bla b in the classroom\" MT @edutopia: via @blkgrlphd: Do the #Math Right http://edut.to/1atgftH  #mathchat #studentengagement";
        final String targetTweet = "\"Infuse fun math bla b in the classroom\" MT @edutopia: via @blkgrlphd: Do the #Math Right http://edut.to/1atgftH  #mathchat #studentengagement";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("math"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenTweetWouldGoOverLimitIfHashtagsWereAddedScenario2_whenHashtagsAreAdded_thenCorrectlyNotChanged() {
        final String originalTweet = "Best Way to Inject Hibernate Session by Spring 3 - http://stackoverflow.com/questions/4699381/best-way-to-inject-hibernate-session-by-spring-3";
        final String targetTweet = "Best Way to Inject Hibernate Session by Spring 3 - http://stackoverflow.com/questions/4699381/best-way-to-inject-hibernate-session-by-spring-3";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("spring", "hibernate"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    @Test
    public final void givenTweetWouldGoOverLimitIfHashtagsWereAddedScenario3_whenHashtagsAreAdded_thenCorrectlyNotChanged() {
        final String originalTweet = "Hot deploying changes with Netbeans, Maven, and Glassfish - http://stackoverflow.com/questions/2290935/hot-deploying-changes-with-netbeans-maven-and-glassfish";
        final String targetTweet = "Hot deploying changes with Netbeans, Maven, and Glassfish - http://stackoverflow.com/questions/2290935/hot-deploying-changes-with-netbeans-maven-and-glassfish";
        final String processedTweet = instance.hashtagWordsFullTweet(originalTweet, Lists.newArrayList("maven", "pom"));
        assertThat(targetTweet, equalTo(processedTweet));
    }

    // is structurally valid

    @Test
    public final void whenCheckedIfStructurallyValidTweet1_theNo() {
        assertFalse(instance.isStructurallyValidForTweeting("#Google http://t.co/VmandYLIu4"));
    }

    @Test
    public final void whenCheckedIfStructurallyValidTweet2_theNo() {
        assertFalse(instance.isStructurallyValidForTweeting("#BITCOIN #ALGORITHM #TRADING #PROFITS #TRADES #EASYTRADING #BOTS #FOREX http://goo.gl/fb/Ou5FT - @censorednewsnow 2"));
    }

    @Test
    public final void whenCheckedIfStructurallyValidTweet1_theYes() {
        assertTrue(instance.isStructurallyValidMinimal("Any easy #REST tutorials for Java? - http://stackoverflow.com/questions/333690/any-easy-rest-tutorials-for-java …"));
    }

    @Test
    public final void whenCheckedIfStructurallyValidTweet2_theYes() {
        assertTrue(instance.isStructurallyValidMinimal("Android ported to the iPhone: http://www.youtube.com/watch?v=5yO2KQHkt4A"));
    }

    @Test
    public final void whenCheckedIfStructurallyValidTweet3_theYes() {
        assertTrue(instance.isStructurallyValidMinimal("Things that I like in #OrientDB, easiness and simplicity: curl -v -u admin:admin http://localhost:2480/query/demo/sql/select from Person"));
    }

}
