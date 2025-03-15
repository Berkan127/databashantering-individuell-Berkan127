package se.systementor.db;

import se.systementor.models.OrderItem;
import se.systementor.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    // Hämtar alla produkter från databasen och returnerar en lista av produkter
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>(); // Skapar en lista för att lagra produkter
        String sql = "SELECT ProductId, Name, Price, VatRate FROM Products"; // SQL-fråga för att hämta produkter

        try (Connection conn = DatabaseConnection.getConnection(); // Skapar en anslutning till databasen
             Statement stmt = conn.createStatement(); // Skapar ett statement för att köra SQL
             ResultSet rs = stmt.executeQuery(sql)) { // Kör SQL-frågan och får resultat

            while (rs.next()) { // Loopar igenom varje rad i resultatet
                Product product = new Product(
                        rs.getInt("ProductId"), // Hämtar produktens ID
                        rs.getString("Name"), // Hämtar produktens namn
                        rs.getDouble("Price"), // Hämtar priset på produkten
                        rs.getDouble("VatRate") // Hämtar momssatsen
                );
                products.add(product); // Lägger till produkten i listan
            }
        } catch (SQLException e) { // Om något går fel med databasen
            e.printStackTrace(); // Skriver ut felmeddelandet
        }
        return products; // Returnerar listan med produkter
    }

    // Sparar en order i databasen
    public void saveOrder(List<OrderItem> items, String receiptNumber, double totalAmount, double totalVat) {
        try (Connection conn = DatabaseConnection.getConnection()) { // Skapar en anslutning till databasen
            conn.setAutoCommit(false); // Stänger av auto-commit för att hantera transaktioner manuellt
            try {
                // SQL-fråga för att spara order i databasen och denna funkar ej frågs stefan
                String orderSql = "INSERT INTO Orders (ReceiptNumber, OrderDateTime, TotalAmount, TotalVat) VALUES (?, NOW(), ?, ?)";
                PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                orderStmt.setString(1, receiptNumber); // Sätter kvittonumret
                orderStmt.setDouble(2, totalAmount); // Sätter totalbeloppet
                orderStmt.setDouble(3, totalVat); // Sätter total moms
                orderStmt.executeUpdate(); // Kör SQL-frågan

                // Hämtar det genererade order-ID:t från databasen
                ResultSet rs = orderStmt.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1); // Hämtar order-ID

                    // SQL-fråga för att spara orderdetaljer
                    String detailSql = "INSERT INTO OrderDetails (OrderId, ProductId, Quantity, UnitPrice, VatRate) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement detailStmt = conn.prepareStatement(detailSql);

                    // Loopar igenom alla produkter i ordern och sparar dem i orderdetaljer-tabellen
                    for (OrderItem item : items) {
                        detailStmt.setInt(1, orderId); // Sätter order-ID
                        detailStmt.setInt(2, item.getProduct().getProductId()); // Sätter produkt-ID
                        detailStmt.setInt(3, item.getQuantity()); // Sätter antal
                        detailStmt.setDouble(4, item.getUnitPrice()); // Sätter enhetspriset
                        detailStmt.setDouble(5, item.getVatRate()); // Sätter momssatsen
                        detailStmt.executeUpdate(); // Kör SQL-frågan
                    }
                }
                conn.commit(); // Bekräftar transaktionen
            } catch (SQLException e) {
                conn.rollback(); // Återställer databasen om något går fel
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Skriver ut felmeddelandet
        }
    }
}
