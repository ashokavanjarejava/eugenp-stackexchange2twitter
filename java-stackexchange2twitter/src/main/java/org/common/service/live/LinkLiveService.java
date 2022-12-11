package org.common.service.live;

import java.util.List;
import java.util.Set;

import org.common.service.LinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.UrlEntity;
import org.springframework.stereotype.Service;
import org.tweet.spring.util.SpringProfileUtil;
import org.tweet.twitter.util.TweetUtil;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Service
@Profile(SpringProfileUtil.LIVE)
public class LinkLiveService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HttpLiveService httpLiveService;

    @Autowired
    private LinkService linkService;

    public LinkLiveService() {
        super();
    }

    // API

    // count links

    /**
     * - <b>live</b><br/>
     */
    public final boolean containsLinkToDomain(final String tweet, final String domain) {
        final String mainUrl = linkService.extractUrl(tweet);
        final String mainUrlExpanded = httpLiveService.expand(mainUrl);
        if (mainUrlExpanded == null) {
            return false;
        }
        if (!mainUrlExpanded.contains(domain)) {
            return false;
        }

        return true;
    }

    /**
     * - <b>live</b><br/>
     */
    public final int countLinksToDomain(final Iterable<String> tweets, final String domain) {
        int count = 0;
        for (final String tweet : tweets) {
            final String mainUrl = linkService.extractUrl(tweet);
            final String mainUrlExpanded = httpLiveService.expand(mainUrl);
            if (mainUrlExpanded == null) {
                continue;
            }
            if (mainUrlExpanded.contains(domain)) {
                count++;
            }
        }

        return count;
    }

    /**
     * - <b>live</b><br/>
     */
    final int countLinksToAnyDomain(final Iterable<String> tweets, final Iterable<String> domains) {
        int count = 0;
        for (final String tweet : tweets) {
            final String mainUrl = linkService.extractUrl(tweet);
            final String mainUrlExpanded = httpLiveService.expand(mainUrl);
            if (mainUrlExpanded == null) {
                // temporary error - go to debug when I'm done
                // it does happen and it may be worth investigating why - for example:
                logger.error("Unable to expand link= {} \nfrom tweet: {}", mainUrl, tweet);
                continue;
            }
            for (final String domain : domains) {
                if (mainUrlExpanded.contains(domain)) {
                    count++;
                    continue;
                }
            }
        }

        return count;
    }

    /**
     * - <b>live</b><br/>
     */
    public final int countLinksToAnyDomainRaw(final Iterable<Tweet> tweets, final Iterable<String> domains) {
        int count = 0;
        for (final Tweet tweet : tweets) {
            final int linkCount = countLinksToAnyDomain(tweet, domains);
            if (linkCount > 0) {
                count++;
            } else if (linkCount > 1) {
                throw new IllegalStateException();
            }
        }

        return count;
    }

    public final int countLinksToAnyDomain(final Tweet tweet, final Iterable<String> domains) {
        if (tweet.getEntities() == null || tweet.getEntities().getUrls() == null) {
            return countLinksToAnyDomain(tweet, domains);
        }

        final List<UrlEntity> urls = tweet.getEntities().getUrls();
        final List<String> expandedUrls = Lists.transform(urls, new Function<UrlEntity, String>() {
            @Override
            public final String apply(final UrlEntity input) {
                return input.getExpandedUrl();
            }
        });

        int count = 0;

        for (final String domain : domains) {
            for (final String expandedUrl : expandedUrls) {
                if (expandedUrl.contains(domain)) {
                    count++;
                    continue;
                }
            }
        }

        return count;
    }

    public final List<String> getLinksToAnyDomain(final Tweet tweet, final Iterable<String> domains) {
        if (tweet.getEntities() == null || tweet.getEntities().getUrls() == null) {
            return getLinksToAnyDomainRaw(TweetUtil.getText(tweet), domains);
        }

        final List<UrlEntity> urls = tweet.getEntities().getUrls();
        final List<String> expandedUrls = Lists.transform(urls, new Function<UrlEntity, String>() {
            @Override
            public final String apply(final UrlEntity input) {
                return input.getExpandedUrl();
            }
        });

        final List<String> collector = Lists.newArrayList();

        for (final String domain : domains) {
            for (final String expandedUrl : expandedUrls) {
                if (expandedUrl.contains(domain)) {
                    collector.add(expandedUrl);
                    continue;
                }
            }
        }

        return collector;
    }

    /**
     * - <b>live</b><br/>
     */
    public final int countLinksToAnyDomainRaw(final String tweet, final Iterable<String> domains) {
        int count = 0;
        final String mainUrl = linkService.extractUrl(tweet);
        final String mainUrlExpanded = httpLiveService.expand(mainUrl);
        if (mainUrlExpanded == null) {
            // temporary error - go to debug when I'm done
            // it does happen and it may be worth investigating why - for example:
            logger.error("Unable to expand link= {} \nfrom tweet: {}", mainUrl, tweet);
            return 0;
        }
        for (final String domain : domains) {
            if (mainUrlExpanded.contains(domain)) {
                count++;
                continue;
            }
        }

        return count;
    }

    /**
     * - <b>live</b><br/>
     */
    final List<String> getLinksToAnyDomainRaw(final String tweet, final Iterable<String> domains) {
        final List<String> collector = Lists.newArrayList();
        final Set<String> mainUrls = linkService.extractUrls(tweet);
        for (final String mainUrl : mainUrls) {
            final String mainUrlExpanded = httpLiveService.expand(mainUrl);
            if (mainUrlExpanded == null) {
                // temporary error - go to debug when I'm done
                // it does happen and it may be worth investigating why - for example:
                logger.error("Unable to expand link= {} \nfrom tweet: {}", mainUrl, tweet);
                continue;
            }
            for (final String domain : domains) {
                if (mainUrlExpanded.contains(domain)) {
                    collector.add(mainUrlExpanded);
                    continue;
                }
            }
        }

        return collector;
    }

    /**
     * - <b>live</b><br/>
     */
    public final boolean hasLinksToAnyDomain(final String tweetText, final Iterable<String> domains) {
        final Set<String> mainUrls = linkService.extractUrls(tweetText);
        for (final String mainUrl : mainUrls) {
            final String mainUrlExpanded = httpLiveService.expand(mainUrl);
            if (mainUrlExpanded == null) {
                // temporary error - go to debug when I'm done
                // it does happen and it may be worth investigating why - for example:
                logger.error("Unable to expand link= {} \nfrom tweet: {}", mainUrl, tweetText);
                return false;
            }

            for (final String domain : domains) {
                if (mainUrlExpanded.contains(domain)) {
                    return true;
                }
            }
        }

        return false;
    }

    // util

}
