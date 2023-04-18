package ru.nsu.fit.pak.budle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.fit.pak.budle.dao.Code;
import ru.nsu.fit.pak.budle.dao.User;
import ru.nsu.fit.pak.budle.dto.UserDto;
import ru.nsu.fit.pak.budle.exceptions.IncorrectDataException;
import ru.nsu.fit.pak.budle.exceptions.IncorrectPhoneNumberFormatException;
import ru.nsu.fit.pak.budle.exceptions.UserAlreadyExistsException;
import ru.nsu.fit.pak.budle.repository.CodeRepository;
import ru.nsu.fit.pak.budle.repository.UserRepository;
import ru.nsu.fit.pak.budle.service.CodeService;
import ru.nsu.fit.pak.budle.service.UserService;

import javax.transaction.Transactional;


@SpringBootTest(classes = BudleApplication.class)
@Testcontainers
class UserBusinessLogicTests {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeService codeService;

    @Autowired
    private CodeRepository codeRepository;

    @Test
    @Transactional
    public void testUserService_tryToRegisterUserWithExistingUsername() {
        insertUsers();
        UserDto dto = new UserDto(4L, "3111", "Oleg", "+79321312213");
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(dto));

    }

    @Test
    @Transactional
    public void testUserService_tryToRegisterUserWithExistingNumber() {
        insertUsers();
        UserDto dto = new UserDto(4L, "3111", "Kerkey", "+7932131231");
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(dto));
    }

    @Test
    @Transactional
    public void testUserService_tryToRegisterUserWithDoesntExistingNumberAndUsername() {
        insertUsers();
        int oldSize = userRepository.findAll().size();
        UserDto dto = new UserDto(4L, "3111", "Kerkey", "+7932131239");
        userService.registerUser(dto);
        Assertions.assertEquals(userRepository.findAll().size(), oldSize + 1);
    }

    @Test
    @Transactional
    public void testCreatingCodeForUser_AfterThatCheckingThisCode_MustBeTrue() {
        codeService.generateCode("+79833219999");
        Code code = codeRepository.findAll().get(0);
        Assertions.assertTrue(codeService.checkCode(code.getPhoneNumber(), code.getCode()));
    }


    @Test
    @Transactional
    public void checkingNonExistedCode_MustThrownException() {
        Assertions.assertThrows(IncorrectDataException.class,
                () -> codeService.checkCode("+79833211199", "1234"));
    }

    @Test
    @Transactional
    public void tryToRequestWithIncorrectNumberFormat_MustBeAnException() {
        Assertions.assertThrows(IncorrectPhoneNumberFormatException.class,
                () -> codeService.generateCode("+7711"));
    }


    @Test
    @Transactional
    public void creatingCodeForExistedUser_MustBeException() {
        insertUsers();
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> codeService.generateCode(userRepository.findAll().get(0).getPhoneNumber()));
    }


    private void insertUsers() {
        userRepository.save(new User(1L, "Oleg", "+7932131231", "3213"));
        userRepository.save(new User(2L, "Marvin", "+7932131233", "3213"));
        userRepository.save(new User(3L, "Sergey", "+7932131232", "3213"));
        userRepository.flush();
    }


}