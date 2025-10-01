package com.event.management.userservice.mapper;

import com.event.management.userservice.dto.UserResponse;
import com.event.management.userservice.dto.UserUpdateRequest;
import com.event.management.userservice.entity.Role;
import com.event.management.userservice.entity.RoleType;
import com.event.management.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponseList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles",
            source = "roles",
            qualifiedByName = "mapStringsToRoles")
    void updateUserFromDto(UserUpdateRequest request, @MappingTarget User user);

    @Named("mapRolesToStrings")
    default List<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
    }

    @Named("mapStringsToRoles")
    default Set<Role> mapStringsToRoles(Set<String> roleNames) {
        if (roleNames == null) {
            return null;
        }
        return roleNames.stream()
                .map(name -> Role.builder().name(RoleType.valueOf(name)).build())
                .collect(Collectors.toSet());
    }
}