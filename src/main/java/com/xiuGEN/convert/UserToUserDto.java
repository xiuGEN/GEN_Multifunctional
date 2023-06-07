package com.xiuGEN.convert;

import com.xiuGEN.pojo.Capacity;
import com.xiuGEN.pojo.User;
import com.xiuGEN.pojo.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserToUserDto {
    UserToUserDto INSTANCE = Mappers.getMapper(UserToUserDto.class);

    /*
    * User转化为UserDto
    * */
    UserDto UserToUserDto(User user);
    /*
     * UserDto转化为User
     * */
    User UserDtoTOUser(UserDto userDto);
}
