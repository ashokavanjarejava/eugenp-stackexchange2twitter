package org.common.text;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.common.service.LinkService;
import org.common.util.LinkUtil;
import org.junit.Test;

public class LinkUtilUnitTest {

    // tests

    // extract urls

    @Test
    public final void whenExtractingUrlFromContentScenario1_thenNoExceptions() {
        final String content = "For functional programming fans: <span class=\"proflinkWrapper\"><span class=\"proflinkPrefix\">+</span><a href=\"https://plus.google.com/112870314339261400768\" class=\"proflink\" oid=\"112870314339261400768\">Adam Bard</a></span>�takes a look at Clojure/core.reducers, and how the parallelism expressed in the framework impacts performance. In particular, the new &#39;fold&#39; method (a parallel &#39;reduce&#39; and &#39;combine&#39;) can have major benefits.<br /><br />More on reducers at <a href=\"http://clojure.com/blog/2012/05/15/anatomy-of-reducer.html\" class=\"ot-anchor\" rel=\"nofollow\">clojure.com/blog/2012/05/15/anatomy-of-reducer.html</a>.";
        final List<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(2));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario2_thenNoExceptions() {
        final String content = "<b>Clojure Koans</b><br /><br />$ git clone git://<a href=\"http://github.com/functional-koans/clojure-koans.git\" class=\"ot-anchor\">github.com/functional-koans/clojure-koans.git</a> <br />$ cd clojure-koans<br />$ curl <a href=\"https://raw.github.com/technomancy/leiningen/stable/bin/lein\" class=\"ot-anchor\">https://raw.github.com/technomancy/leiningen/stable/bin/lein</a> &gt; script/lein<br />$ chmod +x script/lein<br />$ script/lein deps<br />$ script/run<br /><br />Enlightenment awaits.";
        final List<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(3));
    }

    // determine main url

    @Test
    public final void givenUrls_whenDeterminingMainUrlScenario1_thenCorrectlyDetermined() {
        final String content = "For functional programming fans: <span class=\"proflinkWrapper\"><span class=\"proflinkPrefix\">+</span><a href=\"https://plus.google.com/112870314339261400768\" class=\"proflink\" oid=\"112870314339261400768\">Adam Bard</a></span>�takes a look at Clojure/core.reducers, and how the parallelism expressed in the framework impacts performance. In particular, the new &#39;fold&#39; method (a parallel &#39;reduce&#39; and &#39;combine&#39;) can have major benefits.<br /><br />More on reducers at <a href=\"http://clojure.com/blog/2012/05/15/anatomy-of-reducer.html\" class=\"ot-anchor\" rel=\"nofollow\">clojure.com/blog/2012/05/15/anatomy-of-reducer.html</a>.";
        final String mainUrl = new LinkService().extractUrl(content);
        assertThat(mainUrl, equalTo("http://clojure.com/blog/2012/05/15/anatomy-of-reducer.html"));
    }

    @Test
    public final void givenUrls_whenDeterminingMainUrlScenario2_thenCorrectlyDetermined() {
        final String content = "<b>Clojure Koans</b><br /><br />$ git clone git://<a href=\"http://github.com/functional-koans/clojure-koans.git\" class=\"ot-anchor\">github.com/functional-koans/clojure-koans.git</a> <br />$ cd clojure-koans<br />$ curl <a href=\"https://raw.github.com/technomancy/leiningen/stable/bin/lein\" class=\"ot-anchor\">https://raw.github.com/technomancy/leiningen/stable/bin/lein</a> &gt; script/lein<br />$ chmod +x script/lein<br />$ script/lein deps<br />$ script/run<br /><br />Enlightenment awaits.";
        final String mainUrl = new LinkService().extractUrl(content);
        assertThat(mainUrl, equalTo("https://raw.github.com/technomancy/leiningen/stable/bin/lein"));
    }

    // banned domain

    @Test
    public final void givenUrlShouldBeBanned_whenCheckingIfItIsScenario1_thenYes() {
        final String url = "http://www.fantasyfootball.de/blogpost1";
        assertThat(LinkUtil.belongsToBannedDomain(url), equalTo(true));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario1_thenNo() {
        final String url = "http://www.slideshare.net/pcalcado/from-a-monolithic-ruby-on-rails-app-to-the-jvm";
        assertThat(LinkUtil.belongsToBannedDomain(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario2_thenNo() {
        final String url = "http://codepen.io/rdallaire/pen/zFjvG?utm_source=buffer&utm_campaign=Buffer&utm_content=buffer33478&utm_medium=twitter";
        assertThat(LinkUtil.belongsToBannedDomain(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario3_thenNo() {
        final String url = "http://appleinsider.com/articles/13/08/19/iphone-5c-expected-to-replace-iphone-5-while-apples-iphone-4s-will-live-on";
        assertThat(LinkUtil.belongsToBannedDomain(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario4_thenNo() {
        final String url = "http://hub.tutsplus.com/articles/a-web-designers-seo-checklist-including-portable-formats--webdesign-10740";
        assertThat(LinkUtil.belongsToBannedDomain(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario5_thenNo() {
        final String url = "http://www.denbagus.net/10-best-j-query-plugins-that-support-e-commerce-website-development";
        assertThat(LinkUtil.belongsToBannedDomain(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario6_thenNo() {
        final String url = "http://www.developerdrive.com/2013/08/introducing-css3-multiple-backgrounds/";
        assertThat(LinkUtil.belongsToBannedDomain(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario7_thenNo() {
        final String url = "http://www.designdazzling.com/2013/08/perfect-wordpress-corporate-themes-for-website-project/";
        assertThat(LinkUtil.belongsToBannedDomain(url), equalTo(false));
    }

}
