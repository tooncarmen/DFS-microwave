package be.carmen.dfs.domain;

// this Interface was added in order to have controle over the lights
public interface MicrowaveLight {
    /**
     * Turns on the microwave light.
     */
    void turnOnLight();

    /**
     * Turns off the microwave light.
     */
    void turnOffLight();

    /**
     * Indicates if the door to the microwave is open or
     * closed.
     */
    boolean isLightOn();
}
