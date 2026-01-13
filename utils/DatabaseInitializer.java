package utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void createBiodataTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS biodata (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "full_name VARCHAR(255) NOT NULL, " +
                "date_of_birth DATE NOT NULL, " +
                "gender VARCHAR(20) NOT NULL, " +
                "address TEXT NOT NULL, " +
                "phone VARCHAR(20) NOT NULL, " +
                "email VARCHAR(255) NOT NULL, " +
                "occupation VARCHAR(100) NOT NULL, " +
                "nationality VARCHAR(100) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Biodata table created successfully");
        }
    }
}

