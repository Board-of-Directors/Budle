package ru.nsu.fit.pak.budle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.fit.pak.budle.dao.User;
import ru.nsu.fit.pak.budle.dto.UserDto;
import ru.nsu.fit.pak.budle.exceptions.IncorrectDataException;
import ru.nsu.fit.pak.budle.exceptions.UserAlreadyExistsException;
import ru.nsu.fit.pak.budle.mapper.UserMapper;
import ru.nsu.fit.pak.budle.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public Boolean registerUser(UserDto userDto) {

        if (!userRepository.existsByPhoneNumber(userDto.getPhoneNumber())) {
            throw new UserAlreadyExistsException("������������ � ����� ������� ��� ����������.");
        } else {
            User user = userMapper.dtoToModel(userDto);
            userRepository.save(user);
            return true;
        }
    }

    @Override
    public List<UserDto> getUsers() {
        return userMapper.modelListToDtoList(userRepository.findAll());
    }

    @Override
    public Boolean loginUser(UserDto userDto) {
        try {
            User user = userRepository.findByPhoneNumber(userDto.getPhoneNumber()).orElseThrow();

            if (user.getPassword().equals(userDto.getPassword())) {
                return true;
            } else {
                throw new IncorrectDataException("����� ��� ������ ������� �����������.");
            }
        } catch (NoSuchElementException e) {
            throw new IncorrectDataException("������������ � ������ ������� �� ����������.");
        }
    }

    @Override
    public Boolean existsPhoneNumber(String phoneNumber) {
        return !userRepository.existsByPhoneNumber(phoneNumber);
    }
}