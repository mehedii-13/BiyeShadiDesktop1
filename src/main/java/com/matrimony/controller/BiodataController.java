package com.matrimony.controller;

import com.matrimony.dao.BiodataDAO;
import com.matrimony.model.Biodata;
import com.matrimony.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;

public class BiodataController {

    @FXML private DatePicker dobPicker;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> heightCombo;
    @FXML private TextField weightField;
    @FXML private ComboBox<String> maritalStatusCombo;
    @FXML private ComboBox<String> religionCombo;
    @FXML private TextField casteField;
    @FXML private ComboBox<String> motherTongueCombo;
    @FXML private ComboBox<String> complexionCombo;
    @FXML private ComboBox<String> bloodGroupCombo;

    @FXML private ComboBox<String> educationCombo;
    @FXML private TextField occupationField;
    @FXML private TextField companyField;
    @FXML private ComboBox<String> incomeCombo;

    @FXML private TextField fatherNameField;
    @FXML private TextField fatherOccupationField;
    @FXML private TextField motherNameField;
    @FXML private TextField motherOccupationField;
    @FXML private TextField siblingsField;
    @FXML private ComboBox<String> familyTypeCombo;
    @FXML private ComboBox<String> familyStatusCombo;

    @FXML private TextArea addressArea;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField countryField;
    @FXML private TextField phoneField;

    @FXML private TextArea aboutMeArea;
    @FXML private TextArea hobbiesArea;

    @FXML private TextField partnerAgeFromField;
    @FXML private TextField partnerAgeToField;
    @FXML private ComboBox<String> partnerHeightFromCombo;
    @FXML private ComboBox<String> partnerHeightToCombo;
    @FXML private ComboBox<String> partnerReligionCombo;
    @FXML private TextField partnerOccupationField;
    @FXML private ComboBox<String> partnerEducationCombo;
    @FXML private ComboBox<String> partnerIncomeCombo;
    @FXML private ComboBox<String> partnerMaritalStatusCombo;
    @FXML private TextArea partnerExpectationsArea;

    @FXML private Label photoLabel;

    @FXML private TabPane biodataTabPane;

    @FXML private VBox biodataFormRoot;

    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private User currentUser;
    private BiodataDAO biodataDAO;
    private String selectedPhotoPath;

    @FXML
    public void initialize() {
        biodataDAO = new BiodataDAO();
        initializeComboBoxes();
        setupDatePickerListener();
        System.out.println("=== BiodataController.initialize() called ===");
        System.out.println("New BiodataController instance created: " + this.hashCode());
        System.out.println("currentUser at initialization: " + (currentUser != null ? currentUser.getFullName() : "null"));
        System.out.println("BiodataDAO initialized: " + (biodataDAO != null));
        System.out.println("=== End initialize() ===");
    }

    public void setUser(User user) {
        System.out.println("=== BiodataController.setUser() called ===");
        System.out.println("Previous currentUser: " + (this.currentUser != null ? this.currentUser.getFullName() : "null"));
        System.out.println("New user parameter: " + (user != null ? user.getFullName() : "null"));

        this.currentUser = user;

        if (user != null) {
            System.out.println("User set in BiodataController: " + user.getFullName() + " (ID: " + user.getId() + ")");
            System.out.println("After assignment, currentUser: " + this.currentUser.getFullName());
            loadExistingBiodata();
        } else {
            System.out.println("User is null in setUser()");
        }
        System.out.println("=== End setUser() ===");
    }

    private void initializeComboBoxes() {
        String[] heights = {
            "4'0\"", "4'1\"", "4'2\"", "4'3\"", "4'4\"", "4'5\"", "4'6\"", "4'7\"", "4'8\"", "4'9\"", "4'10\"", "4'11\"",
            "5'0\"", "5'1\"", "5'2\"", "5'3\"", "5'4\"", "5'5\"", "5'6\"", "5'7\"", "5'8\"", "5'9\"", "5'10\"", "5'11\"",
            "6'0\"", "6'1\"", "6'2\"", "6'3\"", "6'4\"", "6'5\"", "6'6\"", "6'7\""
        };
        if (heightCombo != null) heightCombo.setItems(FXCollections.observableArrayList(heights));
        if (partnerHeightFromCombo != null) partnerHeightFromCombo.setItems(FXCollections.observableArrayList(heights));
        if (partnerHeightToCombo != null) partnerHeightToCombo.setItems(FXCollections.observableArrayList(heights));

        if (maritalStatusCombo != null) {
            maritalStatusCombo.setItems(FXCollections.observableArrayList(
                "Never Married", "Divorced", "Widowed", "Awaiting Divorce"
            ));
        }
        if (partnerMaritalStatusCombo != null) {
            partnerMaritalStatusCombo.setItems(FXCollections.observableArrayList(
                "Never Married", "Divorced", "Widowed", "Awaiting Divorce", "Doesn't Matter"
            ));
        }

        if (religionCombo != null) {
            religionCombo.setItems(FXCollections.observableArrayList(
                "Islam", "Hinduism", "Buddhism", "Christianity", "Others"
            ));
        }
        if (partnerReligionCombo != null) {
            partnerReligionCombo.setItems(FXCollections.observableArrayList(
                "Islam", "Hinduism", "Buddhism", "Christianity", "Others", "Doesn't Matter"
            ));
        }

        if (motherTongueCombo != null) {
            motherTongueCombo.setItems(FXCollections.observableArrayList(
                "Bengali", "English", "Hindi", "Urdu", "Arabic", "Others"
            ));
        }

        if (complexionCombo != null) {
            complexionCombo.setItems(FXCollections.observableArrayList(
                "Very Fair", "Fair", "Wheatish", "Dark", "Very Dark"
            ));
        }

        if (bloodGroupCombo != null) {
            bloodGroupCombo.setItems(FXCollections.observableArrayList(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
            ));
        }

        String[] educationLevels = {
            "High School", "Diploma", "Bachelor's Degree", "Master's Degree",
            "PhD", "Professional Degree", "Others"
        };
        if (educationCombo != null) educationCombo.setItems(FXCollections.observableArrayList(educationLevels));
        if (partnerEducationCombo != null) partnerEducationCombo.setItems(FXCollections.observableArrayList(educationLevels));

        String[] incomeRanges = {
            "Below 2 Lakh", "2-5 Lakh", "5-10 Lakh", "10-20 Lakh",
            "20-50 Lakh", "50 Lakh - 1 Crore", "Above 1 Crore"
        };
        if (incomeCombo != null) incomeCombo.setItems(FXCollections.observableArrayList(incomeRanges));
        if (partnerIncomeCombo != null) partnerIncomeCombo.setItems(FXCollections.observableArrayList(incomeRanges));

        if (familyTypeCombo != null) {
            familyTypeCombo.setItems(FXCollections.observableArrayList(
                "Joint Family", "Nuclear Family", "Others"
            ));
        }

        if (familyStatusCombo != null) {
            familyStatusCombo.setItems(FXCollections.observableArrayList(
                "Middle Class", "Upper Middle Class", "Rich", "Affluent"
            ));
        }
    }

    private void setupDatePickerListener() {
        if (dobPicker != null && ageField != null) {
            dobPicker.valueProperty().addListener((obs, oldDate, newDate) -> {
                if (newDate != null) {
                    int age = Period.between(newDate, LocalDate.now()).getYears();
                    ageField.setText(String.valueOf(age));
                }
            });
        }
    }

    private void loadExistingBiodata() {
        if (currentUser == null) {
            System.out.println("Cannot load biodata: currentUser is null");
            return;
        }

        System.out.println("Loading biodata for user ID: " + currentUser.getId());

        try {
            Biodata existingBiodata = biodataDAO.getBiodataByUserId(currentUser.getId());

            if (existingBiodata != null) {
                System.out.println("Biodata found! Populating form...");
                populateFormWithBiodata(existingBiodata);
            } else {
                System.out.println("No existing biodata found for user ID: " + currentUser.getId());
            }
        } catch (Exception e) {
            System.err.println("Error loading biodata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateFormWithBiodata(Biodata biodata) {
        try {
            System.out.println("Populating form with biodata...");

            if (biodata.getDateOfBirth() != null) {
                dobPicker.setValue(biodata.getDateOfBirth());
                System.out.println("Date of birth: " + biodata.getDateOfBirth());
            }
            if (biodata.getAge() > 0) {
                ageField.setText(String.valueOf(biodata.getAge()));
                System.out.println("Age: " + biodata.getAge());
            }
            if (biodata.getHeight() != null) heightCombo.setValue(biodata.getHeight());
            if (biodata.getWeight() != null) weightField.setText(biodata.getWeight());
            if (biodata.getMaritalStatus() != null) maritalStatusCombo.setValue(biodata.getMaritalStatus());
            if (biodata.getReligion() != null) religionCombo.setValue(biodata.getReligion());
            if (biodata.getCaste() != null) casteField.setText(biodata.getCaste());
            if (biodata.getMotherTongue() != null) motherTongueCombo.setValue(biodata.getMotherTongue());
            if (biodata.getComplexion() != null) complexionCombo.setValue(biodata.getComplexion());
            if (biodata.getBloodGroup() != null) bloodGroupCombo.setValue(biodata.getBloodGroup());

            if (biodata.getEducation() != null) {
                educationCombo.setValue(biodata.getEducation());
                System.out.println("Education: " + biodata.getEducation());
            }
            if (biodata.getOccupation() != null) {
                occupationField.setText(biodata.getOccupation());
                System.out.println("Occupation: " + biodata.getOccupation());
            }
            if (biodata.getCompanyName() != null) companyField.setText(biodata.getCompanyName());
            if (biodata.getAnnualIncome() != null) incomeCombo.setValue(biodata.getAnnualIncome());

            if (biodata.getFatherName() != null) fatherNameField.setText(biodata.getFatherName());
            if (biodata.getFatherOccupation() != null) fatherOccupationField.setText(biodata.getFatherOccupation());
            if (biodata.getMotherName() != null) motherNameField.setText(biodata.getMotherName());
            if (biodata.getMotherOccupation() != null) motherOccupationField.setText(biodata.getMotherOccupation());
            if (biodata.getSiblings() != null) siblingsField.setText(biodata.getSiblings());
            if (biodata.getFamilyType() != null) familyTypeCombo.setValue(biodata.getFamilyType());
            if (biodata.getFamilyStatus() != null) familyStatusCombo.setValue(biodata.getFamilyStatus());

            if (biodata.getAddress() != null) addressArea.setText(biodata.getAddress());
            if (biodata.getCity() != null) {
                cityField.setText(biodata.getCity());
                System.out.println("City: " + biodata.getCity());
            }
            if (biodata.getState() != null) stateField.setText(biodata.getState());
            if (biodata.getCountry() != null) countryField.setText(biodata.getCountry());

            if (biodata.getAboutMe() != null) aboutMeArea.setText(biodata.getAboutMe());
            if (biodata.getHobbies() != null) hobbiesArea.setText(biodata.getHobbies());

            if (biodata.getPartnerAgeFrom() > 0) partnerAgeFromField.setText(String.valueOf(biodata.getPartnerAgeFrom()));
            if (biodata.getPartnerAgeTo() > 0) partnerAgeToField.setText(String.valueOf(biodata.getPartnerAgeTo()));
            if (biodata.getPartnerHeightFrom() != null) partnerHeightFromCombo.setValue(biodata.getPartnerHeightFrom());
            if (biodata.getPartnerHeightTo() != null) partnerHeightToCombo.setValue(biodata.getPartnerHeightTo());
            if (biodata.getPartnerReligion() != null) partnerReligionCombo.setValue(biodata.getPartnerReligion());
            if (biodata.getPartnerEducation() != null) partnerEducationCombo.setValue(biodata.getPartnerEducation());
            if (biodata.getPartnerOccupation() != null) partnerOccupationField.setText(biodata.getPartnerOccupation());
            if (biodata.getPartnerIncome() != null) partnerIncomeCombo.setValue(biodata.getPartnerIncome());
            if (biodata.getPartnerMaritalStatus() != null) partnerMaritalStatusCombo.setValue(biodata.getPartnerMaritalStatus());
            if (biodata.getPartnerExpectations() != null) partnerExpectationsArea.setText(biodata.getPartnerExpectations());

            if (biodata.getPhotoPath() != null) {
                selectedPhotoPath = biodata.getPhotoPath();
                photoLabel.setText(new File(biodata.getPhotoPath()).getName());
                System.out.println("Photo: " + new File(biodata.getPhotoPath()).getName());
            }

            System.out.println("Form populated successfully!");

        } catch (Exception e) {
            System.err.println("Error populating form: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                "Failed to load biodata into form: " + e.getMessage());
        }
    }

    @FXML
    private void handlePhotoUpload() {
        if (photoLabel == null) {
            System.err.println("Photo label not initialized");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(photoLabel.getScene().getWindow());

        if (selectedFile != null) {
            if (selectedFile.length() > 5 * 1024 * 1024) {
                showAlert(Alert.AlertType.WARNING, "File Too Large",
                    "Please select a photo smaller than 5MB");
                return;
            }

            try {
                Path uploadsDir = Paths.get("uploads", "photos");
                Files.createDirectories(uploadsDir);

                String fileName = "user_" + currentUser.getId() + "_" +
                                System.currentTimeMillis() + getFileExtension(selectedFile);
                Path targetPath = uploadsDir.resolve(fileName);
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                selectedPhotoPath = targetPath.toString();
                photoLabel.setText(selectedFile.getName());
                photoLabel.setStyle("-fx-text-fill: #4CAF50;");

                showAlert(Alert.AlertType.INFORMATION, "Success", "Photo uploaded successfully!");

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Upload Failed",
                    "Failed to upload photo: " + e.getMessage());
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return (lastDot > 0) ? name.substring(lastDot) : "";
    }

    @FXML
    private void handleSaveDraft() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
            return;
        }

        try {
            Biodata biodata = collectBiodataFromForm();
            biodata.setUserId(currentUser.getId());

            boolean success = biodataDAO.saveBiodata(biodata);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Draft Saved",
                    "Biodata draft saved to database successfully!");
                System.out.println("Draft saved to database");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to save draft to database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                "An error occurred while saving draft: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        System.out.println("=== handleSubmit() called ===");
        System.out.println("BiodataController instance: " + this.hashCode());
        System.out.println("currentUser: " + (currentUser != null ? currentUser.getFullName() + " (ID: " + currentUser.getId() + ")" : "NULL"));
        System.out.println("biodataDAO: " + (biodataDAO != null ? "initialized" : "NULL"));

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Session Error",
                "User session is not available. Please return to dashboard and try again.");
            System.err.println("currentUser is null in handleSubmit()");
            return;
        }

        System.out.println("User session valid, proceeding with validation...");

        if (!validateRequiredFields()) {
            System.out.println("Validation failed, not submitting");
            return;
        }

        System.out.println("Validation passed, collecting form data...");

        try {
            Biodata biodata = collectBiodataFromForm();
            System.out.println("Form data collected");

            biodata.setUserId(currentUser.getId());
            System.out.println("User ID set: " + currentUser.getId());

            boolean success = biodataDAO.saveBiodata(biodata);
            System.out.println("saveBiodata() returned: " + success);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Biodata submitted and saved to database successfully!");
                System.out.println("Biodata saved to database successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to save biodata to database.");
                System.err.println("saveBiodata() returned false");
            }
        } catch (Exception e) {
            System.err.println("Exception in handleSubmit(): " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                "An error occurred while saving: " + e.getMessage());
        }

        System.out.println("=== End handleSubmit() ===");
    }

    private boolean validateRequiredFields() {
        StringBuilder errors = new StringBuilder();

        if (dobPicker == null || dobPicker.getValue() == null) {
            errors.append("Date of Birth is required\n");
        }
        if (ageField == null || ageField.getText().trim().isEmpty()) {
            errors.append("Age is required\n");
        }
        if (heightCombo == null || heightCombo.getValue() == null) {
            errors.append("Height is required\n");
        }
        if (maritalStatusCombo == null || maritalStatusCombo.getValue() == null) {
            errors.append("Marital Status is required\n");
        }
        if (religionCombo == null || religionCombo.getValue() == null) {
            errors.append("Religion is required\n");
        }
        if (educationCombo == null || educationCombo.getValue() == null) {
            errors.append("Education is required\n");
        }
        if (occupationField == null || occupationField.getText().trim().isEmpty()) {
            errors.append("Occupation is required\n");
        }
        if (cityField == null || cityField.getText().trim().isEmpty()) {
            errors.append("City is required\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Required Fields Missing",
                "Please fill in the following required fields:\n\n" + errors.toString());
            return false;
        }

        return true;
    }

    private Biodata collectBiodataFromForm() {
        Biodata biodata = new Biodata();

        if (dobPicker != null) biodata.setDateOfBirth(dobPicker.getValue());
        if (ageField != null) {
            try {
                biodata.setAge(Integer.parseInt(ageField.getText().trim()));
            } catch (NumberFormatException e) {
                biodata.setAge(0);
            }
        }
        if (heightCombo != null) biodata.setHeight(heightCombo.getValue());
        if (weightField != null) biodata.setWeight(weightField.getText().trim());
        if (maritalStatusCombo != null) biodata.setMaritalStatus(maritalStatusCombo.getValue());
        if (religionCombo != null) biodata.setReligion(religionCombo.getValue());
        if (casteField != null) biodata.setCaste(casteField.getText().trim());
        if (motherTongueCombo != null) biodata.setMotherTongue(motherTongueCombo.getValue());
        if (complexionCombo != null) biodata.setComplexion(complexionCombo.getValue());
        if (bloodGroupCombo != null) biodata.setBloodGroup(bloodGroupCombo.getValue());

        if (educationCombo != null) biodata.setEducation(educationCombo.getValue());
        if (occupationField != null) biodata.setOccupation(occupationField.getText().trim());
        if (companyField != null) biodata.setCompanyName(companyField.getText().trim());
        if (incomeCombo != null) biodata.setAnnualIncome(incomeCombo.getValue());

        if (fatherNameField != null) biodata.setFatherName(fatherNameField.getText().trim());
        if (fatherOccupationField != null) biodata.setFatherOccupation(fatherOccupationField.getText().trim());
        if (motherNameField != null) biodata.setMotherName(motherNameField.getText().trim());
        if (motherOccupationField != null) biodata.setMotherOccupation(motherOccupationField.getText().trim());
        if (siblingsField != null) biodata.setSiblings(siblingsField.getText().trim());
        if (familyTypeCombo != null) biodata.setFamilyType(familyTypeCombo.getValue());
        if (familyStatusCombo != null) biodata.setFamilyStatus(familyStatusCombo.getValue());

        if (addressArea != null) biodata.setAddress(addressArea.getText().trim());
        if (cityField != null) biodata.setCity(cityField.getText().trim());
        if (stateField != null) biodata.setState(stateField.getText().trim());
        if (countryField != null) biodata.setCountry(countryField.getText().trim());

        if (aboutMeArea != null) biodata.setAboutMe(aboutMeArea.getText().trim());
        if (hobbiesArea != null) biodata.setHobbies(hobbiesArea.getText().trim());

        if (partnerAgeFromField != null) {
            try {
                biodata.setPartnerAgeFrom(Integer.parseInt(partnerAgeFromField.getText().trim()));
            } catch (NumberFormatException e) {
                biodata.setPartnerAgeFrom(0);
            }
        }
        if (partnerAgeToField != null) {
            try {
                biodata.setPartnerAgeTo(Integer.parseInt(partnerAgeToField.getText().trim()));
            } catch (NumberFormatException e) {
                biodata.setPartnerAgeTo(0);
            }
        }
        if (partnerHeightFromCombo != null) biodata.setPartnerHeightFrom(partnerHeightFromCombo.getValue());
        if (partnerHeightToCombo != null) biodata.setPartnerHeightTo(partnerHeightToCombo.getValue());
        if (partnerReligionCombo != null) biodata.setPartnerReligion(partnerReligionCombo.getValue());
        if (partnerEducationCombo != null) biodata.setPartnerEducation(partnerEducationCombo.getValue());
        if (partnerOccupationField != null) biodata.setPartnerOccupation(partnerOccupationField.getText().trim());
        if (partnerIncomeCombo != null) biodata.setPartnerIncome(partnerIncomeCombo.getValue());
        if (partnerMaritalStatusCombo != null) biodata.setPartnerMaritalStatus(partnerMaritalStatusCombo.getValue());
        if (partnerExpectationsArea != null) biodata.setPartnerExpectations(partnerExpectationsArea.getText().trim());

        biodata.setPhotoPath(selectedPhotoPath);

        return biodata;
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            System.out.println("Going back to dashboard...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent dashboard = loader.load();
            System.out.println("UserDashboard.fxml loaded");

            UserDashboardController controller = loader.getController();
            if (currentUser != null) {
                controller.setUser(currentUser);
            }

            Scene scene = new Scene(dashboard);

            Stage stage = null;
            if (biodataFormRoot != null) {
                stage = (Stage) biodataFormRoot.getScene().getWindow();
            } else if (dobPicker != null) {
                stage = (Stage) dobPicker.getScene().getWindow();
            } else if (biodataTabPane != null) {
                stage = (Stage) biodataTabPane.getScene().getWindow();
            }

            if (stage != null) {
                stage.setScene(scene);
                if (currentUser != null) {
                    stage.setTitle("Dashboard - " + currentUser.getFullName());
                } else {
                    stage.setTitle("Dashboard");
                }
                System.out.println("Navigated back to dashboard!");
            } else {
                System.err.println("Could not get stage reference");
                showAlert(Alert.AlertType.ERROR, "Error", "Could not navigate back to dashboard.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard.");
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancel button clicked - returning to dashboard");
        navigateBackToDashboard();
    }

    private void navigateBackToDashboard() {
        try {
            System.out.println("Going back to dashboard...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent dashboard = loader.load();
            System.out.println("UserDashboard.fxml loaded");

            UserDashboardController controller = loader.getController();
            if (currentUser != null) {
                controller.setUser(currentUser);
            }

            Scene scene = new Scene(dashboard);

            Stage stage = null;
            if (biodataFormRoot != null) {
                stage = (Stage) biodataFormRoot.getScene().getWindow();
            } else if (dobPicker != null) {
                stage = (Stage) dobPicker.getScene().getWindow();
            } else if (biodataTabPane != null) {
                stage = (Stage) biodataTabPane.getScene().getWindow();
            } else if (submitButton != null) {
                stage = (Stage) submitButton.getScene().getWindow();
            }

            if (stage != null) {
                stage.setScene(scene);
                if (currentUser != null) {
                    stage.setTitle("Dashboard - " + currentUser.getFullName());
                } else {
                    stage.setTitle("Dashboard");
                }
                System.out.println("Navigated back to dashboard!");
            } else {
                System.err.println("Could not get stage reference");
                showAlert(Alert.AlertType.ERROR, "Error", "Could not navigate back to dashboard.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

