package org.common.persistence.setup.upgrades.live.nolonger;

import java.util.List;

import org.common.persistence.setup.AfterSetupEvent;
import org.common.service.live.LinkLiveService;
import org.common.util.LinkUtil.Technical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Component;
import org.stackexchange.util.TwitterAccountEnum;
import org.tweet.meta.persistence.dao.IRetweetJpaDAO;
import org.tweet.meta.persistence.model.Retweet;
import org.tweet.meta.service.TweetMetaLocalService;
import org.tweet.spring.util.SpringProfileUtil;
import org.tweet.twitter.service.AdvancedTweetService;
import org.tweet.twitter.service.live.TwitterReadLiveService;
import org.tweet.twitter.util.TweetUtil;

@Component
@Profile(SpringProfileUtil.DEPLOYED)
class RemoveDuplicateRetweetsUpgrader implements ApplicationListener<AfterSetupEvent>, IRemoveDuplicateRetweetsUpgrader {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Environment env;

    @Autowired
    private IRetweetJpaDAO retweetDao;

    @Autowired
    private AdvancedTweetService tweetService;

    @Autowired
    private TweetMetaLocalService tweetMetaLocalService;

    @Autowired
    private TwitterReadLiveService twitterReadLiveService;

    @Autowired
    private LinkLiveService linkLiveService;

    public RemoveDuplicateRetweetsUpgrader() {
        super();
    }

    //

    @Override
    @Async
    public void onApplicationEvent(final AfterSetupEvent event) {
        if (env.getProperty("setup.upgrade.retweets.removeduplicates.do", Boolean.class)) {
            logger.info("Starting to execute the RemoveDuplicateRetweetsUpgrader Upgrader");
            removeDuplicateLocalRetweets();
            logger.info("Finished executing the RemoveDuplicateRetweetsUpgrader Upgrader");
        }
    }

    // util

    @Override
    public void removeDuplicateLocalRetweets() {
        logger.info("Executing the RecreateMissingRetweetsUpgrader Upgrader");
        for (final TwitterAccountEnum twitterAccount : TwitterAccountEnum.values()) {
            if (twitterAccount.isRt()) {
                try {
                    logger.info("Removing all duplicate retweets of twitterAccount= " + twitterAccount.name());
                    final boolean processedSomething = removeDuplicateRetweetsOnAccount(twitterAccount.name());
                    if (processedSomething) {
                        logger.info("Done removing all duplicate retweets of twitterAccount= " + twitterAccount.name() + "; sleeping for 5 secs...");
                        Thread.sleep(1000 * 5 * 1); // 5 sec
                    }
                } catch (final RuntimeException ex) {
                    logger.error("Unable to remove duplicate retweets of twitterAccount= " + twitterAccount.name(), ex);
                } catch (final InterruptedException threadEx) {
                    logger.error("Unable to remove duplicate retweets of twitterAccount= " + twitterAccount.name(), threadEx);
                }
            }
        }
    }

    @Override
    public boolean removeDuplicateRetweetsOnAccount(final String twitterAccount) {
        final List<Tweet> allTweetsOnAccount = twitterReadLiveService.listTweetsOfAccountMultiRequestRaw(twitterAccount, 3);
        final boolean doneRemovals = removeDuplicateRetweetsOnAccount(allTweetsOnAccount, twitterAccount);
        return doneRemovals;
    }

    private final boolean removeDuplicateRetweetsOnAccount(final List<Tweet> allTweetsForAccount, final String twitterAccount) {
        int countRemovals = 0;
        for (final Tweet tweet : allTweetsForAccount) {
            if (removeDuplicateRetweets(tweet, twitterAccount)) {
                countRemovals++;
            }
        }

        return countRemovals > 0;
    }

    private final boolean removeDuplicateRetweets(final Tweet tweet, final String twitterAccount) {
        try {
            return removeDuplicateRetweetsInternal(tweet, twitterAccount);
        } catch (final RuntimeException ex) {
            final String tweetUrl = "https://twitter.com/" + tweet.getFromUser() + "/status/" + tweet.getId();
            logger.error("Unable to remove duplicates for retweet: " + TweetUtil.getText(tweet) + " from \nlive tweet url= " + tweetUrl, ex);
            return false;
        }
    }

    private final boolean removeDuplicateRetweetsInternal(final Tweet rawTweet, final String twitterAccount) {
        final String rawTweetText = TweetUtil.getText(rawTweet);
        final boolean linkingToSe = linkLiveService.countLinksToAnyDomain(rawTweet, Technical.seDomains) > 0;
        if (linkingToSe) {
            logger.debug("Tweet is linking to Stack Exchange - not a retweet= {}", rawTweetText);
            return false;
        }

        final String preProcessedText = tweetService.processPreValidity(rawTweetText);
        final String goodText = tweetService.postValidityProcessTweetTextWithUrl(preProcessedText, twitterAccount);

        final boolean cleanedSomethingByExactMatch = cleanRetweetExactMatches(goodText, twitterAccount);
        final boolean cleanedSomethingBeyondExactMatch = cleanRetweetRelaxedMatches(goodText, twitterAccount);

        return cleanedSomethingByExactMatch || cleanedSomethingBeyondExactMatch;
    }

    private final boolean cleanRetweetExactMatches(final String goodText, final String twitterAccount) {
        final List<Retweet> foundRetweets = retweetDao.findAllByTextAndTwitterAccount(goodText, twitterAccount);
        if (foundRetweets.size() <= 1) {
            return false;
        }
        final Retweet keepingOne = foundRetweets.remove(0);
        for (final Retweet duplicateRetweet : foundRetweets) {
            retweetDao.delete(duplicateRetweet);
        }
        logger.info("Removed on twitterAccount= {} - {} retweet= {}", twitterAccount, foundRetweets.size(), keepingOne.getText());
        return true;
    }

    private final boolean cleanRetweetStrictMatches(final String goodText, final String twitterAccount) {
        // final List<Retweet> allLocalTweetsStrict = tweetMetaLocalService.findLocalCandidatesStrict(goodText, twitterAccount);
        final List<Retweet> allLocalTweets = tweetMetaLocalService.findLocalCandidatesStrict(goodText, twitterAccount);
        if (allLocalTweets.size() <= 1) {
            return false;
        } else {
            final Retweet exactMatch = retweetDao.findOneByTextAndTwitterAccount(goodText, twitterAccount);
            if (exactMatch == null) {
                logger.error("Found relative matches, but no exact match to prefer (and delete the rest)");
                return false;
            }
            allLocalTweets.remove(exactMatch);
            for (final Retweet remainingMatch : allLocalTweets) {
                retweetDao.delete(remainingMatch);
            }
            logger.warn("Removed {} partial match retweets", allLocalTweets.size());
            return true;
        }

        // logger.info("Removed on twitterAccount= {} - {} retweet= {}", twitterAccount, allLocalTweetsRelaxed.size(), keepingOne.getText());
    }

    private final boolean cleanRetweetRelaxedMatches(final String goodText, final String twitterAccount) {
        // final List<Retweet> allLocalTweetsStrict = tweetMetaLocalService.findLocalCandidatesStrict(goodText, twitterAccount);
        final List<Retweet> allLocalTweets = tweetMetaLocalService.findLocalCandidatesRelaxed(goodText, twitterAccount);
        if (allLocalTweets.size() <= 1) {
            return false;
        } else {
            final Retweet exactMatch = retweetDao.findOneByTextAndTwitterAccount(goodText, twitterAccount);
            if (exactMatch == null) {
                logger.error("Found relative matches, but no exact match to prefer (and delete the rest)");
                return false;
            }
            allLocalTweets.remove(exactMatch);
            for (final Retweet remainingMatch : allLocalTweets) {
                retweetDao.delete(remainingMatch);
            }
            logger.warn("Removed {} partial match retweets", allLocalTweets.size());
            return true;
        }

        // logger.info("Removed on twitterAccount= {} - {} retweet= {}", twitterAccount, allLocalTweetsRelaxed.size(), keepingOne.getText());
    }

}
