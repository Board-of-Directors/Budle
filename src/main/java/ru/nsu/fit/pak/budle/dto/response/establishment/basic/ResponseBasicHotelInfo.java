package ru.nsu.fit.pak.budle.dto.response.establishment.basic;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseBasicHotelInfo extends ResponseBasicEstablishmentInfo {
    private int starsCount;
}