package ru.nsu.fit.pak.Budle.service;

import ru.nsu.fit.pak.Budle.dto.EstablishmentDto;

import java.util.List;

public interface EstablishmentServiceInterface {
    List<EstablishmentDto> getEstablishments();
    EstablishmentDto getEstablishmentById(Long id);
}
