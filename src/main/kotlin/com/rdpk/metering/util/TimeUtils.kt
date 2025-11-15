package com.rdpk.metering.util

import java.time.Instant

/**
 * Truncate timestamp to window boundary
 * @param windowDurationSeconds Window duration in seconds
 * @return Instant truncated to the start of the window
 */
fun Instant.truncateToWindow(windowDurationSeconds: Long): Instant {
    val epochSeconds = this.epochSecond
    val windowStartSeconds = (epochSeconds / windowDurationSeconds) * windowDurationSeconds
    return Instant.ofEpochSecond(windowStartSeconds)
}

