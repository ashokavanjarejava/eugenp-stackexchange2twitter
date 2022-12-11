package org.rss.service;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rss.persistence.model.RssEntry;
import org.rss.spring.RssContextConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tweet.spring.util.SpringProfileUtil;

import com.sun.syndication.io.FeedException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RssContextConfig.class })
@ActiveProfiles(SpringProfileUtil.LIVE)
public class RssServiceReadOnlyLiveTest {

    @Autowired
    private RssService rssService;

    // tests

    @Test
    public final void whenConsumingAnRssFeed_theenFeedsAreExtracted() throws IOException, IllegalArgumentException, FeedException {
        final List<RssEntry> feeds = rssService.extractTitlesAndLinks("http://feeds.feedburner.com/baeldung");
        assertThat(feeds, notNullValue());
        assertThat(feeds, not(empty()));

        for (final RssEntry feed : feeds) {
            System.out.println(feed);
        }
    }

}
