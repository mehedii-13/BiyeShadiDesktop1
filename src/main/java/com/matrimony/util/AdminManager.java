package com.matrimony.util;

import com.matrimony.dao.UserDAO;
import com.matrimony.model.User;

import java.util.Scanner;

public class AdminManager {

    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("");
        System.out.println("ADMIN USER MANAGEMENT TOOL        ");
        System.out.println("");
        System.out.println();

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Create Admin User");
            System.out.println("2. Promote User to Admin");
            System.out.println("3. Demote Admin to User");
            System.out.println("4. Check User Role");
            System.out.println("5. Exit");
            System.out.print("\nEnter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createAdminUser(scanner);
                case "2" -> promoteToAdmin(scanner);
                case "3" -> demoteToUser(scanner);
                case "4" -> checkUserRole(scanner);
                case "5" -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createAdminUser(Scanner scanner) {
        System.out.println("\n--- Create Admin User ---");

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Gender (Male/Female/Other): ");
        String gender = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User adminUser = new User();
        adminUser.setFullName(fullName);
        adminUser.setEmail(email);
        adminUser.setPhone(phone);
        adminUser.setGender(gender);
        adminUser.setPassword(password);
        adminUser.setRole("admin");

        boolean success = userDAO.registerUser(adminUser);

        if (success) {
            System.out.println("Admin user created successfully!");
        } else {
            System.out.println("Failed to create admin user. Email may already exist.");
        }
    }

    private static void promoteToAdmin(Scanner scanner) {
        System.out.println("\n--- Promote User to Admin ---");

        System.out.print("Enter user email: ");
        String email = scanner.nextLine().trim();

        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        if ("admin".equals(user.getRole())) {
            System.out.println("User is already an admin.");
            return;
        }

        boolean success = userDAO.updateUserRole(user.getId(), "admin");

        if (success) {
            System.out.println("User promoted to admin successfully!");
            System.out.println("Name: " + user.getFullName());
            System.out.println("Email: " + user.getEmail());
        } else {
            System.out.println("Failed to promote user.");
        }
    }

    private static void demoteToUser(Scanner scanner) {
        System.out.println("\n--- Demote Admin to User ---");

        System.out.print("Enter admin email: ");
        String email = scanner.nextLine().trim();

        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        if (!"admin".equals(user.getRole())) {
            System.out.println("User is not an admin.");
            return;
        }

        boolean success = userDAO.updateUserRole(user.getId(), "user");

        if (success) {
            System.out.println("Admin demoted to user successfully!");
            System.out.println("Name: " + user.getFullName());
            System.out.println("Email: " + user.getEmail());
        } else {
            System.out.println("Failed to demote admin.");
        }
    }

    private static void checkUserRole(Scanner scanner) {
        System.out.println("\n Check User Role ");

        System.out.print("Enter user email: ");
        String email = scanner.nextLine().trim();

        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\nUser Information:");
        System.out.println("ID: " + user.getId());
        System.out.println("Name: " + user.getFullName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Phone: " + user.getPhone());
        System.out.println("Gender: " + user.getGender());
        System.out.println("Role: " + user.getRole());
        System.out.println("Is Admin: " + ("admin".equals(user.getRole()) ? "Yes" : "No"));
    }
}

