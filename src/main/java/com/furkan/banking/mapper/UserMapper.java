package com.furkan.banking.mapper;

import com.furkan.banking.dto.UserDTO;
import com.furkan.banking.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class, AccountMapper.class})
public interface UserMapper {

    @Mapping(source = "roles", target = "roles")
    @Mapping(source = "accounts", target = "accounts")
    User toUser(UserDTO userDTO);

    @Mapping(source = "roles", target = "roles")
    @Mapping(source = "accounts", target = "accounts")
    UserDTO toUserDTO(User user);
}
