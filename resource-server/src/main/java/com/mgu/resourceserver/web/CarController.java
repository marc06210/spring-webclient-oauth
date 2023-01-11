package com.mgu.resourceserver.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.List;

@RestController
@Profile("car")
public class CarController {

    private final static String JSON_CAR = """
        [
          {"color":"yellow","brand":"renault", "price-rate": "100", "max-speed":  130},
          {"color":"green","brand":"peugeot", "price-rate": "100", "max-speed":  140}
        ]
        """;

    @GetMapping("/cars")
    public List<Car> getlistOfCars(@RequestParam(name = "error", defaultValue = "false", required = false) boolean shouldRaiseError ) {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) { }
        if(shouldRaiseError) {
            throw new RuntimeException("Exception raised on demand");
        }
        return readFromFileUsingJackson();
    }

    List<Car> readFromFileUsingJackson() {
        List<Car> retValue = null;
        try {
            retValue = new ObjectMapper()
                    .readValue(JSON_CAR, new TypeReference<List<Car>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retValue;
    }
}

record Car(
        @JsonProperty("color") String color,
        @JsonProperty("brand") String brand,
        @JsonProperty("max-speed") int maxSpeed,
        @JsonProperty("price-rate") double priceRate) { }
