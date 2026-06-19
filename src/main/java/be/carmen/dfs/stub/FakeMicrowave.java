package be.carmen.dfs.stub;


import be.carmen.dfs.domain.Microwave;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeMicrowave implements Microwave {
    private boolean heaterIsOn = false;
    private boolean lampIsOn = false;
    private boolean doorIsOpen = false;

    @Setter
    private EventListener eventListener;


    @Override
    public void turnOnHeater() {
        log.info("** HEATER IS TURNING ON");
        this.heaterIsOn = true;
    }

    @Override
    public void turnOffHeater() {
        log.info("** HEATER IS TURNING OFF");
        this.heaterIsOn = false;
    }

    @Override
    public Boolean isDoorOpen() {
        return doorIsOpen;
    }

    @Override
    public Boolean isHeaterOn() {
        return heaterIsOn;
    }


    @Override
    public void turnOnLight() {
        log.info("** LAMP IS TURNING ON");
        lampIsOn = true;
    }

    @Override
    public void turnOffLight() {
        log.info("** LAMP IS TURNING OFF");
        lampIsOn = false;
    }

    @Override
    public boolean isLightOn() {
        return lampIsOn;
    }

    public void simulateDoor(boolean open) {
        this.doorIsOpen = open;
        eventListener.onDoorStatusChanged(open);
    }

    public void simulateStart() {
        eventListener.onStartButtonPressed();
    }
}
