package hft.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeMessage {

    @JsonProperty("e")
    private String eventType;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("p")
    private String price;

    public String getEventType() {
        return eventType;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getPrice() {
        return price;
    }

}