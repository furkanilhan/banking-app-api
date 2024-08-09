package com.furkan.banking.repository;

import com.furkan.banking.enums.RoleName;
import com.furkan.banking.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
