package ru.nsu.fit.pak.budle.dto.response.establishment.extended;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseExtendedRestaurantInfo extends ResponseExtendedEstablishmentInfo {
    private String cuisineCountry;
}