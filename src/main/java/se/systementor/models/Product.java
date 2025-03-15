package se.systementor.models;

public class Product {
    private int productId;
    private String name;
    private double price;
    private double vatRate;

    public Product(int productId, String name, double price, double vatRate) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.vatRate = vatRate;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getVatRate() { return vatRate; }
    public void setVatRate(double vatRate) { this.vatRate = vatRate; }

    @Override
    public String toString() {
        return String.format("%s - %.1f kr", name, price);
    }
}
