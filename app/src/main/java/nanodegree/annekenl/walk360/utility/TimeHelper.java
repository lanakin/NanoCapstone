package nanodegree.annekenl.walk360.utility;

public class TimeHelper
{
    public final static long minuteInMilliseconds = 60000;

    public static long nanosecondsToMilliseconds(long nanoseconds) {
        return nanoseconds / 1000000;
    }

    public static long millisecondsToNanoseconds(long milliseconds) {
        return milliseconds * 1000000;
    }

    public static long elapsedWallTimeMillisInMinutes(long startMillis)
    {
        long elapsedTime = System.currentTimeMillis() - startMillis;

        return elapsedTime / minuteInMilliseconds;
    }
}
