/**
 * Author: Cristina Guerrero
 * Date: 10th February 2021
 */

package utils.mapelites;

/**
 * This class manages the dimension of the feature and how its values are organised in buckets
 * taking into consideration the minValue, maxValue and bucketSize.
 * 
 * There are different solutions for this problem and some design questions that affect these:
 * 1) do the minValue are maxValue important enough to be contained in their own independent cell?
 * 2) What happens if the value is lower/higher than minValue/maxValue?
 * 3) What happens with decimal values?
 * 
 * The decisions taken are the following:
 * 1) Yes they are
 * 2) The first and last buckets will contain <=minValue and >=maxValue, respectively
 * 3) They will be rounded before the calculations so 0.3 --> 0; 0.5 --> 1; 99.8 --> 100
 * 
 * The buckets id are calculated for the interval excluding minValue and maxValue so for [minValue + 1, maxValue - 1].
 * This id calculation returns a value between 1 and nBuckets, and it is assume the buckets contain bucketSize numbers.
 * minValue will be assigned id 0 and maxValue will be assigned nBuckets + 1
 * The total number of buckets for the map will be nBuckets + 2
 * 
 * Example 1:
 * minValue = 0; maxValue = 100; bucketSize = 10
 * 
 * id:0 --> [<=0] 
 * id:1 --> [1 - 10] 
 * id:2 --> [11 - 20] 
 * id:3 --> [21 - 30] 
 * id:4 --> [31 - 40]
 * id:5 --> [41 - 50]
 * id:6 --> [51 - 60]
 * id:7 --> [61 - 70]
 * id:8 --> [71 - 80]
 * id:9 --> [81 - 90]
 * id:10 --> [91 - 99]
 * id:11 --> [>=100]
 * 
 * Example 2:
 * minValue = 5; maxValue = 33; bucketSize = 10
 * id:0 --> [<=5]
 * id:1 --> [6 - 15]
 * id:2 --> [16 - 25]
 * id:3 --> [26 - 32]
 * id:4 --> [>=33]
 * 
 * It is possible that that the one-to-last bucket will not contain bucketSize numbers but this is
 * expected and preferable to other options.
 */
public class Buckets {
    
    public static int getMapIdx(Double value, Integer minValue, Integer maxValue, Integer bucketSize) {
        //System.out.println("getMapIdx: " + value + " " + minValue + " "+ maxValue + " "+ bucketSize + " ");
        Double roundedValue = (double) Math.round(value);

        // The buckets id are calculated for the interval excluding minValue and maxValue: [minValue + 1, maxValue - 1]
        // This id calculation would return a value between 1 and nBuckets

        // If the value is the minimum or lower, it is assigned the first bucket for the map: id 0
        if (Double.compare(roundedValue, minValue) <= 0){
            return 0;
        }

        // We calculate the values for the interval that we will use to calculate the ids, which exclude minValue and maxValue
        Integer minInterval = minValue + 1;
        Integer maxInterval = maxValue - 1;

        // If the value is the maximum or higher, it is assigned the last bucket, after the interval buckets 
        if ((maxValue != null) && (Double.compare(roundedValue, maxValue) >= 0)) {
            // It is the last id + 1
            return getIntervalBucketId((double) maxInterval, minInterval, bucketSize) + 1;
        }

        // If it is in the interval, we just return the id
        return getIntervalBucketId(roundedValue, minInterval, bucketSize);
    }

    public static int getMapNBuckets(Integer minValue, Integer maxValue, Integer bucketSize) {
        Integer minInterval = minValue + 1;
        Integer maxInterval = maxValue - 1;

        return getIntervalBucketId((double) maxInterval, minInterval, bucketSize) + 2;
    }

    /**
     * Generate and array with the information of the range of values contained in each bucket
     * @param minValue
     * @param maxValue
     * @param bucketSize
     * @return
     */
    public static String[] getMapRangesInfo(Integer minValue, Integer maxValue, Integer bucketSize) {
        int nTotalBuckets = getMapNBuckets(minValue, maxValue, bucketSize);
        String[] mapRangesInfo = new String[nTotalBuckets];

        // first bucket is for <=minValue
        mapRangesInfo[0] = "[<="+minValue+"]";

        // last bucket is for >=maxValue 
        int lastId = nTotalBuckets-1;
        mapRangesInfo[lastId] = "[>="+maxValue+"]";

        // the rest of buckets values are obtained with the interval calculation∂
        Integer maxInterval = maxValue - 1;
        Integer intervalValue = minValue + 1;

        for (int id = 1; id < lastId; id++) {
            Integer lastValueRange = intervalValue + (bucketSize - 1);

            if(lastValueRange > maxInterval) {
                lastValueRange = maxInterval;
            }

            if (intervalValue == lastValueRange) {
                mapRangesInfo[id] = "[" + intervalValue + "]";
            } else {
                mapRangesInfo[id] = "[" + intervalValue + " - " + lastValueRange + "]";
            }
            
            intervalValue = lastValueRange + 1;
        }

        return mapRangesInfo;
    }

    /**
     * This method returns the id (bucket) that corresponds to the value. 
     * The id starts in 1
     * @param value
     * @param minValue
     * @param bucketSize
     * @return
     */
    private static int getIntervalBucketId(Double value, Integer minValue, Integer bucketSize) {
        return (int) (((value - minValue) / bucketSize) + 1);
    }
}
