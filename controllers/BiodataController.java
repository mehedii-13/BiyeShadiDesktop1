package controllers;

import dao.BiodataDAO;
import models.Biodata;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BiodataController {
    private BiodataDAO biodataDAO;

    public BiodataController(Connection connection) {
        this.biodataDAO = new BiodataDAO(connection);
    }

    public Map<String, Object> addBiodata(int userId, Map<String, String> biodataInfo) {
        Map<String, Object> response = new HashMap<>();

        try {
            Biodata biodata = new Biodata();
            biodata.setUserId(userId);
            biodata.setFullName(biodataInfo.get("fullName"));
            biodata.setDateOfBirth(LocalDate.parse(biodataInfo.get("dateOfBirth")));
            biodata.setGender(biodataInfo.get("gender"));
            biodata.setAddress(biodataInfo.get("address"));
            biodata.setPhone(biodataInfo.get("phone"));
            biodata.setEmail(biodataInfo.get("email"));
            biodata.setOccupation(biodataInfo.get("occupation"));
            biodata.setNationality(biodataInfo.get("nationality"));

            biodataDAO.create(biodata);
            response.put("success", true);
            response.put("message", "Biodata added successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    public Map<String, Object> getBiodata(int userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Biodata biodata = biodataDAO.getByUserId(userId);
            response.put("success", true);
            response.put("data", biodata);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    public Map<String, Object> updateBiodata(int biodataId, Map<String, String> biodataInfo) {
        Map<String, Object> response = new HashMap<>();

        try {
            Biodata biodata = new Biodata();
            biodata.setId(biodataId);
            biodata.setUserId(Integer.parseInt(biodataInfo.get("userId")));
            biodata.setFullName(biodataInfo.get("fullName"));
            biodata.setDateOfBirth(LocalDate.parse(biodataInfo.get("dateOfBirth")));
            biodata.setGender(biodataInfo.get("gender"));
            biodata.setAddress(biodataInfo.get("address"));
            biodata.setPhone(biodataInfo.get("phone"));
            biodata.setEmail(biodataInfo.get("email"));
            biodata.setOccupation(biodataInfo.get("occupation"));
            biodata.setNationality(biodataInfo.get("nationality"));

            biodataDAO.update(biodata);
            response.put("success", true);
            response.put("message", "Biodata updated successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }
}

