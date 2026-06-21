package org.example.Servlet;

import org.example.Entity.RoleDetails;
import org.example.Mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    RoleMapper roleMapper;

    @GetMapping("/detail")
    @PreAuthorize("hasAuthority('movie:view')")
    public RoleDetails roleDetail(@RequestParam("name") String name) {
        return roleMapper.findRoleFullByName(name);
    }
}
