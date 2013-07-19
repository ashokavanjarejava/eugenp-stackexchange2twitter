package org.classification.service;

import java.io.IOException;
import java.util.List;

import org.classification.spring.ClassificationConfig;
import org.common.spring.CommonContextConfig;
import org.gplus.spring.GplusContextConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonContextConfig.class, ClassificationConfig.class, GplusContextConfig.class })
public class ClassificationAccuracyServiceLiveTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private ClassificationAccuracyTestService classificationAccuracyService;

    // tests

    // 5000 features: 0.923
    // 10000 features: 0.9xx
    /**
     * - note: the data to be classified has EMPTY type information included in the encoded vector <br/>
     * - so the results are production-like, but not excellent
     */
    @Test
    // @Ignore("long running - ignored by default")
    public final void givenClassifierWasTrained_whenClassifyingTestDataWithoutTypeInfo_thenResultsAreGood() throws IOException {
        final List<Integer> probeCounts = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> featuresCount = Lists.newArrayList(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 15000);

        final int runs = 50;
        for (final Integer features : featuresCount) {
            for (final Integer probes : probeCounts) {
                final double mean = classificationAccuracyService.calculateClassifierAccuracy(runs, probes, features);
                logger.warn("For features= " + features + " and probes= " + probes + " result is= " + mean);
                System.out.println("For features= " + features + " and probes= " + probes + " result is= " + mean);
            }
        }
    }

}