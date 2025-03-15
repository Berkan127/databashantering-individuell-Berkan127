package se.systementor.models;

public class OrderItem {
    private Product product;
    private int quantity;
    private double unitPrice;
    private double vatRate;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.vatRate = product.getVatRate();
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getVatRate() { return vatRate; }
    public void setVatRate(double vatRate) { this.vatRate = vatRate; }

    public double getTotal() {
        return unitPrice * quantity;
    }

    public double getVatAmount() {
        return getTotal() * (vatRate / 100.0);
    }

    @Override
    public String toString() {
        return String.format("%-20s %3d st  á %6.2f  %8.2f", 
            product.getName(), quantity, unitPrice, getTotal());
    }
}
