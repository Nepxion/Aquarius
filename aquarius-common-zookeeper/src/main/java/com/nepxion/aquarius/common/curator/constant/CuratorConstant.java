package com.nepxion.aquarius.common.curator.constant;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

public class CuratorConstant {
    public static final String CONNECT_STRING = "curator.connectString";
    public static final String SESSION_TIMEOUT_MS = "curator.sessionTimeoutMs";
    public static final String CONNECTION_TIMEOUT_MS = "curator.connectionTimeoutMs";

    public static final String RETRY_TYPE = "curator.retryType";

    public static final String RETRY_TYPE_EXPONENTIAL_BACKOFF_RETRY_BASE_SLEEP_TIME_MS = "curator.exponentialBackoffRetry.baseSleepTimeMs";
    public static final String RETRY_TYPE_EXPONENTIAL_BACKOFF_RETRY_MAX_RETRIES = "curator.exponentialBackoffRetry.maxRetries";
    public static final String RETRY_TYPE_BOUNDED_EXPONENTIAL_BACKOFF_RETRY_BASE_SLEEP_TIME_MS = "curator.boundedExponentialBackoffRetry.baseSleepTimeMs";
    public static final String RETRY_TYPE_BOUNDED_EXPONENTIAL_BACKOFF_RETRY_MAX_SLEEP_TIME_MS = "curator.boundedExponentialBackoffRetry.maxSleepTimeMs";
    public static final String RETRY_TYPE_BOUNDED_EXPONENTIAL_BACKOFF_RETRY_MAX_RETRIES = "curator.boundedExponentialBackoffRetry.maxRetries";
    public static final String RETRY_TYPE_RETRY_NTIMES_COUNT = "curator.retryNTimes.count";
    public static final String RETRY_TYPE_RETRY_NTIMES_SLEEP_MS_BETWEEN_RETRIES = "curator.retryNTimes.sleepMsBetweenRetries";
    public static final String RETRY_TYPE_RETRY_FOREVER_RETRY_INTERVAL_MS = "curator.retryForever.retryIntervalMs";
    public static final String RETRY_TYPE_RETRY_UNTIL_ELAPSED_MAX_ELAPSED_TIME_MS = "curator.retryUntilElapsed.maxElapsedTimeMs";
    public static final String RETRY_TYPE_RETRY_UNTIL_ELAPSED_SLEEP_MS_BETWEEN_RETRIES = "curator.retryUntilElapsed.sleepMsBetweenRetries";
}