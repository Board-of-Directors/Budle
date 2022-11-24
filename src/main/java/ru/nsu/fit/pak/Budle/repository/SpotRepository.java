package ru.nsu.fit.pak.Budle.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.fit.pak.Budle.dao.Establishment;
import ru.nsu.fit.pak.Budle.dao.Spot;

import java.util.List;

@Repository
public interface SpotRepository extends CrudRepository<Spot, Long> {
    List<Spot> findByEstablishment(Establishment establishment);
}