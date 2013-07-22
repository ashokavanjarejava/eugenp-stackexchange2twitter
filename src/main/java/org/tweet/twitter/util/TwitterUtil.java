package org.tweet.twitter.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.common.util.LinkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public final class TwitterUtil {
    final static Logger logger = LoggerFactory.getLogger(TwitterUtil.class);

    final static Splitter splitter = Splitter.on(' ').omitEmptyStrings().trimResults(); // if this would include more chars, then recreating the tweet would not be exact
    final static Joiner joiner = Joiner.on(' ');

    final static List<String> bannedContainsKeywords = Lists.newArrayList(// @formatter:off
        "buy", 
        "freelance", "job", "consulting", "hire", "hiring", "careers", 
        "need", 
        "football", "exclusive",
        "escort", "escorts", "xxx", "porn"
    );// @formatter:on
    final static List<String> bannedStartsWithExprs = Lists.newArrayList(// @formatter:off
            "photo: "
    );// @formatter:on
    final static List<String> bannedExpressions = Lists.newArrayList(// @formatter:off
        "web developer", "web developers", 
        "application engineer", "application engineers", 
        "python developer", "java developer", "php developer", "clojure developer", "c# developer", "c++ developer", 
        "backend developer", "back end developer", "frontend developer", "front end developer", "fullstack developer", "full stack developer", 
        "on strike"
    ); // @formatter:on
    final static List<String> bannedRegExes = Lists.newArrayList(// @formatter:off
        "Get (.)* on Amazon.*", // Get 42% off Secrets of the #JavaScript Ninja on Amazon http://amzn.to/12kkaUn @jeresig
        "I'm broadcasting .* on .*",  // I'm broadcasting #LIVE on #HangWith for #iPhone! Come Hang w/souljaboy! http://bit.ly/hangwsocial
        "Follow us on (Linkedin|Twitter|G+) .*" // Follow us on Linkedin - http://linkd.in/V4Fxa5  #Android #iOS #PS3 #Xbox360 #Apps #GameDev #IDRTG #Video #Game #Developer
    ); // @formatter:on

    final static List<String> bannedTwitterUsers = Lists.newArrayList(// @formatter:off
        "blogginginside" // in German - https://twitter.com/blogginginside 
    ); // @formatter:on

    private TwitterUtil() {
        throw new AssertionError();
    }

    // API

    public static boolean isTweetTextWithoutLinkValid(final String text) {
        final int linkNoInTweet = LinkUtils.extractUrls(text).size();
        if (linkNoInTweet > 1) {
            return false;
        }

        return text.length() <= 122;
    }

    public static boolean isTweetTextWithLinkValid(final String fullTweet) {
        return fullTweet.length() <= 140;
    }

    public static boolean isUserBannedFromRetweeting(final String username) {
        return bannedTwitterUsers.contains(username);
    }

    // pre-processing

    public static String hashtagWords(final String fullTweet, final List<String> wordsToHash) {
        final Iterable<String> tokens = splitter.split(fullTweet);
        // if (fullTweet.length() + countWordsToHash(tokens, wordsToHash) > 140) {
        // return fullTweet;
        // }

        final Iterable<String> transformedTokens = Iterables.transform(tokens, new HashtagWordFunction(wordsToHash));

        final String processedTweet = joiner.join(transformedTokens);
        return processedTweet;
    }

    // retweet logic

    public static String extractTweetFromRt(final String rt) {
        final String result = rt.replaceAll("\\ART @[a-zA-Z0-9_]+ ?[:-] ?", ""); // \A - anchor - matches before start of text block
        return result;
    }

    public static String extractOriginalUserFromRt(final String rt) {
        final Pattern pattern = Pattern.compile("@[a-zA-Z0-9_]*");
        final Matcher matcher = pattern.matcher(rt);
        if (matcher.find()) {
            final String user = matcher.group(0);
            return user.substring(1);
        }

        return null;
    }

    // utils

    static int countWordsToHash(final Iterable<String> tokens, final List<String> lowerCaseWordsToHash) {
        int countWordsToHash = 0;
        for (final String token : tokens) {
            if (lowerCaseWordsToHash.contains(token.toLowerCase())) {
                countWordsToHash++;
            }
        }

        return countWordsToHash;
    }

    public static boolean isTweetBanned(final String text) {
        // by contains keyword
        final List<String> tweetTokens = Lists.newArrayList(Splitter.on(CharMatcher.anyOf(" ,?!:#.")).split(text));
        for (final String tweetToken : tweetTokens) {
            if (TwitterUtil.bannedContainsKeywords.contains(tweetToken.toLowerCase())) {
                logger.debug("Rejecting the following tweet because a token matches one of the banned keywords: token= {}; tweet= \n{}", tweetToken, text);
                return true;
            }
        }

        // by starts with keyword
        for (final String bannedStartsWith : bannedStartsWithExprs) {
            if (text.startsWith(bannedStartsWith)) {
                logger.debug("Rejecting the following tweet because it starts with= {}; tweet= \n{}", bannedStartsWith, text);
                return true;
            }
        }

        // by expression
        final String textLowerCase = text.toLowerCase();
        for (final String bannedExpression : bannedExpressions) {
            if (textLowerCase.contains(bannedExpression)) {
                logger.debug("Rejecting the following tweet because a token matches the banned expression={}; tweet=\n{}", bannedExpression, text);
                return true;
            }
        }

        // by regex
        for (final String bannedRegEx : bannedRegExes) {
            if (text.matches(bannedRegEx)) {
                return true;
            }
        }

        return false;
    }

}
