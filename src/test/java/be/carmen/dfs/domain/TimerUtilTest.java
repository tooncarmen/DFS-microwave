package be.carmen.dfs.domain;

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class TimerUtilTest {

    private static final int DEFAULT_SECONDS = 1;

    @BeforeEach
    void setUp() {
        Awaitility.setDefaultPollInterval(100, TimeUnit.MILLISECONDS);
        Awaitility.setDefaultPollDelay(Duration.ONE_SECOND);
        Awaitility.setDefaultTimeout(new Duration(30, TimeUnit.SECONDS));
    }

    @Test
    void whenATimerIsStarted_statusIsActive() {
        TimerUtil timerUtil = new TimerUtil();
        assertFalse(timerUtil.isActive(), "Timer is not active when not started");
        timerUtil.preformDelayedTask(LocalDateTime.now().plusSeconds(DEFAULT_SECONDS), () -> IO.println("done"));
        assertTrue(timerUtil.isActive());
        timerUtil.stop();
    }

    @Test
    void whenATimerIsStarted_statusIsNotActive() {
        TimerUtil timerUtil = new TimerUtil();
        assertFalse(timerUtil.isActive(), "Timer is not active when not started");
        timerUtil.stop();
    }

    @Test
    void whenATimerActive_itCanGetCancelled() {
        //given
        TimerUtil timerUtil = new TimerUtil();
        timerUtil.preformDelayedTask(LocalDateTime.now().plusSeconds(DEFAULT_SECONDS), () -> log.info("done"));
        //when
        assertTrue(timerUtil.isActive(), "Timer is started");
        timerUtil.stop();
        //then
        assertFalse(timerUtil.isActive(), "Timer is no longer active");
    }

    @Test
    void whenATimerIsDone_TheFunctionIsExecuted() {
        TimerUtil timerUtil = new TimerUtil();

        AtomicBoolean finished = new AtomicBoolean(false);

        //given
        timerUtil.preformDelayedTask(LocalDateTime.now().plusSeconds(DEFAULT_SECONDS),
                () -> {
                    log.info("done");
                    finished.set(true);
                });
        //when

        await().atLeast(1, TimeUnit.SECONDS).until(() -> !timerUtil.isActive());
        //then
        assertTrue(finished.get());
        assertFalse(timerUtil.isActive(), "Timer is no longer active");
    }

    @Test
    void whenTheTimerIsActive_timeCanGetExtended() {
        //given
        TimerUtil timerUtil = new TimerUtil();

        timerUtil.preformDelayedTask(LocalDateTime.now().plusSeconds(DEFAULT_SECONDS),
                () -> log.info("done"));
        //when
        timerUtil.increaseTimer(2, ChronoUnit.SECONDS);
        //then
        assertTrue(timerUtil.isActive(), "Timer is active");
        //still active after initial 2s
        await().timeout(2, TimeUnit.SECONDS).until(timerUtil::isActive);
        //stops after 4 secondsc
        await().atMost(5, TimeUnit.SECONDS).until(() -> !timerUtil.isActive());
    }

    @Test
    void stoppingWhenNoTimerIsSetDoesNothing() {
        TimerUtil timerUtil = new TimerUtil();
        timerUtil.stop();
        assertFalse(timerUtil.isActive(), "Timer is not active");
    }
}