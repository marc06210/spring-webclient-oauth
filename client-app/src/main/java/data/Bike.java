package data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Bike(
        @JsonProperty("color") String color,
        @JsonProperty("brand") String brand,
        @JsonProperty("rate") double rate) {
}
