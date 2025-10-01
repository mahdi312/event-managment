package com.event.management.userservice.repository;

import com.event.management.userservice.entity.RoleType;
import com.event.management.userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);

}
