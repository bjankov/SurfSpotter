package hr.algebra.surfspot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "PlanPutovanja")
public class Itinerary {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "surfSpot")
    private List<SurfSpot> surfSpots;

    public Itinerary() {}

    public Itinerary(List<SurfSpot> surfSpots) {
        this.surfSpots = surfSpots;
    }
}
