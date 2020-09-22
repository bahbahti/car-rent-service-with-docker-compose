package com.netcracker.constants;

public class Queries {
    public static final String USERS_QUERY = "SELECT username, password, enabled  FROM customers where username= ?";
    public static final String ROLES_QUERY = "SELECT c.username, r.role FROM customers c INNER JOIN roles r ON(c.role_id = r.id) where c.username = ?";
}
