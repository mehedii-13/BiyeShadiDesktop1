package com.matrimony.dao;

import com.matrimony.database.DatabaseConnection;
import com.matrimony.model.Biodata;

import java.sql.*;
import java.time.LocalDate;

public class BiodataDAO {

    /**
     * Save or update biodata for a user
     */
    public boolean saveBiodata(Biodata biodata) {
        System.out.println("=== BiodataDAO.saveBiodata() called ===");
        System.out.println("User ID: " + biodata.getUserId());
        System.out.println("Age: " + biodata.getAge());
        System.out.println("Education: " + biodata.getEducation());
        System.out.println("Occupation: " + biodata.getOccupation());
        System.out.println("City: " + biodata.getCity());

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1 FROM biodata LIMIT 1");
            System.out.println("biodata table exists and is accessible");
        } catch (SQLException e) {
            System.err.println("ERROR: biodata table check failed!");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            System.err.println("Attempting to initialize database...");
            DatabaseConnection.initializeDatabase();
        }

        boolean exists = biodataExists(biodata.getUserId());
        System.out.println("Biodata exists for user: " + exists);

        if (exists) {
            System.out.println("Updating existing biodata");
            return updateBiodata(biodata);
        } else {
            System.out.println("Inserting new biodata");
            return insertBiodata(biodata);
        }
    }

    /**
     * Insert new biodata
     */
    private boolean insertBiodata(Biodata biodata) {
        System.out.println("insertBiodata() starting...");

        String sql = "INSERT INTO biodata (user_id, date_of_birth, age, height, weight, "+
                    "marital_status, religion, caste, mother_tongue, complexion, blood_group, "+
                    "education, occupation, annual_income, company_name, "+
                    "father_name, father_occupation, mother_name, mother_occupation, siblings, "+
                    "family_type, family_status, address, city, state, country, "+
                    "about_me, hobbies, "+
                    "partner_age_from, partner_age_to, partner_height_from, partner_height_to, "+
                    "partner_religion, partner_education, partner_occupation, partner_income, "+
                    "partner_marital_status, partner_expectations, photo_path, profile_completed) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (conn == null) {
                System.err.println("Database connection is null");
                return false;
            }

            System.out.println("Database connection established");
            System.out.println("Preparing SQL statement...");

            setBiodataParameters(pstmt, biodata);
            System.out.println("Parameters set, executing update...");

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Update executed, rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        biodata.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Biodata saved successfully for user ID: " + biodata.getUserId());
                return true;
            } else {
                System.err.println("WARNING: No rows were affected by INSERT");
                System.err.println("This usually means the INSERT didn't execute properly");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("ERROR inserting biodata:");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            System.err.println("User ID: " + biodata.getUserId());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update existing biodata
     */
    public boolean updateBiodata(Biodata biodata) {
        String sql = "UPDATE biodata SET date_of_birth = ?, age = ?, height = ?, weight = ?, "+
                    "marital_status = ?, religion = ?, caste = ?, mother_tongue = ?, complexion = ?, "+
                    "blood_group = ?, education = ?, occupation = ?, annual_income = ?, company_name = ?, "+
                    "father_name = ?, father_occupation = ?, mother_name = ?, mother_occupation = ?, "+
                    "siblings = ?, family_type = ?, family_status = ?, address = ?, city = ?, state = ?, "+
                    "country = ?, about_me = ?, hobbies = ?, "+
                    "partner_age_from = ?, partner_age_to = ?, partner_height_from = ?, partner_height_to = ?, "+
                    "partner_religion = ?, partner_education = ?, partner_occupation = ?, partner_income = ?, "+
                    "partner_marital_status = ?, partner_expectations = ?, photo_path = ?, "+
                    "profile_completed = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Failed to update biodata: Database connection error");
                return false;
            }

            setUpdateBiodataParameters(pstmt, biodata);
            pstmt.setInt(40, biodata.getUserId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Biodata updated successfully for user ID: " + biodata.getUserId());
                return true;
            } else {
                System.err.println("WARNING: No rows were affected by UPDATE");
                System.err.println("User ID: " + biodata.getUserId());
                System.err.println("This usually means no biodata record exists for this user");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("ERROR updating biodata:");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            System.err.println("User ID: " + biodata.getUserId());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Set parameters for PreparedStatement
     */
    private void setBiodataParameters(PreparedStatement pstmt, Biodata biodata) throws SQLException {
        pstmt.setInt(1, biodata.getUserId());
        pstmt.setDate(2, biodata.getDateOfBirth() != null ? Date.valueOf(biodata.getDateOfBirth()) : null);
        pstmt.setInt(3, biodata.getAge());
        pstmt.setString(4, biodata.getHeight());
        pstmt.setString(5, biodata.getWeight());
        pstmt.setString(6, biodata.getMaritalStatus());
        pstmt.setString(7, biodata.getReligion());
        pstmt.setString(8, biodata.getCaste());
        pstmt.setString(9, biodata.getMotherTongue());
        pstmt.setString(10, biodata.getComplexion());
        pstmt.setString(11, biodata.getBloodGroup());
        pstmt.setString(12, biodata.getEducation());
        pstmt.setString(13, biodata.getOccupation());
        pstmt.setString(14, biodata.getAnnualIncome());
        pstmt.setString(15, biodata.getCompanyName());
        pstmt.setString(16, biodata.getFatherName());
        pstmt.setString(17, biodata.getFatherOccupation());
        pstmt.setString(18, biodata.getMotherName());
        pstmt.setString(19, biodata.getMotherOccupation());
        pstmt.setString(20, biodata.getSiblings());
        pstmt.setString(21, biodata.getFamilyType());
        pstmt.setString(22, biodata.getFamilyStatus());
        pstmt.setString(23, biodata.getAddress());
        pstmt.setString(24, biodata.getCity());
        pstmt.setString(25, biodata.getState());
        pstmt.setString(26, biodata.getCountry());
        pstmt.setString(27, biodata.getAboutMe());
        pstmt.setString(28, biodata.getHobbies());
        pstmt.setInt(29, biodata.getPartnerAgeFrom());
        pstmt.setInt(30, biodata.getPartnerAgeTo());
        pstmt.setString(31, biodata.getPartnerHeightFrom());
        pstmt.setString(32, biodata.getPartnerHeightTo());
        pstmt.setString(33, biodata.getPartnerReligion());
        pstmt.setString(34, biodata.getPartnerEducation());
        pstmt.setString(35, biodata.getPartnerOccupation());
        pstmt.setString(36, biodata.getPartnerIncome());
        pstmt.setString(37, biodata.getPartnerMaritalStatus());
        pstmt.setString(38, biodata.getPartnerExpectations());
        pstmt.setString(39, biodata.getPhotoPath());
        pstmt.setBoolean(40, biodata.isProfileCompleted());
    }

    /**
     * Set parameters for UPDATE PreparedStatement (without user_id in SET clause)
     */
    private void setUpdateBiodataParameters(PreparedStatement pstmt, Biodata biodata) throws SQLException {
        pstmt.setDate(1, biodata.getDateOfBirth() != null ? Date.valueOf(biodata.getDateOfBirth()) : null);
        pstmt.setInt(2, biodata.getAge());
        pstmt.setString(3, biodata.getHeight());
        pstmt.setString(4, biodata.getWeight());
        pstmt.setString(5, biodata.getMaritalStatus());
        pstmt.setString(6, biodata.getReligion());
        pstmt.setString(7, biodata.getCaste());
        pstmt.setString(8, biodata.getMotherTongue());
        pstmt.setString(9, biodata.getComplexion());
        pstmt.setString(10, biodata.getBloodGroup());
        pstmt.setString(11, biodata.getEducation());
        pstmt.setString(12, biodata.getOccupation());
        pstmt.setString(13, biodata.getAnnualIncome());
        pstmt.setString(14, biodata.getCompanyName());
        pstmt.setString(15, biodata.getFatherName());
        pstmt.setString(16, biodata.getFatherOccupation());
        pstmt.setString(17, biodata.getMotherName());
        pstmt.setString(18, biodata.getMotherOccupation());
        pstmt.setString(19, biodata.getSiblings());
        pstmt.setString(20, biodata.getFamilyType());
        pstmt.setString(21, biodata.getFamilyStatus());
        pstmt.setString(22, biodata.getAddress());
        pstmt.setString(23, biodata.getCity());
        pstmt.setString(24, biodata.getState());
        pstmt.setString(25, biodata.getCountry());
        pstmt.setString(26, biodata.getAboutMe());
        pstmt.setString(27, biodata.getHobbies());
        pstmt.setInt(28, biodata.getPartnerAgeFrom());
        pstmt.setInt(29, biodata.getPartnerAgeTo());
        pstmt.setString(30, biodata.getPartnerHeightFrom());
        pstmt.setString(31, biodata.getPartnerHeightTo());
        pstmt.setString(32, biodata.getPartnerReligion());
        pstmt.setString(33, biodata.getPartnerEducation());
        pstmt.setString(34, biodata.getPartnerOccupation());
        pstmt.setString(35, biodata.getPartnerIncome());
        pstmt.setString(36, biodata.getPartnerMaritalStatus());
        pstmt.setString(37, biodata.getPartnerExpectations());
        pstmt.setString(38, biodata.getPhotoPath());
        pstmt.setBoolean(39, biodata.isProfileCompleted());
    }

    /**
     * Check if biodata exists for a user
     */
    public boolean biodataExists(int userId) {
        String sql = "SELECT 1 FROM biodata WHERE user_id = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                return false;
            }

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error checking biodata existence: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get biodata by user ID
     */
    public Biodata getBiodataByUserId(int userId) {
        String sql = "SELECT * FROM biodata WHERE user_id = ?";

        System.out.println("BiodataDAO: Fetching biodata for user_id = " + userId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Database connection is null");
                return null;
            }

            pstmt.setInt(1, userId);

            System.out.println("Executing query: " + sql);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Biodata found in database for user_id = " + userId);
                    Biodata biodata = mapBiodata(rs);
                    System.out.println("Biodata mapped successfully");
                    return biodata;
                } else {
                    System.out.println("No biodata record found in database for user_id = " + userId);
                }
            }

        } catch (SQLException e) {
            System.err.println("SQL Error getting biodata: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Delete biodata by user ID
     */
    public boolean deleteBiodata(int userId) {
        String sql = "DELETE FROM biodata WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                return false;
            }

            pstmt.setInt(1, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting biodata: " + e.getMessage());
        }

        return false;
    }

    /**
     * Map ResultSet to Biodata object
     */
    private Biodata mapBiodata(ResultSet rs) throws SQLException {
        Biodata biodata = new Biodata();

        biodata.setId(rs.getInt("id"));
        biodata.setUserId(rs.getInt("user_id"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            biodata.setDateOfBirth(dob.toLocalDate());
        }

        biodata.setAge(rs.getInt("age"));
        biodata.setHeight(rs.getString("height"));
        biodata.setWeight(rs.getString("weight"));
        biodata.setMaritalStatus(rs.getString("marital_status"));
        biodata.setReligion(rs.getString("religion"));
        biodata.setCaste(rs.getString("caste"));
        biodata.setMotherTongue(rs.getString("mother_tongue"));
        biodata.setComplexion(rs.getString("complexion"));
        biodata.setBloodGroup(rs.getString("blood_group"));

        biodata.setEducation(rs.getString("education"));
        biodata.setOccupation(rs.getString("occupation"));
        biodata.setAnnualIncome(rs.getString("annual_income"));
        biodata.setCompanyName(rs.getString("company_name"));

        biodata.setFatherName(rs.getString("father_name"));
        biodata.setFatherOccupation(rs.getString("father_occupation"));
        biodata.setMotherName(rs.getString("mother_name"));
        biodata.setMotherOccupation(rs.getString("mother_occupation"));
        biodata.setSiblings(rs.getString("siblings"));
        biodata.setFamilyType(rs.getString("family_type"));
        biodata.setFamilyStatus(rs.getString("family_status"));

        biodata.setAddress(rs.getString("address"));
        biodata.setCity(rs.getString("city"));
        biodata.setState(rs.getString("state"));
        biodata.setCountry(rs.getString("country"));

        biodata.setAboutMe(rs.getString("about_me"));
        biodata.setHobbies(rs.getString("hobbies"));

        biodata.setPartnerAgeFrom(rs.getInt("partner_age_from"));
        biodata.setPartnerAgeTo(rs.getInt("partner_age_to"));
        biodata.setPartnerHeightFrom(rs.getString("partner_height_from"));
        biodata.setPartnerHeightTo(rs.getString("partner_height_to"));
        biodata.setPartnerReligion(rs.getString("partner_religion"));
        biodata.setPartnerEducation(rs.getString("partner_education"));
        biodata.setPartnerOccupation(rs.getString("partner_occupation"));
        biodata.setPartnerIncome(rs.getString("partner_income"));
        biodata.setPartnerMaritalStatus(rs.getString("partner_marital_status"));
        biodata.setPartnerExpectations(rs.getString("partner_expectations"));

        biodata.setPhotoPath(rs.getString("photo_path"));
        biodata.setProfileCompleted(rs.getBoolean("profile_completed"));
        biodata.setVerified(rs.getBoolean("is_verified"));

        return biodata;
    }
}

