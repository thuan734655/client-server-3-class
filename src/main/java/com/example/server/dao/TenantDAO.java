package com.example.server.dao;

import com.example.common.model.Tenant;
import com.example.server.db.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TenantDAO {

    public List<Tenant> findAll() throws SQLException {
        String sql = "SELECT user_id, contact_number, gender, roomNumber, name FROM thong_tin_thue ORDER BY user_id";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Tenant> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        }
    }

    public Tenant insert(Tenant t) throws SQLException {
        String sql = "INSERT INTO thong_tin_thue(contact_number, gender, roomNumber, name) VALUES (?,?,?,?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getContact_number());
            ps.setString(2, t.getGender());
            if (t.getRoomNumber() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, t.getRoomNumber());
            ps.setString(4, t.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setUser_id(keys.getInt(1));
                }
            }
            return t;
        }
    }

    public boolean update(Tenant t) throws SQLException {
        String sql = "UPDATE thong_tin_thue SET contact_number=?, gender=?, roomNumber=?, name=? WHERE user_id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getContact_number());
            ps.setString(2, t.getGender());
            if (t.getRoomNumber() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, t.getRoomNumber());
            ps.setString(4, t.getName());
            ps.setInt(5, t.getUser_id());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int userId) throws SQLException {
        String sql = "DELETE FROM thong_tin_thue WHERE user_id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private Tenant map(ResultSet rs) throws SQLException {
        return new Tenant(
                rs.getInt("user_id"),
                rs.getString("contact_number"),
                rs.getString("gender"),
                (Integer) rs.getObject("roomNumber"),
                rs.getString("name")
        );
    }
}
