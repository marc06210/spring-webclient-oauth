package com.mgu.resourceserver.web;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("speed")
public class SpeedController {

    private static int currSpeed = 10;
    @GetMapping("/bike-speed/{bikeName}")
    public BikeSpeed getBikeSpeed(@PathVariable String bikeName) {
        return new BikeSpeed(bikeName, getSpeed());
    }

    static synchronized int getSpeed() {
        int result = currSpeed;
        currSpeed += 5;
        if(currSpeed==40) {
            currSpeed = 10;
        }
        return result;
    }
}

record BikeSpeed(String name, int maxSpeed) { }
