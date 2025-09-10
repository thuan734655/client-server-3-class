package com.example.server.service;

import com.example.common.model.Tenant;
import com.example.server.dao.TenantDAO;

import java.sql.SQLException;
import java.util.List;

public class TenantService {
    private final TenantDAO dao = new TenantDAO();

    public List<Tenant> getAll() throws SQLException { return dao.findAll(); }
    public Tenant create(Tenant t) throws SQLException { return dao.insert(t); }
    public boolean update(Tenant t) throws SQLException { return dao.update(t); }
    public boolean delete(int id) throws SQLException { return dao.delete(id); }
}
