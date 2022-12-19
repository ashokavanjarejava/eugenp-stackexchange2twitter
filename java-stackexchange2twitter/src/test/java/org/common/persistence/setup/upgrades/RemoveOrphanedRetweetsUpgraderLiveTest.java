package org.common.persistence.setup.upgrades;

import org.common.persistence.setup.upgrades.live.IRemoveOrphanedRetweetsUpgrader;
import org.common.spring.CommonPersistenceJPAConfig;
import org.common.spring.CommonServiceConfig;
import org.common.spring.MyApplicationContextInitializerProv;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keyval.spring.KeyValPersistenceJPAConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.stackexchange.spring.StackexchangePersistenceJPAConfig;
import org.stackexchange.util.TwitterAccountEnum;
import org.tweet.meta.spring.TwitterMetaConfig;
import org.tweet.meta.spring.TwitterMetaPersistenceJPAConfig;
import org.tweet.spring.TwitterConfig;
import org.tweet.spring.TwitterLiveConfig;
import org.tweet.spring.util.SpringProfileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {//@formatter:off
    CommonServiceConfig.class, 
    CommonPersistenceJPAConfig.class, 
    
    KeyValPersistenceJPAConfig.class,
    
    StackexchangePersistenceJPAConfig.class,
    
    TwitterMetaConfig.class,
    TwitterMetaPersistenceJPAConfig.class,
    
    TwitterConfig.class, 
    TwitterLiveConfig.class
})//@formatter:on
@ActiveProfiles({ SpringProfileUtil.DEPLOYED, SpringProfileUtil.LIVE })
public class RemoveOrphanedRetweetsUpgraderLiveTest {

    static {
        System.setProperty(MyApplicationContextInitializerProv.PERSISTENCE_TARGET_KEY, "prod");
    }

    @Autowired
    private IRemoveOrphanedRetweetsUpgrader removeOrphanedRetweetsUpgrader;

    // fixtures

    // tests

    @Test
    public final void whenContextIsBootstrapped_thenNoException() {
        //
    }

    @Test
    public final void whenRemovingOrhpanedRetweetsOnSingleAccount_thenNoExceptions() {
        removeOrphanedRetweetsUpgrader.removeOrphanedRetweetsOnAccount(TwitterAccountEnum.iOSdigest.name());
    }

    @Test
    public final void whenRemovingOrhpanedRetweetsOnAllAccounts_thenNoExceptions() {
        // iOSdigest - issues
        // jQueryDaily - issues
        removeOrphanedRetweetsUpgrader.removeOrphanedRetweets();
    }

}
