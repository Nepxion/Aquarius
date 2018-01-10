package com.nepxion.aquarius.common.curator.constant;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

public class CuratorConstant {
    public static final String CONFIG_FILE = "config-curator.properties";

    public static final String CONNECT_STRING = "connectString";
    public static final String SESSION_TIMEOUT_MS = "sessionTimeoutMs";
    public static final String CONNECTION_TIMEOUT_MS = "connectionTimeoutMs";

    public static final String RETRY_TYPE = "retryType";
    public static final String RETRY_TYPE_EXPONENTIAL_BACKOFF_RETRY = "exponentialBackoffRetry";
    public static final String RETRY_TYPE_BOUNDED_EXPONENTIAL_BACKOFF_RETRY = "boundedExponentialBackoffRetry";
    public static final String RETRY_TYPE_RETRY_NTIMES = "retryNTimes";
    public static final String RETRY_TYPE_RETRY_FOREVER = "retryForever";
    public static final String RETRY_TYPE_RETRY_UNTIL_ELAPSED = "retryUntilElapsed";

    public static final String PARAMETER_NAME_BASE_SLEEP_TIME_MS = "baseSleepTimeMs";
    public static final String PARAMETER_NAME_MAX_SLEEP_TIME_MS = "maxSleepTimeMs";
    public static final String PARAMETER_NAME_MAX_RETRIES = "maxRetries";
    public static final String PARAMETER_NAME_COUNT = "count";
    public static final String PARAMETER_NAME_SLEEP_MS_BETWEEN_RETRIES = "sleepMsBetweenRetries";
    public static final String PARAMETER_NAME_RETRY_INTERVAL_MS = "retryIntervalMs";
    public static final String PARAMETER_NAME_MAX_ELAPSED_TIME_MS = "maxElapsedTimeMs";
}