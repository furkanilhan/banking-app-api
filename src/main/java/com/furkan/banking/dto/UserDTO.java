package com.furkan.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private Set<RoleDTO> roles;
    private List<AccountDTO> accounts;
}
