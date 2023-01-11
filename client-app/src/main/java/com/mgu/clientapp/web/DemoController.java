package com.mgu.clientapp.web;

import com.mgu.clientapp.error.MyCustomServerException;
import data.Bike;
import data.BikeSpeed;
import data.Car;
import data.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
public class DemoController {
    private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);

    private WebClient webClientv1;
    private WebClient webClientv2;
    private WebClient webClientv3;

    public DemoController(@Qualifier("webClientV1") WebClient webClientv1,
                          @Qualifier("webClientV2") WebClient webClientv2,
                          @Qualifier("webClientV3") WebClient webClientv3) {
        this.webClientv1 = webClientv1;
        this.webClientv2 = webClientv2;
        this.webClientv3 = webClientv3;
    }

    @GetMapping("/demo1")
    public Flux<Vehicle> getDemo(@RequestParam(name = "error", defaultValue = "false", required = false) boolean shouldRaiseError) {
        LOG.info("{} - {}", webClientv1, webClientv2);

        Flux<Vehicle> pub1 = this.webClientv1.get().uri("http://car-server:8090/cars?error="+shouldRaiseError)
                .retrieve()
                .bodyToFlux(Car.class)
                .map(DemoController::getVehicle)
                ;

        Flux<Vehicle> pub2 = this.webClientv2.get().uri("http://bike-server:8091/bikes")
                .retrieve()
                .bodyToFlux(Bike.class)
                .map(DemoController::getVehicle);

        return Flux.concat(pub1, pub2);
    }

    @GetMapping("/demo2")
    public Flux<Vehicle> getDemoV2(@RequestParam(name = "error", defaultValue = "false", required = false) boolean shouldRaiseError) {
        // one call to get all the cars
        Flux<Vehicle> pub1 = this.webClientv1.get().uri("http://car-server:8090/cars?error="+shouldRaiseError)
                .retrieve()
                .bodyToFlux(Car.class)
                .onErrorResume(t -> Flux.empty()) // in case of error, we return empty list
                .map(DemoController::getVehicle);

        // one call to get all the bikes
        // for each bike we invoke another endpoint to get its max speed
        Flux<Vehicle> pub2 = this.webClientv2.get().uri("http://bike-server:8091/bikes")
                .retrieve()
                .bodyToFlux(Bike.class)
                .map(DemoController::getVehicle)
                .flatMap(v -> this.webClientv3.get().uri("http://speed-server:8092/bike-speed/" + v.brand())
                            .retrieve()
                            .bodyToMono(BikeSpeed.class)
                            .map(speed -> new Vehicle(v.type(), v.brand(), v.hourlyRate(), v.color(), speed.maxSpeed())));

        // we concat all the results
        return Flux.concat(pub1, pub2);
    }

    // involved when invoking /demo1
    @ExceptionHandler(MyCustomServerException.class)
    public ResponseEntity<String> handleWebClientResponseException(MyCustomServerException ex) {
        LOG.error("Error from WebClient: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
    }

    static Vehicle getVehicle(Car car) {
        double hourlyRate = car.priceRate()/24;
        return new Vehicle("car", car.brand(), hourlyRate, car.color(), car.maxSpeed());
    }

    static Vehicle getVehicle(Bike bike) {
        return new Vehicle("bike", bike.brand(), bike.rate(), bike.color(), 0);
    }
}
