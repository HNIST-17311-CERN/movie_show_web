package org.example.Entity;

import java.util.List;

public class RoleDetails extends Role {

    private List<Permission> permissions;
    private List<User> users;

    public List<Permission> getPermissions() { return permissions; }
    public void setPermissions(List<Permission> permissions) { this.permissions = permissions; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    @Override
    public String toString() {
        return "RoleDetails{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", permissions=" + permissions +
                ", users=" + users +
                '}';
    }
}
