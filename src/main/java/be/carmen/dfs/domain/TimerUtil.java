package be.carmen.dfs.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Timer;
import java.util.TimerTask;


@Slf4j
public class TimerUtil {
    public static final long DELAY = 0L;
    public static final long PERIOD = 1000L;
    private LocalDateTime endTime;
    private Runnable doWhenDone;
    private Timer timer;
    @Getter
    private boolean isActive;

    public void preformDelayedTask(LocalDateTime endTime, Runnable doWhenDone) {
        this.endTime = endTime;
        this.doWhenDone = doWhenDone;

        TimerTask task = new TimerTask() {
            public void run() {
                if (LocalDateTime.now().isBefore(endTime)) {
                    log.info("Running... " + Duration.between(LocalDateTime.now(), endTime).toSeconds() + " seconds left");
                } else {
                    log.info("TIMER HAS ENDED");
                    cancel();
                    isActive = false;
                    doWhenDone.run();
                }
            }
        };
        timer = new Timer("CountDownTimer");
        timer.schedule(task, DELAY, PERIOD);
        isActive = true;
    }

    public void increaseTimer(long amount, TemporalUnit unit) {
        this.stop();
        this.preformDelayedTask(endTime.plus(amount, unit), doWhenDone);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            isActive = false;
        }
    }
}
