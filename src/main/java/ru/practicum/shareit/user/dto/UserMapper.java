package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static User toUser(UserDto userDto, int userId) {
        return new User(
                userId,
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static List<UserDto> toUserDtoList(Map<Integer, User> userMap) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userMap.values()) {
            userDtoList.add(toUserDto(user));
        }
        return userDtoList;
    }
}
