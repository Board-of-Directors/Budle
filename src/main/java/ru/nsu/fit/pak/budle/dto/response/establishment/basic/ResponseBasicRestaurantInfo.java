package ru.nsu.fit.pak.budle.dto.response.establishment.basic;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseBasicRestaurantInfo extends ResponseBasicEstablishmentInfo {
    private String cuisineCountry;

}