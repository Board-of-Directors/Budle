package ru.nsu.fit.pak.Budle.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nsu.fit.pak.Budle.dao.Establishment;
import ru.nsu.fit.pak.Budle.dao.Worker;
import ru.nsu.fit.pak.Budle.dto.UserDto;
import ru.nsu.fit.pak.Budle.dto.WorkerDto;

@Component
public class WorkerMapper {
    @Autowired
    EstablishmentMapper establishmentMapper;
    @Autowired
    UserMapper userMapper;

    public WorkerDto modelToDto(Worker worker) {
        UserMapper userMapper = new UserMapper();
        WorkerDto dto = new WorkerDto();
        dto.setId(worker.getId());
        dto.setOnWork(worker.getOnWork());
        dto.setWorkerType(worker.getWorkerType());
        dto.setEstablishments(establishmentMapper.modelListToDtoList(worker.getEstablishments()));
        dto.setUser(userMapper.modelToDto(worker.getUser()));
        return dto;
    }
}
