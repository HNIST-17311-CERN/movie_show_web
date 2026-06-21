package org.example.Mapper;

import org.example.Entity.RoleDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {

    RoleDetails findRoleFullByName(@Param("roleName") String roleName);
}
