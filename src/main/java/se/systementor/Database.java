package se.systementor;

import se.systementor.db.DatabaseConnection; // Importera den gemensamma anslutningsklassen
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    // Hämtar alla produkter från databasen
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT p.id, p.name, p.price, p.vat_rate, p.category_id " +
                "FROM product p " +
                "JOIN categories c ON p.category_id = c.id " +
                "ORDER BY c.name, p.name";

        try (Connection conn = DatabaseConnection.getConnection(); // Använder DatabaseConnection
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("vat_rate"),
                        rs.getInt("category_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    // Hämtar alla kategorier från databasen
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM categories ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection(); // Använder DatabaseConnection
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }
}
