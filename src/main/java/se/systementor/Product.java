package se.systementor;

// Klassen representerar en produkt i systemet
public class Product {
    private int id;          // Produktens unika ID
    private String name;     // Produktens namn
    private double price;    // Produktens pris (exklusive moms)
    private double vatRate;  // Momssatsen för produkten
    private int categoryId;  // ID för produktens kategori

    // Konstruktor för att skapa ett nytt produktobjekt
    public Product(int id, String name, double price, double vatRate, int categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.vatRate = vatRate;
        this.categoryId = categoryId;
    }

    // Getter-metoder för att hämta värdena på de privata variablerna
    public int getId() { return id; }               // Hämtar produktens ID
    public String getName() { return name; }        // Hämtar produktens namn
    public double getPrice() { return price; }      // Hämtar produktens pris
    public double getVatRate() { return vatRate; }  // Hämtar produktens momssats
    public int getCategoryId() { return categoryId; } // Hämtar produktens kategori-ID

    // Metod som omvandlar produktens information till en textsträng (t.ex. för att visas i gränssnittet)
    @Override
    public String toString() {
        return String.format("%s - %.2f kr", name, price); // Returnerar produktens namn och pris i rätt format
    }
}
