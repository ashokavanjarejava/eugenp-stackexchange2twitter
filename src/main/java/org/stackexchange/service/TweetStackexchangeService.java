package org.stackexchange.service;

import java.io.IOException;
import java.util.Map;

import org.common.service.BaseTweetFromSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stackexchange.api.client.QuestionsApi;
import org.stackexchange.api.constants.StackSite;
import org.stackexchange.component.MinStackScoreRetriever;
import org.stackexchange.component.StackExchangePageStrategy;
import org.stackexchange.persistence.dao.IQuestionTweetJpaDAO;
import org.stackexchange.persistence.model.QuestionTweet;
import org.tweet.twitter.component.TwitterHashtagsRetriever;
import org.tweet.twitter.service.TagRetrieverService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

@Service
public final class TweetStackexchangeService extends BaseTweetFromSourceService<QuestionTweet> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private QuestionsApi questionsApi;
    @Autowired
    private IQuestionTweetJpaDAO questionTweetApi;
    @Autowired
    private TagRetrieverService tagService;
    @Autowired
    private StackExchangePageStrategy pageStrategy;
    @Autowired
    private MinStackScoreRetriever minStackScoreRetriever;
    @Autowired
    private TwitterHashtagsRetriever twitterHashtagsRetriever;

    public TweetStackexchangeService() {
        super();
    }

    // API

    // write

    public final void tweetTopQuestionBySite(final StackSite stackSite, final String twitterAccount) throws JsonProcessingException, IOException {
        try {
            tweetTopQuestionBySiteInternal(stackSite, twitterAccount);
        } catch (final RuntimeException runtimeEx) {
            logger.error("Unexpected exception when trying to tweet from stackSite= " + stackSite + " on twitterAccount= " + twitterAccount, runtimeEx);
        }
    }

    public final void tweetTopQuestionBySiteAndTag(final StackSite stackSite, final String twitterAccount) throws JsonProcessingException, IOException {
        String stackTag = null;
        try {
            stackTag = tagService.pickStackTag(twitterAccount);
            final int pageToStartWith = pageStrategy.decidePage(twitterAccount);
            tweetTopQuestionBySiteAndTagInternal(stackSite, twitterAccount, stackTag, pageToStartWith);
        } catch (final RuntimeException runtimeEx) {
            logger.error("Unexpected exception when trying to tweet from stackSite= " + stackSite + " and stackTag= " + stackTag + " on twitterAccount= " + twitterAccount, runtimeEx);
        }
    }

    // util

    /**
     * - not part of the API because it asks for the page
     */
    final void tweetTopQuestionBySite(final StackSite stackSite, final String twitterAccount, final int pageToStartWith) throws JsonProcessingException, IOException {
        try {
            tweetTopQuestionBySiteInternal(stackSite, twitterAccount, pageToStartWith);
        } catch (final RuntimeException runtimeEx) {
            logger.error("Unexpected exception when trying to tweet from stackSite= " + stackSite + " on twitterAccount= " + twitterAccount, runtimeEx);
        }
    }

    /**
     * - not part of the API because it asks for the tag
     */
    final void tweetTopQuestionBySiteAndTag(final StackSite site, final String stackTag, final String twitterAccount) throws JsonProcessingException, IOException {
        try {
            tweetTopQuestionBySiteAndTagInternal(site, twitterAccount, stackTag);
        } catch (final RuntimeException runtimeEx) {
            logger.error("Unexpected exception when trying to tweet from site=" + site + " and stackTag= " + stackTag + " on twitterAccount= " + twitterAccount, runtimeEx);
        }
    }

    /**
     * - not part of the API because it asks for the tag and the page
     */
    /*0*/final void tweetTopQuestionBySiteAndTag(final StackSite stackSite, final String stackTag, final String twitterAccount, final int pageToStartWith) throws JsonProcessingException, IOException {
        try {
            tweetTopQuestionBySiteAndTagInternal(stackSite, twitterAccount, stackTag, pageToStartWith);
        } catch (final RuntimeException runtimeEx) {
            logger.error("Unexpected exception when trying to tweet from stackSite=" + stackSite + " and stackTag= " + stackTag + " on twitterAccount= " + twitterAccount, runtimeEx);
        }
    }

    final void tweetTopQuestionBySiteInternal(final StackSite stackSite, final String twitterAccount) throws JsonProcessingException, IOException {
        final int pageToStartWith = pageStrategy.decidePage(twitterAccount);
        tweetTopQuestionBySiteInternal(stackSite, twitterAccount, pageToStartWith);
    }

    final void tweetTopQuestionBySiteInternal(final StackSite stackSite, final String twitterAccount, final int pageToStartWith) throws JsonProcessingException, IOException {
        logger.debug("Begin trying to tweet from stackSite= {}, on twitterAccount= {}, pageToStartWith= {}", stackSite.name(), twitterAccount, pageToStartWith);

        int currentPage = pageToStartWith;
        boolean tweetSuccessful = false;
        while (!tweetSuccessful) {
            logger.trace("Trying to tweeting from stackSite= {}, on twitterAccount= {}, question from page= {}", stackSite.name(), twitterAccount, currentPage);
            final int maxScoreForQuestionsOnThisAccount = minStackScoreRetriever.minScoreByAccount(twitterAccount);

            final String siteQuestionsRawJson = questionsApi.questions(maxScoreForQuestionsOnThisAccount, stackSite, currentPage);
            tweetSuccessful = tryTweetTopQuestion(stackSite, twitterAccount, siteQuestionsRawJson);
            currentPage++;
        }
    }

    final void tweetTopQuestionBySiteAndTagInternal(final StackSite stackSite, final String twitterAccount, final String stackTag) throws IOException, JsonProcessingException {
        final int pageToStartWith = pageStrategy.decidePage(twitterAccount);
        tweetTopQuestionBySiteAndTagInternal(stackSite, twitterAccount, stackTag, pageToStartWith);
    }

    final void tweetTopQuestionBySiteAndTagInternal(final StackSite stackSite, final String twitterAccount, final String stackTag, final int pageToStartWith) throws IOException, JsonProcessingException {
        logger.debug("Begin trying to tweet from stackSite= {}, on twitterAccount= {}, pageToStartWith= {}", stackSite.name(), twitterAccount, pageToStartWith);

        int currentPage = pageToStartWith;
        boolean tweetSuccessful = false;
        while (!tweetSuccessful) {
            logger.trace("Trying to tweeting from stackSite= {}, on twitterAccount= {}, pageToStartWith= {}", stackSite.name(), twitterAccount, pageToStartWith);
            final int maxScoreForQuestionsOnThisAccount = minStackScoreRetriever.minScore(stackTag, stackSite, twitterAccount);

            final String questionsForTagRawJson = questionsApi.questions(maxScoreForQuestionsOnThisAccount, stackSite, stackTag, currentPage);
            tweetSuccessful = tryTweetTopQuestion(stackSite, twitterAccount, questionsForTagRawJson);
            currentPage++;
        }
    }

    private final boolean tryTweetTopQuestion(final StackSite stackSite, final String twitterAccount, final String siteQuestionsRawJson) throws IOException, JsonProcessingException {
        final JsonNode siteQuestionsJson = new ObjectMapper().readTree(siteQuestionsRawJson);
        if (!isValidQuestions(siteQuestionsJson, twitterAccount)) {
            return false;
        }
        final ArrayNode siteQuestionsJsonArray = (ArrayNode) siteQuestionsJson.get("items");
        for (final JsonNode questionJson : siteQuestionsJsonArray) {
            final String questionId = questionJson.get(QuestionsApi.QUESTION_ID).toString();
            final String title = questionJson.get(QuestionsApi.TITLE).toString();
            final String link = questionJson.get(QuestionsApi.LINK).toString();

            logger.trace("Considering to tweet on twitterAccount= {}, Question= {}", twitterAccount, questionId);
            if (hasThisAlreadyBeenTweeted(new QuestionTweet(questionId, twitterAccount, null))) {
                return false;
            }

            logger.info("Tweeting Question: title= {} with id= {}", title, questionId);
            final boolean success = tryTweetOneDelegator(title, link, questionId, stackSite, twitterAccount);
            if (!success) {
                logger.debug("Tried and failed to tweet on twitterAccount= {}, tweet text= {}", twitterAccount, title);
                continue;
            } else {
                logger.info("Successfully retweeted on twitterAccount= {}, tweet text= {}", twitterAccount, title);
                return true;
            }
        }

        return false;
    }

    private final boolean tryTweetOneDelegator(final String textRaw, final String url, final String questionId, final StackSite site, final String twitterAccount) {
        final Map<String, Object> customDetails = Maps.newHashMap();
        customDetails.put("questionId", questionId);
        customDetails.put("site", site);

        return tryTweetOne(textRaw, url, twitterAccount, customDetails);
    }

    // checks

    private final boolean isValidQuestions(final JsonNode siteQuestionsJson, final String twitterAccount) {
        final JsonNode items = siteQuestionsJson.get("items");
        Preconditions.checkNotNull(items, "For twitterAccount = " + twitterAccount + ", there are no items (null) in the questions json = " + siteQuestionsJson);
        Preconditions.checkState(((ArrayNode) siteQuestionsJson.get("items")).size() > 0, "For twitterAccount = " + twitterAccount + ", there are no items (empty) in the questions json = " + siteQuestionsJson);

        return true;
    }

    // template

    @Override
    protected final boolean tryTweetOne(final String textRaw, final String url, final String twitterAccount, final Map<String, Object> customDetails) {
        final String questionId = (String) customDetails.get("questionId");
        final StackSite site = (StackSite) customDetails.get("site");

        // is it worth it by itself? - yes

        // is it worth it in the context of all the current list of tweets? - yes

        // pre-process
        final String tweetText = tweetService.preValidityProcess(textRaw);

        // is it valid?
        if (!tweetService.isTweetTextValid(tweetText)) {
            logger.debug("Tweet invalid (size, link count) on twitterAccount= {}, tweet text= {}", twitterAccount, tweetText);
            return false;
        }

        // is this tweet pointing to something good? - yes

        // is the tweet rejected by some classifier? - no

        // post-process
        final String processedTweetText = tweetService.postValidityProcess(tweetText, twitterAccount);

        // construct full tweet
        final String fullTweet = tweetService.constructTweetSimple(processedTweetText, url.substring(1, url.length() - 1));

        // tweet
        twitterLiveService.tweet(twitterAccount, fullTweet);

        // mark
        markDone(new QuestionTweet(questionId, twitterAccount, site.name()));

        // done
        return true;
    }

    @Override
    protected final void markDone(final QuestionTweet entity) {
        getApi().save(entity);
    }

    @Override
    protected final boolean hasThisAlreadyBeenTweeted(final QuestionTweet question) {
        final QuestionTweet existingTweet = getApi().findByQuestionId(question.getQuestionId());
        return existingTweet != null;
    }

    @Override
    protected final IQuestionTweetJpaDAO getApi() {
        return questionTweetApi;
    }

}
