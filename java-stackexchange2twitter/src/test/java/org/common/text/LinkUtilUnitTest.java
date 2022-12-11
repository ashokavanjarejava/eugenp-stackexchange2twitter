package org.common.text;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.common.service.LinkService;
import org.common.util.LinkUtil;
import org.junit.Test;

public class LinkUtilUnitTest {

    // tests

    // extract urls

    @Test
    public final void whenExtractingUrlFromContentScenario1_thenNoExceptions() {
        final String content = "For functional programming fans: <span class=\"proflinkWrapper\"><span class=\"proflinkPrefix\">+</span><a href=\"https://plus.google.com/112870314339261400768\" class=\"proflink\" oid=\"112870314339261400768\">Adam Bard</a></span>�takes a look at Clojure/core.reducers, and how the parallelism expressed in the framework impacts performance. In particular, the new &#39;fold&#39; method (a parallel &#39;reduce&#39; and &#39;combine&#39;) can have major benefits.<br /><br />More on reducers at <a href=\"http://clojure.com/blog/2012/05/15/anatomy-of-reducer.html\" class=\"ot-anchor\" rel=\"nofollow\">clojure.com/blog/2012/05/15/anatomy-of-reducer.html</a>.";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(2));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario4_thenNoExceptions() {
        final String content = "'More http://example.com nonsense!.";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(1));
        assertThat(extractedUrls, hasItem("http://example.com"));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario5_thenNoExceptions() {
        final String content = "Hack routers injecting some like that somewhere: &lt;iframe src=http://admin:admin@192.168.0.1/changeDNS?newDNS=yourDNS";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(1));
        assertThat(extractedUrls, hasItem("http://admin:admin@192.168.0.1/changeDNS?newDNS=yourDNS"));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario6_thenNoExceptions() {
        final String content = "PHP CGI Argument Injection: add -s like in http://domain/file.php";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(1));
        assertThat(extractedUrls, hasItem("http://domain/file.php"));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario7_thenNoExceptions() {
        final String content = "CentOS 5: Upgrading PHP to Version 5.2: http://portalbuilders.pro/PBblog/?p=312 - solving segmentation fault causing Apache httpd to crash";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(1));
        assertThat(extractedUrls, hasItem("http://portalbuilders.pro/PBblog/?p=312"));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario8_thenNoExceptions() {
        final String content = "Setting Up Data Compression (Using mod_deflate): http://portalbuilders.pro/PBblog/?p=200";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(1));
        assertThat(extractedUrls, hasItem("http://portalbuilders.pro/PBblog/?p=200"));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario9_thenNoExceptions() {
        final String content = "Promoting Your WordPress Posts on Twitter: http://portalbuilders.pro/PBblog/?p=164 (integrating Twitter into your WordPress software)";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(1));
        assertThat(extractedUrls, hasItem("http://portalbuilders.pro/PBblog/?p=164"));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario10_thenNoExceptions() {
        final String content = "HTML encoding, URL encoding, and internationalization in PHP: http://portalbuilders.pro/PBblog/?p=179 (on correct string handling in PHP)";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(1));
        assertThat(extractedUrls, hasItem("http://portalbuilders.pro/PBblog/?p=179"));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario11_thenNoExceptions() {
        final String content = "New Book: Code in the Cloud: Programming Google AppEngine: http://PortalBuilder/PBblog/?p=462 - cloud development on Google's AppEngine";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(1));
        assertThat(extractedUrls, hasItem("http://PortalBuilder/PBblog/?p=462"));
    }

    @Test
    public final void whenExtractingUrlFromContentScenario12_thenNoExceptions() {
        final String content = "Internet-Marketing 1000 Gmail PVA needed - repost by yokozuna: Need gmail (http://t.co/x44ZFO3GHp) accounts ver... http://t.co/G3OPIzAJhX";
        final Set<String> extractedUrls = new LinkService().extractUrls(content);
        assertThat(extractedUrls, hasSize(2));
        assertThat(extractedUrls, hasItem("http://t.co/x44ZFO3GHp"));
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
        final String content = "git clone https://github.com/eugenp/stackexchange2twitter.git - cloning the main https://github.com/eugenp/stackexchange2twitter";
        final String mainUrl = new LinkService().extractUrl(content);
        assertThat(mainUrl, equalTo("https://github.com/eugenp/stackexchange2twitter"));
    }

    // banned domain - no

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario1_thenNo() {
        final String url = "http://www.slideshare.net/pcalcado/from-a-monolithic-ruby-on-rails-app-to-the-jvm";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario2_thenNo() {
        final String url = "http://codepen.io/rdallaire/pen/zFjvG?utm_source=buffer&utm_campaign=Buffer&utm_content=buffer33478&utm_medium=twitter";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario3_thenNo() {
        final String url = "http://appleinsider.com/articles/13/08/19/iphone-5c-expected-to-replace-iphone-5-while-apples-iphone-4s-will-live-on";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario4_thenNo() {
        final String url = "http://hub.tutsplus.com/articles/a-web-designers-seo-checklist-including-portable-formats--webdesign-10740";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario5_thenNo() {
        final String url = "http://www.denbagus.net/10-best-j-query-plugins-that-support-e-commerce-website-development";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario6_thenNo() {
        final String url = "http://www.developerdrive.com/2013/08/introducing-css3-multiple-backgrounds/";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario7_thenNo() {
        final String url = "http://www.designdazzling.com/2013/08/perfect-wordpress-corporate-themes-for-website-project/";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(false));
    }

    @Test
    public final void givenUrlShouldNotBeBanned_whenCheckingIfItIsScenario8_thenNo() {
        final String url = "http://damonmiller.github.io/esapi4cf/";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(false));
    }

    // banned domain - yes

    @Test
    public final void givenUrlShouldBeBanned_whenCheckingIfItIsScenario1_thenYes() {
        final String url = "http://www.fantasyfootball.de/blogpost1";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(true));
    }

    // domain containing `job`

    @Test
    public final void givenUrlShouldBeBanned_whenCheckingIfItIsScenario2_thenYes() {
        final String url = "http://www.jobsforgood.com/Sr__Software_Engineer";
        assertThat(LinkUtil.belongsToBannedDomainTechnical(url), equalTo(true));
    }

}
