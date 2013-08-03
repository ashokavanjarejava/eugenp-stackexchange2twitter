package org.classification.util;

public final class ClassificationSettings {

    /**
     * - TODO: figure out what this is exactly
     */
    public static final int PROBES_FOR_CONTENT_ENCODER_VECTOR = 3;
    public static final int LEARNERS_IN_THE_CLASSIFIER_POOL = 150;
    public static final int FEATURES = 7000;
    /*
    # Actual Scores
    - 5000:
    -- 2 - 0.843
    -- 3 - 0.839
    - 7000: 
    -- 3 - 0.861
    -- 5 - 0.852,0.850
    */

    /*
    # Vary the size of the pool
    - pool =  50 - 0.789
    - pool = 100 - 0.806
    - pool = 150 - 0.812,0.829
    - pool = 200 - 
     * - note: it's not important how well they perform, just how much is the gap between 50-200 
    */

    /*
    # Vary the size of the runs
    - runs =   100 - 
    - runs =   250 - 0.824
    - runs =   500 - 0.823
    - runs =   750 - 
    - runs =  1000 - 0.826
     * - note: it's not important how well they perform, just how much is the gap between runs is 
    */

    /**
     * `_` can be included as well
     * - TODO: potential: / + &
     */
    public static final String TWEET_TOKENIZER = " ,.!?\":()|-";

    // @ is an important signal - do not add to the tokenizer

    private ClassificationSettings() {
        throw new AssertionError();
    }

    // util

}
// features = 10000, pool = 200 => sec 1122 (0.561)
// features = 5000, pool = 150 => sec 559
// features = 5000, pool = 30 => sec 100
