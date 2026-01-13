package com.matrimony.model;

public class MatchProfile {
    private int userId;
    private String fullName;
    private String gender;
    private int age;
    private String height;
    private String maritalStatus;
    private String religion;
    private String education;
    private String occupation;
    private String city;
    private String state;
    private String country;
    private String annualIncome;
    private String photoPath;

    public MatchProfile() {}

    public MatchProfile(int userId, String fullName, String gender, int age, String height,
                       String maritalStatus, String religion, String education, String occupation,
                       String city, String state, String country, String annualIncome, String photoPath) {
        this.userId = userId;
        this.fullName = fullName;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.maritalStatus = maritalStatus;
        this.religion = religion;
        this.education = education;
        this.occupation = occupation;
        this.city = city;
        this.state = state;
        this.country = country;
        this.annualIncome = annualIncome;
        this.photoPath = photoPath;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(String annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getLocation() {
        StringBuilder location = new StringBuilder();
        if (city != null && !city.isEmpty()) location.append(city);
        if (state != null && !state.isEmpty()) {
            if (location.length() > 0) location.append(", ");
            location.append(state);
        }
        if (country != null && !country.isEmpty()) {
            if (location.length() > 0) location.append(", ");
            location.append(country);
        }
        return location.length() > 0 ? location.toString() : "Not specified";
    }
}

