package com.matrimony.util;

import com.matrimony.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class AddRoleColumn {

    public static void main(String[] args) {
        System.out.println("");
        System.out.println("DATABASE MIGRATION: ADD ROLE COLUMN ");
        System.out.println("\n");

        try {
            addRoleColumn();
        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addRoleColumn() {
        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                System.err.println("Cannot connect to database");
                return;
            }

            System.out.println("Connected to database");

            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "users", "role");

            if (columns.next()) {
                System.out.println("Role column already exists");
                System.out.println("No migration needed");

                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                String columnSize = columns.getString("COLUMN_SIZE");
                String columnDefault = columns.getString("COLUMN_DEF");

                System.out.println("\nCurrent column info:");
                System.out.println("Name: " + columnName);
                System.out.println("Type: " + columnType + "(" + columnSize + ")");
                System.out.println("Default: " + (columnDefault != null ? columnDefault : "user"));

                columns.close();
                return;
            }
            columns.close();

            System.out.println("Role column not found, adding it now...");

            try (Statement stmt = conn.createStatement()) {

                String addColumnSQL =
                    "ALTER TABLE users " +
                    "ADD COLUMN role VARCHAR(20) DEFAULT 'user' NOT NULL";

                stmt.executeUpdate(addColumnSQL);
                System.out.println("Role column added successfully");

                String createIndexSQL =
                    "CREATE INDEX idx_role ON users(role)";

                try {
                    stmt.executeUpdate(createIndexSQL);
                    System.out.println("Index on role column created");
                } catch (Exception e) {
                    System.out.println("Index might already exist: " + e.getMessage());
                }

                String updateSQL =
                    "UPDATE users SET role = 'user' WHERE role IS NULL OR role = ''";

                int updated = stmt.executeUpdate(updateSQL);
                if (updated > 0) {
                    System.out.println("Updated " + updated + "existing users to 'user' role");
                }

                System.out.println("\n Migration completed successfully!");
                System.out.println("\n");

                showTableStructure(conn);
            }

        } catch (Exception e) {
            System.err.println("\n Error during migration:");
            System.err.println("" + e.getMessage());
            e.printStackTrace();
            System.err.println("\n");
        }
    }

    private static void showTableStructure(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            System.out.println("Current users table structure:");
            System.out.println("");

            ResultSet rs = stmt.executeQuery("DESCRIBE users");

            System.out.printf("%-15s %-20s %-10s %-10s%n",
                "Field", "Type", "Null", "Default");
            System.out.println("");

            while (rs.next()) {
                String field = rs.getString("Field");
                String type = rs.getString("Type");
                String nullable = rs.getString("Null");
                String defaultValue = rs.getString("Default");

                System.out.printf("%-15s %-20s %-10s %-10s%n",
                    field, type, nullable,
                    defaultValue != null ? defaultValue : "NULL");
            }

            System.out.println("\n");
            rs.close();

        } catch (Exception e) {
            System.err.println("Could not show table structure: " + e.getMessage());
        }
    }
}

