package data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Car(
        @JsonProperty("color") String color,
        @JsonProperty("brand") String brand,
        @JsonProperty("max-speed") int maxSpeed,
        @JsonProperty("price-rate") double priceRate) {
}
