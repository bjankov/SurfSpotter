package hr.algebra.surfspot.model;

public class WaveDetails {
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

    @Override
    public String toString() {
        return waveHeight.toString() + " m | " + waveType.getDisplayValue();
    }
}
