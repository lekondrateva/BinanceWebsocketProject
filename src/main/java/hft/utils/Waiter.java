package hft.utils;

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
public class Waiter {

    public static void waitFor(Runnable assertion, int timeoutMs, int pollIntervalMs) {
        log.info("waitFor(assertion) started");

        try {
            Awaitility.await()
                    .atMost(Duration.ofMillis(timeoutMs))
                    .pollInterval(Duration.ofMillis(pollIntervalMs))
                    .untilAsserted(() -> {
                        log.debug("🔁 Trying assertion...");
                        assertion.run();
                        log.info("✅ Assertion passed");
                    });
        } catch (ConditionTimeoutException e) {
            log.error("Timeout: assertion not passed within {} ms", timeoutMs);
            throw e;
        }
    }

    public static <T> void waitFor(Supplier<T> actualSupplier, T expected, int timeoutMs, int pollIntervalMs) {
        log.info("waitFor(value == expected) started: expecting {}", expected);

        try {
            Awaitility.await()
                    .atMost(Duration.ofMillis(timeoutMs))
                    .pollInterval(Duration.ofMillis(pollIntervalMs))
                    .until(() -> {
                        T actual = actualSupplier.get();
                        log.debug("🔁 Got value: {}", actual);
                        return Objects.equals(actual, expected);
                    });

            log.info("Value matched expected: {}", expected);
        } catch (ConditionTimeoutException e) {
            log.error("Timeout: expected = {}, but not reached in {} ms", expected, timeoutMs);
            throw e;
        }
    }

    public static <T> void waitFor(Supplier<T> actualSupplier, Predicate<T> condition, int timeoutMs, int pollIntervalMs) {
        log.info("⏳ waitFor(condition) started");

        try {
            Awaitility.await()
                    .atMost(Duration.ofMillis(timeoutMs))
                    .pollInterval(Duration.ofMillis(pollIntervalMs))
                    .until(() -> {
                        T actual = actualSupplier.get();
                        boolean passed = condition.test(actual);
                        log.debug("Condition check: value = {}, passed = {}", actual, passed);
                        return passed;
                    });

            log.info("Condition satisfied");
        } catch (ConditionTimeoutException e) {
            log.error("Timeout: condition not met within {} ms", timeoutMs);
            throw e;
        }
    }

    public static <T> void waitForNotNull(Supplier<T> supplier, int timeoutMs, int pollIntervalMs) {
        log.info("⏳ waitForNotNull started");

        try {
            Awaitility.await()
                    .atMost(Duration.ofMillis(timeoutMs))
                    .pollInterval(Duration.ofMillis(pollIntervalMs))
                    .until(() -> {
                        T result = supplier.get();
                        log.debug("Got: {}", result);
                        return result != null;
                    });

            log.info("Value is not null");
        } catch (ConditionTimeoutException e) {
            log.error("Timeout: value stayed null within {} ms", timeoutMs);
            throw e;
        }
    }
}
