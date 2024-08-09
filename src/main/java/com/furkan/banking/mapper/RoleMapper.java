package com.furkan.banking.mapper;

import com.furkan.banking.dto.RoleDTO;
import com.furkan.banking.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toRoleDTO(Role role);
    Role toRole(RoleDTO roleDTO);
}
