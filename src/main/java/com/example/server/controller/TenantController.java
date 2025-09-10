package com.example.server.controller;

import com.example.common.model.Tenant;
import com.example.server.service.TenantService;

import java.sql.SQLException;
import java.util.List;

public class TenantController {
    private final TenantService service = new TenantService();

    public List<Tenant> listTenants() throws SQLException { return service.getAll(); }
    public Tenant createTenant(Tenant t) throws SQLException { return service.create(t); }
    public boolean updateTenant(Tenant t) throws SQLException { return service.update(t); }
    public boolean deleteTenant(int id) throws SQLException { return service.delete(id); }
}
