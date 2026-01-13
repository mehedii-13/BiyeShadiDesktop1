package dao;

import models.Biodata;
import java.sql.*;
import java.time.LocalDate;

public class BiodataDAO {
    private Connection connection;

    public BiodataDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Biodata biodata) throws SQLException {
        String query = "INSERT INTO biodata (user_id, full_name, date_of_birth, gender, address, phone, email, occupation, nationality) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, biodata.getUserId());
            stmt.setString(2, biodata.getFullName());
            stmt.setDate(3, Date.valueOf(biodata.getDateOfBirth()));
            stmt.setString(4, biodata.getGender());
            stmt.setString(5, biodata.getAddress());
            stmt.setString(6, biodata.getPhone());
            stmt.setString(7, biodata.getEmail());
            stmt.setString(8, biodata.getOccupation());
            stmt.setString(9, biodata.getNationality());
            stmt.executeUpdate();
        }
    }

    public Biodata getByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM biodata WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractBiodataFromResultSet(rs);
            }
        }
        return null;
    }

    public void update(Biodata biodata) throws SQLException {
        String query = "UPDATE biodata SET full_name = ?, date_of_birth = ?, gender = ?, address = ?, phone = ?, email = ?, occupation = ?, nationality = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, biodata.getFullName());
            stmt.setDate(2, Date.valueOf(biodata.getDateOfBirth()));
            stmt.setString(3, biodata.getGender());
            stmt.setString(4, biodata.getAddress());
            stmt.setString(5, biodata.getPhone());
            stmt.setString(6, biodata.getEmail());
            stmt.setString(7, biodata.getOccupation());
            stmt.setString(8, biodata.getNationality());
            stmt.setInt(9, biodata.getId());
            stmt.executeUpdate();
        }
    }

    private Biodata extractBiodataFromResultSet(ResultSet rs) throws SQLException {
        Biodata biodata = new Biodata();
        biodata.setId(rs.getInt("id"));
        biodata.setUserId(rs.getInt("user_id"));
        biodata.setFullName(rs.getString("full_name"));
        biodata.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        biodata.setGender(rs.getString("gender"));
        biodata.setAddress(rs.getString("address"));
        biodata.setPhone(rs.getString("phone"));
        biodata.setEmail(rs.getString("email"));
        biodata.setOccupation(rs.getString("occupation"));
        biodata.setNationality(rs.getString("nationality"));
        return biodata;
    }
}

