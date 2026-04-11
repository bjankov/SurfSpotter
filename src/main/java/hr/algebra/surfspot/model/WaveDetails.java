package hr.algebra.surfspot.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaveDetails {
    private static final Logger log = LoggerFactory.getLogger(WaveDetails.class);

    private WaveType waveType;
    private Double waveHeight;

    public WaveDetails() {
    }

    public WaveDetails(WaveType waveType, Double waveHeight) {
        this.waveType = waveType;
        this.waveHeight = waveHeight;
    }

    public WaveType getWaveType() {
        return waveType;
    }

    public void setWaveType(WaveType waveType) {
        this.waveType = waveType;
    }

    public Double getWaveHeight() {
        return waveHeight;
    }

    public void setWaveHeight(Double waveHeight) {
        this.waveHeight = waveHeight;
    }
}
