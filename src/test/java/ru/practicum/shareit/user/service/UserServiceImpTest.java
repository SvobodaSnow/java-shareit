package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImpTest {

    private final UserService userService;
    private final EntityManager em;

    private final UserDto userDto1 = new UserDto(
            15L,
            "NameTest1",
            "NameTest1@NameTest1.ru"
    );

    @Test
    void saveUserTest() {
        UserDto newUserDto = userService.createUser(userDto1);

        assertThat(newUserDto.getName(), equalTo(userDto1.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto1.getEmail()));

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        List<User> users = query.setParameter("email", userDto1.getEmail()).getResultList();

        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).getName(), equalTo(userDto1.getName()));
        assertThat(users.get(0).getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void saveUserTestWithEmptyName() {
        UserDto userDtoWithEmptyName = new UserDto();
        userDtoWithEmptyName.setId(20L);
        userDtoWithEmptyName.setName("");
        userDtoWithEmptyName.setEmail("NameTest@NameTest.ru");

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> userService.createUser(userDtoWithEmptyName)
        );

        assertThat(throwable.getMessage(), equalTo("Не указано имя пользователя"));
    }

    @Test
    void saveUserTestWithEmptyEmail() {
        UserDto userDtoWithEmptyEmail = new UserDto();
        userDtoWithEmptyEmail.setId(20L);
        userDtoWithEmptyEmail.setName("NameTest");
        userDtoWithEmptyEmail.setEmail("");

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> userService.createUser(userDtoWithEmptyEmail)
        );

        assertThat(throwable.getMessage(), equalTo("Не указан адрес почты пользователя"));
    }

    @Test
    void saveUserTestWithWrongEmail() {
        UserDto userDtoWithWrongEmail = new UserDto();
        userDtoWithWrongEmail.setId(20L);
        userDtoWithWrongEmail.setName("NameTest");
        userDtoWithWrongEmail.setEmail("NameTestNameTest.ru");

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> userService.createUser(userDtoWithWrongEmail)
        );

        assertThat(throwable.getMessage(), equalTo("Некоректный адрес почты"));
    }

    @Test
    void saveUserTestRepeatingEmail() {
        UserDto newUserDto = userService.createUser(userDto1);

        assertThat(newUserDto.getName(), equalTo(userDto1.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto1.getEmail()));

        Throwable throwable = assertThrows(
                Exception.class,
                () -> userService.createUser(userDto1)
        );

        assertThat(
                throwable.getMessage(),
                equalTo("could not execute statement; SQL [n/a]; constraint [null]; " +
                        "nested exception is org.hibernate.exception.ConstraintViolationException: " +
                        "could not execute statement")
        );
    }

    @Test
    void updateUserTest() {
        UserDto newUserDto = userService.createUser(userDto1);

        assertThat(newUserDto.getName(), equalTo(userDto1.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto1.getEmail()));

        UserDto updateUserDto = new UserDto();

        updateUserDto.setName("NameUpdate");

        updateUserDto = userService.updateUser(updateUserDto, newUserDto.getId());

        assertThat(updateUserDto.getName(), equalTo("NameUpdate"));
        assertThat(updateUserDto.getEmail(), equalTo(newUserDto.getEmail()));

        updateUserDto = new UserDto();

        updateUserDto.setEmail("NameUpdate@NameUpdate.ru");

        updateUserDto = userService.updateUser(updateUserDto, newUserDto.getId());

        assertThat(updateUserDto.getName(), equalTo("NameUpdate"));
        assertThat(updateUserDto.getEmail(), equalTo("NameUpdate@NameUpdate.ru"));
    }

    @Test
    void updateUserTestWithoutNameAndEmail() {
        UserDto newUserDto = userService.createUser(userDto1);

        assertThat(newUserDto.getName(), equalTo(userDto1.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto1.getEmail()));

        UserDto emptyUserDto = new UserDto();

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(emptyUserDto, newUserDto.getId())
        );

        assertThat(throwable.getMessage(), equalTo("Не указаны новые имя или email"));
    }

    @Test
    void updateUserTestWithIncorrectId() {
        UserDto newUserDto = userService.createUser(userDto1);

        assertThat(newUserDto.getName(), equalTo(userDto1.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto1.getEmail()));

        newUserDto.setName("NameUpdate");

        Throwable throwable = assertThrows(
                Exception.class,
                () -> userService.updateUser(newUserDto, -1L)
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Unable to find ru.practicum.shareit.user.model.User " +
                        "with id -1; nested exception is javax.persistence.EntityNotFoundException: Unable to find " +
                        "ru.practicum.shareit.user.model.User with id -1")
        );
    }

    @Test
    void getUserByIdTest() {
        UserDto newUserDto = userService.createUser(userDto1);

        assertThat(newUserDto.getName(), equalTo(userDto1.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto1.getEmail()));

        UserDto retrievedUserDto = userService.getUserDtoById(newUserDto.getId());

        assertThat(retrievedUserDto.getId(), equalTo(newUserDto.getId()));
        assertThat(retrievedUserDto.getName(), equalTo(newUserDto.getName()));
        assertThat(retrievedUserDto.getEmail(), equalTo(newUserDto.getEmail()));
    }
}