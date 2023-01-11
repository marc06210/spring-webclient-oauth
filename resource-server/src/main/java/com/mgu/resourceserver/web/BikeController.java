package com.mgu.resourceserver.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@Profile("bike")
public class BikeController {

    private static final String JSON_BIKE= """
        [
          {"color":"blue","brand":"rockrider", "rate": "10"},
          {"color":"black","brand":"cloot", "rate": "8"}
        ]            
        """;

    @GetMapping("/bikes")
    public Object getProposals() {
        return readFromFileUsingJackson();
    }

    Object readFromFileUsingJackson() {
        Object retValue = null;
        try {
            retValue = new ObjectMapper()
                    .readValue(JSON_BIKE, new TypeReference<List<Bike>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retValue;
    }
}

record Bike(
        @JsonProperty("color") String color,
        @JsonProperty("brand") String brand,
        @JsonProperty("rate") double rate) { }
