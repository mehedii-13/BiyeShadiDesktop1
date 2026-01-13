package com.matrimony.model;

import java.time.LocalDate;

public class Biodata {
    private int id;
    private int userId;

    private LocalDate dateOfBirth;
    private int age;
    private String height;
    private String weight;
    private String maritalStatus;
    private String religion;
    private String caste;
    private String motherTongue;
    private String complexion;
    private String bloodGroup;

    private String education;
    private String occupation;
    private String annualIncome;
    private String companyName;

    private String fatherName;
    private String fatherOccupation;
    private String motherName;
    private String motherOccupation;
    private String siblings;
    private String familyType;
    private String familyStatus;

    private String address;
    private String city;
    private String state;
    private String country;

    private String aboutMe;
    private String hobbies;

    private int partnerAgeFrom;
    private int partnerAgeTo;
    private String partnerHeightFrom;
    private String partnerHeightTo;
    private String partnerReligion;
    private String partnerEducation;
    private String partnerOccupation;
    private String partnerIncome;
    private String partnerMaritalStatus;
    private String partnerExpectations;

    private String photoPath;

    private boolean profileCompleted;
    private boolean verified;

    public Biodata() {
        this.country = "Bangladesh";
        this.profileCompleted = false;
        this.verified = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
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

    public String getCaste() {
        return caste;
    }

    public void setCaste(String caste) {
        this.caste = caste;
    }

    public String getMotherTongue() {
        return motherTongue;
    }

    public void setMotherTongue(String motherTongue) {
        this.motherTongue = motherTongue;
    }

    public String getComplexion() {
        return complexion;
    }

    public void setComplexion(String complexion) {
        this.complexion = complexion;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
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

    public String getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(String annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherOccupation() {
        return fatherOccupation;
    }

    public void setFatherOccupation(String fatherOccupation) {
        this.fatherOccupation = fatherOccupation;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherOccupation() {
        return motherOccupation;
    }

    public void setMotherOccupation(String motherOccupation) {
        this.motherOccupation = motherOccupation;
    }

    public String getSiblings() {
        return siblings;
    }

    public void setSiblings(String siblings) {
        this.siblings = siblings;
    }

    public String getFamilyType() {
        return familyType;
    }

    public void setFamilyType(String familyType) {
        this.familyType = familyType;
    }

    public String getFamilyStatus() {
        return familyStatus;
    }

    public void setFamilyStatus(String familyStatus) {
        this.familyStatus = familyStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public int getPartnerAgeFrom() {
        return partnerAgeFrom;
    }

    public void setPartnerAgeFrom(int partnerAgeFrom) {
        this.partnerAgeFrom = partnerAgeFrom;
    }

    public int getPartnerAgeTo() {
        return partnerAgeTo;
    }

    public void setPartnerAgeTo(int partnerAgeTo) {
        this.partnerAgeTo = partnerAgeTo;
    }

    public String getPartnerHeightFrom() {
        return partnerHeightFrom;
    }

    public void setPartnerHeightFrom(String partnerHeightFrom) {
        this.partnerHeightFrom = partnerHeightFrom;
    }

    public String getPartnerHeightTo() {
        return partnerHeightTo;
    }

    public void setPartnerHeightTo(String partnerHeightTo) {
        this.partnerHeightTo = partnerHeightTo;
    }

    public String getPartnerReligion() {
        return partnerReligion;
    }

    public void setPartnerReligion(String partnerReligion) {
        this.partnerReligion = partnerReligion;
    }

    public String getPartnerEducation() {
        return partnerEducation;
    }

    public void setPartnerEducation(String partnerEducation) {
        this.partnerEducation = partnerEducation;
    }

    public String getPartnerOccupation() {
        return partnerOccupation;
    }

    public void setPartnerOccupation(String partnerOccupation) {
        this.partnerOccupation = partnerOccupation;
    }

    public String getPartnerIncome() {
        return partnerIncome;
    }

    public void setPartnerIncome(String partnerIncome) {
        this.partnerIncome = partnerIncome;
    }

    public String getPartnerMaritalStatus() {
        return partnerMaritalStatus;
    }

    public void setPartnerMaritalStatus(String partnerMaritalStatus) {
        this.partnerMaritalStatus = partnerMaritalStatus;
    }

    public String getPartnerExpectations() {
        return partnerExpectations;
    }

    public void setPartnerExpectations(String partnerExpectations) {
        this.partnerExpectations = partnerExpectations;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int calculateCompletionPercentage() {
        int totalFields = 30;
        int filledFields = 0;

        if (dateOfBirth != null) filledFields++;
        if (height != null && !height.isEmpty()) filledFields++;
        if (weight != null && !weight.isEmpty()) filledFields++;
        if (maritalStatus != null && !maritalStatus.isEmpty()) filledFields++;
        if (religion != null && !religion.isEmpty()) filledFields++;
        if (caste != null && !caste.isEmpty()) filledFields++;
        if (motherTongue != null && !motherTongue.isEmpty()) filledFields++;
        if (complexion != null && !complexion.isEmpty()) filledFields++;
        if (bloodGroup != null && !bloodGroup.isEmpty()) filledFields++;
        if (education != null && !education.isEmpty()) filledFields++;
        if (occupation != null && !occupation.isEmpty()) filledFields++;
        if (annualIncome != null && !annualIncome.isEmpty()) filledFields++;
        if (fatherName != null && !fatherName.isEmpty()) filledFields++;
        if (motherName != null && !motherName.isEmpty()) filledFields++;
        if (familyType != null && !familyType.isEmpty()) filledFields++;
        if (address != null && !address.isEmpty()) filledFields++;
        if (city != null && !city.isEmpty()) filledFields++;
        if (state != null && !state.isEmpty()) filledFields++;
        if (aboutMe != null && !aboutMe.isEmpty()) filledFields++;
        if (hobbies != null && !hobbies.isEmpty()) filledFields++;
        if (partnerAgeFrom > 0) filledFields++;
        if (partnerAgeTo > 0) filledFields++;
        if (partnerReligion != null && !partnerReligion.isEmpty()) filledFields++;
        if (partnerEducation != null && !partnerEducation.isEmpty()) filledFields++;
        if (partnerOccupation != null && !partnerOccupation.isEmpty()) filledFields++;
        if (partnerMaritalStatus != null && !partnerMaritalStatus.isEmpty()) filledFields++;
        if (partnerExpectations != null && !partnerExpectations.isEmpty()) filledFields++;
        if (photoPath != null && !photoPath.isEmpty()) filledFields++;

        return (filledFields * 100) / totalFields;
    }
}

