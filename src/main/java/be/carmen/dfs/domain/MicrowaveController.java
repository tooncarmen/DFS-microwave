package be.carmen.dfs.domain;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Slf4j
public class MicrowaveController implements Microwave.EventListener {

    private final Microwave microwave;
    private final int defaultSecondsStart;
    private final TimerUtil timerUtil;

    public MicrowaveController(Microwave microwave, TimerUtil timerUtil, int defaultSecondsStart) {
        this.microwave = microwave;
        this.defaultSecondsStart = defaultSecondsStart;
        this.timerUtil = timerUtil;
    }

    @Override
    public void onDoorStatusChanged(Boolean open) {
        if (Objects.isNull(open)) {
            throw new IllegalStateException("Door is in an unknown state");
        }
        if (open) {
            microwave.turnOnLight();
            microwave.turnOffHeater();
            timerUtil.stop();
        } else {
            microwave.turnOffLight();
        }
    }

    @Override
    public void onStartButtonPressed() {
        if (!microwave.isDoorOpen()) {
            if (timerUtil.isActive()) {
                timerUtil.increaseTimer(defaultSecondsStart, ChronoUnit.SECONDS);
            } else {
                microwave.turnOnHeater();
                timerUtil.preformDelayedTask(LocalDateTime.now().plusSeconds(defaultSecondsStart), microwave::turnOffHeater);
            }
        } else {
            log.info("Door is open, close it before you can start");
        }
    }
}
