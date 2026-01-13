package com.matrimony.dao;

import com.matrimony.database.DatabaseConnection;
import com.matrimony.model.MatchProfile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    /**
     * Search for matches based on filter criteria
     * Only shows users of opposite gender (for matrimony matching)
     */
    public List<MatchProfile> searchMatches(int currentUserId, String currentUserGender, String name, Integer minAge, Integer maxAge,
                                           String minHeight, String maxHeight, String maritalStatus,
                                           String religion, String education, String income,
                                           String city, String state, String country) {

        List<MatchProfile> matches = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.id, u.full_name, u.gender, b.age, b.height, b.marital_status, ");
        sql.append("b.religion, b.education, b.occupation, b.city, b.state, b.country, ");
        sql.append("b.annual_income, b.photo_path ");
        sql.append("FROM users u ");
        sql.append("LEFT JOIN biodata b ON u.id = b.user_id ");
        sql.append("WHERE u.id != ? AND u.role = 'user' ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(currentUserId);

        // Filter by opposite gender only (prevent same-gender matching)
        if (currentUserGender != null && !currentUserGender.isEmpty()) {
            sql.append("AND u.gender != ? ");
            parameters.add(currentUserGender);
            System.out.println("Filtering to exclude same gender: " + currentUserGender);
        }

        // Add name filter if provided
        if (name != null && !name.trim().isEmpty()) {
            sql.append("AND LOWER(u.full_name) LIKE LOWER(?) ");
            parameters.add("%" + name.trim() + "%");
        }

        if (minAge != null) {
            sql.append("AND b.age >= ? ");
            parameters.add(minAge);
        }

        if (maxAge != null) {
            sql.append("AND b.age <= ? ");
            parameters.add(maxAge);
        }

        if (minHeight != null && !minHeight.isEmpty()) {
            sql.append("AND b.height >= ? ");
            parameters.add(minHeight);
        }

        if (maxHeight != null && !maxHeight.isEmpty()) {
            sql.append("AND b.height <= ? ");
            parameters.add(maxHeight);
        }

        if (maritalStatus != null && !maritalStatus.isEmpty() && !maritalStatus.equals("Any")) {
            sql.append("AND b.marital_status = ? ");
            parameters.add(maritalStatus);
        }

        if (religion != null && !religion.isEmpty() && !religion.equals("Any")) {
            sql.append("AND b.religion = ? ");
            parameters.add(religion);
        }

        if (education != null && !education.isEmpty() && !education.equals("Any")) {
            sql.append("AND b.education = ? ");
            parameters.add(education);
        }

        if (income != null && !income.isEmpty() && !income.equals("Any")) {
            sql.append("AND b.annual_income = ? ");
            parameters.add(income);
        }

        if (city != null && !city.trim().isEmpty()) {
            sql.append("AND LOWER(b.city) LIKE LOWER(?) ");
            parameters.add("%" + city.trim() + "%");
        }

        if (state != null && !state.trim().isEmpty()) {
            sql.append("AND LOWER(b.state) LIKE LOWER(?) ");
            parameters.add("%" + state.trim() + "%");
        }

        if (country != null && !country.trim().isEmpty()) {
            sql.append("AND LOWER(b.country) LIKE LOWER(?) ");
            parameters.add("%" + country.trim() + "%");
        }

        sql.append("ORDER BY b.created_at DESC LIMIT 100");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            System.out.println("Executing search query with " + parameters.size() + "filters");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MatchProfile profile = new MatchProfile();
                    profile.setUserId(rs.getInt("id"));
                    profile.setFullName(rs.getString("full_name"));
                    profile.setGender(rs.getString("gender"));
                    profile.setAge(rs.getInt("age"));
                    profile.setHeight(rs.getString("height"));
                    profile.setMaritalStatus(rs.getString("marital_status"));
                    profile.setReligion(rs.getString("religion"));
                    profile.setEducation(rs.getString("education"));
                    profile.setOccupation(rs.getString("occupation"));
                    profile.setCity(rs.getString("city"));
                    profile.setState(rs.getString("state"));
                    profile.setCountry(rs.getString("country"));
                    profile.setAnnualIncome(rs.getString("annual_income"));
                    profile.setPhotoPath(rs.getString("photo_path"));

                    matches.add(profile);
                }
            }

            System.out.println("Found " + matches.size() + "matches");

        } catch (SQLException e) {
            System.err.println("Error searching matches: " + e.getMessage());
            e.printStackTrace();
        }

        return matches;
    }

    /**
     * Get all matches without filters (for initial display)
     * Only shows users of opposite gender
     */
    public List<MatchProfile> getAllMatches(int currentUserId, String currentUserGender) {
        return searchMatches(currentUserId, currentUserGender, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}

