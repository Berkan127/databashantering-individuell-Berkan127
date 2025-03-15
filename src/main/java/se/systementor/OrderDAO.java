package se.systementor;

import se.systementor.db.DatabaseConnection; // Importera rätt anslutningsklass
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderDAO {

    // Metod för att spara en order i databasen
    public void saveOrder(List<Product> products) {
        String sqlOrder = "INSERT INTO orders (receipt_number, order_date, total_amount, total_vat) VALUES (?, ?, ?, ?)";
        String sqlOrderDetails = "INSERT INTO orderdetails (OrderId, ProductId, Quantity, UnitPrice, VatRate) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) { // Använd DatabaseConnection här
            conn.setAutoCommit(false);
            try {
                Map<Product, Long> productQuantities = products.stream()
                        .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

                double totalAmount = products.stream().mapToDouble(Product::getPrice).sum();
                double totalVat = products.stream()
                        .mapToDouble(p -> p.getPrice() * p.getVatRate() / 100.0)
                        .sum();

                PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
                String receiptNumber = generateReceiptNumber();
                stmtOrder.setString(1, receiptNumber);
                stmtOrder.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmtOrder.setDouble(3, totalAmount);
                stmtOrder.setDouble(4, totalVat);
                stmtOrder.executeUpdate();

                ResultSet rs = stmtOrder.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);

                    PreparedStatement stmtDetails = conn.prepareStatement(sqlOrderDetails);
                    for (Map.Entry<Product, Long> entry : productQuantities.entrySet()) {
                        Product product = entry.getKey();
                        long quantity = entry.getValue();

                        stmtDetails.setInt(1, orderId);
                        stmtDetails.setInt(2, product.getId());
                        stmtDetails.setInt(3, (int) quantity);
                        stmtDetails.setDouble(4, product.getPrice());
                        stmtDetails.setDouble(5, product.getVatRate());
                        stmtDetails.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateReceiptNumber() {
        return String.format("%d-%s",
                System.currentTimeMillis(),
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
        );
    }
}
