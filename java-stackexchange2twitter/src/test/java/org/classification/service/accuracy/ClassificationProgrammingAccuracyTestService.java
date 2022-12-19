package org.classification.service.accuracy;

import static org.classification.util.ClassificationSettings.FEATURES;
import static org.classification.util.ClassificationSettings.PROBES_FOR_CONTENT_ENCODER_VECTOR;
import static org.classification.util.Classifiers.PROGRAMMING;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.mahout.classifier.sgd.CrossFoldLearner;
import org.classification.data.ClassificationTestData;
import org.classification.service.ClassificationService;
import org.classification.util.Classifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class ClassificationProgrammingAccuracyTestService {

    @Autowired
    private ClassificationService classificationService;

    public ClassificationProgrammingAccuracyTestService() {
        super();
    }

    // API

    public final double calculateProgrammingClassifierAccuracyDefault(final int runs) throws IOException {
        return calculateProgrammingClassifierAccuracy(runs, PROBES_FOR_CONTENT_ENCODER_VECTOR, FEATURES);
    }

    final double calculateProgrammingClassifierAccuracy(final int runs, final int probes, final int features) throws IOException {
        final List<ImmutablePair<String, String>> testData = ClassificationTestData.programmingAndNonProgrammingTestData();
        final long start = System.nanoTime() / (1000 * 1000 * 1000);
        final List<Double> results = Lists.newArrayList();
        for (int i = 0; i < runs; i++) {
            Collections.shuffle(testData);
            final double percentageCorrect = analyzeProgrammingData(testData, probes, features);
            results.add(percentageCorrect);
            if (i % 100 == 0) {
                System.out.println("Processing 100 ... - " + ((i / 100) + 1));
            }
        }

        final DescriptiveStatistics stats = new DescriptiveStatistics();
        for (final Double stat : results) {
            stats.addValue(stat);
        }
        final double mean = stats.getMean();
        final long end = System.nanoTime() / (1000 * 1000 * 1000);
        System.out.println("Processing time: " + (end - start) + " sec");
        return mean;
    }

    // util

    private final double analyzeProgrammingData(final List<ImmutablePair<String, String>> testData, final int probes, final int features) throws IOException {
        final CrossFoldLearner bestLearner = Classifiers.Programming.trainNewLearnerProgramming(probes, features);
        classificationService.setProgrammingVsNonProgrammingLerner(bestLearner);

        int correct = 0;
        int total = 0;
        for (final Pair<String, String> tweetData : testData) {
            total++;
            final boolean expected = PROGRAMMING.equals(tweetData.getLeft());
            final boolean isTweetMatch = classificationService.isProgramming(tweetData.getRight(), probes, features);
            if (isTweetMatch == expected) {
                correct++;
            }
        }

        final double cd = correct;
        final double td = total;
        final double percentageCorrect = cd / td;
        return percentageCorrect;
    }

}
