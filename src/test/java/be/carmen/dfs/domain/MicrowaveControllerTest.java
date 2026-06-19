package be.carmen.dfs.domain;

import be.carmen.dfs.stub.FakeMicrowave;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicrowaveControllerTest {
    public static final int DEFAULT_SECONDS_START = 1;
    private MicrowaveController controller;
    private FakeMicrowave microwave;

    @BeforeEach
    void setUp() {
        microwave = new FakeMicrowave();
        controller = new MicrowaveController(microwave, new TimerUtil(), DEFAULT_SECONDS_START);
        microwave.setEventListener(controller);
    }

    @Test
    void whenAMicrowaveIsInNeutralState() {
        assertFalse(microwave.isHeaterOn());
        assertFalse(microwave.isLightOn());
        assertFalse(microwave.isDoorOpen());
    }

    @Test
    void whenTheDoorIsOpen_theLightShouldBeOn() {
        microwave.simulateDoor(true);
        assertTrue(microwave.isLightOn());
    }

    @Test
    void whenTheDoorIsClosed_theLightShouldBeOff() {
        microwave.simulateDoor(false);
        assertFalse(microwave.isLightOn());
    }

    @Test
    void whenTheDoorIsOpen_theHeaterShouldBeOff() {
        microwave.simulateDoor(true);
        assertFalse(microwave.isHeaterOn());
    }

    @Test
    void whenTheDoorIsOpen_startButtonDoesNotAffectAnything() {
        microwave.simulateDoor(true);
        microwave.simulateStart();
        assertFalse(microwave.isHeaterOn());
        assertTrue(microwave.isLightOn());
        assertTrue(microwave.isDoorOpen());
    }

    @Test
    void whenTheDoorIsClosed_startButtonActivatesTimerAndHeater() {
        //when
        microwave.simulateDoor(false);
        microwave.simulateStart();

        //then
        assertTrue(microwave.isHeaterOn());
    }

    @Test
    void whenTheDoorIsOpenedWhileTheHeaterIsOn_theHeaterStops() {
        //given
        microwave.turnOnHeater();
        //when
        microwave.simulateDoor(true);
        //then
        assertFalse(microwave.isHeaterOn());
    }

    @Test
    void whenTheMicrowaveIsStarted_itStopsAfterAGivenTime()  {
        //when
        microwave.simulateStart();
        //then
        assertTrue(microwave.isHeaterOn());
        await().atMost(DEFAULT_SECONDS_START + 1, TimeUnit.SECONDS).until(() -> !microwave.isHeaterOn());
    }

    @Test
    void whenTheMicrowaveIsStarted_AndTheStartIsPressedAgain_StartDurationIsAdded() {
        //when
        microwave.simulateStart();

        //then
        assertTrue(microwave.isHeaterOn());

        //press again
        microwave.simulateStart();

        //active after initial time
        await().timeout((DEFAULT_SECONDS_START * 1000), TimeUnit.MILLISECONDS).until(() -> microwave.isHeaterOn());
        //stops after twice initial time
        await().atMost((DEFAULT_SECONDS_START * 2) + 1, TimeUnit.SECONDS).until(() -> !microwave.isHeaterOn());
    }

    @Test
    void whenTheDoorIsInUnknownState_anErrorIsThrown() {
        Assertions.assertThrows(IllegalStateException.class, () -> controller.onDoorStatusChanged(null));
    }
}
