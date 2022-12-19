package org.tweet.test;

import org.classification.data.SpecificClassificationDataUtilIntegrationTest;
import org.classification.service.ClassificationServiceIntegrationTest;
import org.common.service.LinkServiceIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.tweet.meta.service.TweetMetaLocalServiceIntegrationTest;
import org.tweet.twitter.service.TweetMentionServiceIntegrationTest;
import org.tweet.twitter.service.TweetServiceIntegrationTest;

@RunWith(Suite.class)
@SuiteClasses({// @formatter:off
    PersistenceIntegrationTestSuite.class, 
    PropertiesExistIntegrationTestSuite.class,
    
    // classif
    ClassificationServiceIntegrationTest.class,
    SpecificClassificationDataUtilIntegrationTest.class,
    
    // tweet meta
    TweetMetaLocalServiceIntegrationTest.class, 
    
    // tweet
    TweetServiceIntegrationTest.class,
    TweetMentionServiceIntegrationTest.class, 
    
    // stack
    
    // common
    LinkServiceIntegrationTest.class
}) // @formatter:on
// SetupBackupIntegrationTest - not to be included
public final class IntegrationTestSuite {
    //
}
