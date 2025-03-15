package se.systementor;

import se.systementor.db.ProductRepository;
import se.systementor.models.OrderItem;
import se.systementor.models.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CashRegisterGUI extends JFrame {
    private JTextArea receiptArea; // Textområde för att visa kvittot
    private JTextField quantityField; // Textfält där användaren anger antal produkter
    private JPanel productsPanel; // Panel för att visa produktknappar
    private List<Product> products; // Lista över alla produkter
    private List<OrderItem> currentOrder; // Lista över produkter i nuvarande order
    private ProductRepository repository; // Objekt för att hämta och spara produkter
    private int nextReceiptNumber = 1; // Nästa kvittonummer

    public CashRegisterGUI() {
        repository = new ProductRepository(); // Hämtar produktdata från databasen
        currentOrder = new ArrayList<>(); // Skapar en tom orderlista

        setTitle("Kassa System"); // Sätter fönstrets titel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Stänger programmet vid fönsterstängning
        setLayout(new BorderLayout()); // Använder BorderLayout för placering av komponenter

        // Skapar gränssnittets olika delar
        createProductsPanel();
        createReceiptPanel();
        createControlPanel();

        pack(); // Anpassar fönstrets storlek efter innehållet
        setLocationRelativeTo(null); // Centrerar fönstret på skärmen
    }

    // Skapar panelen som visar produktknapparna
    private void createProductsPanel() {
        productsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Layout för produktknappar
        productsPanel.setBackground(Color.GREEN); // Sätter bakgrundsfärg
        products = repository.getAllProducts(); // Hämtar alla produkter från databasen

        // Skapar en knapp för varje produkt
        for (Product product : products) {
            JButton btn = new JButton(product.getName()); // Skapar knapp med produktens namn
            btn.setBackground(Color.WHITE); // Sätter vit bakgrund på knappen
            btn.setPreferredSize(new Dimension(100, 30)); // Bestämmer knappens storlek
            btn.addActionListener(e -> handleProductClick(product)); // Kopplar knapptryck till metod
            productsPanel.add(btn); // Lägger till knappen i panelen
        }

        // Skapar en behållare som håller produkterna längst upp
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(Color.GREEN);
        topContainer.add(productsPanel, BorderLayout.NORTH);

        add(topContainer, BorderLayout.CENTER); // Lägger till produkterna i fönstret
    }

    // Skapar området där kvittot visas
    private void createReceiptPanel() {
        receiptArea = new JTextArea(20, 30); // Skapar en textyta för kvittot
        receiptArea.setEditable(false); // Gör kvittot skrivskyddat
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Sätter typsnitt
        initializeReceipt(); // Initierar kvittot

        JScrollPane scrollPane = new JScrollPane(receiptArea); // Skapar en scrollbar för kvittot
        scrollPane.setPreferredSize(new Dimension(300, 400)); // Bestämmer storlek
        add(scrollPane, BorderLayout.EAST); // Lägger kvittot till höger i fönstret
    }

    // Skapar kontrollpanelen med knappar och textfält
    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Layout för kontroller
        controlPanel.setBackground(Color.BLACK); // Sätter bakgrundsfärg

        quantityField = new JTextField(20); // Skapar textfält för antal
        JButton addButton = new JButton("Add"); // Skapar "Lägg till"-knapp
        JButton payButton = new JButton("Pay"); // Skapar "Betala"-knapp

        addButton.addActionListener(e -> handleAdd()); // Kopplar klick till metod
        payButton.addActionListener(e -> handlePay()); // Kopplar klick till metod

        // Lägger till komponenter i panelen
        controlPanel.add(new JLabel("Antal:") {{ setForeground(Color.WHITE); }});
        controlPanel.add(quantityField);
        controlPanel.add(addButton);
        controlPanel.add(payButton);

        add(controlPanel, BorderLayout.SOUTH); // Lägger panelen längst ner i fönstret
    }

    // Hanterar klick på en produktknapp
    private void handleProductClick(Product product) {
        quantityField.setText("1"); // Förifyller fältet med 1
        updateReceipt(); // Uppdaterar kvittot
    }

    // Hanterar när användaren klickar på "Add"
    private void handleAdd() {
        try {
            int quantity = Integer.parseInt(quantityField.getText()); // Hämtar antal
            if (quantity <= 0) throw new NumberFormatException(); // Kontrollerar giltigt antal

            Product selectedProduct = products.get(0); // Ska vara den senast valda produkten
            OrderItem item = new OrderItem(selectedProduct, quantity); // Skapar en orderpost
            currentOrder.add(item); // Lägger till produkten i ordern
            updateReceipt(); // Uppdaterar kvittot
            quantityField.setText(""); // Tömmer fältet efter tillägg
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hanterar betalning och avslutning av ordern
    private void handlePay() {
        if (currentOrder.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items in the order!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Beräknar totalbelopp och moms
        double total = currentOrder.stream().mapToDouble(OrderItem::getTotal).sum();
        double totalVat = currentOrder.stream().mapToDouble(OrderItem::getVatAmount).sum();

        // Genererar kvittonummer och sparar ordern i databasen
        String receiptNumber = generateReceiptNumber();
        repository.saveOrder(currentOrder, receiptNumber, total, totalVat);

        JOptionPane.showMessageDialog(this, "TACK FÖR DITT KÖP!", "Order Complete", JOptionPane.INFORMATION_MESSAGE);

        currentOrder.clear(); // Rensar orderlistan
        initializeReceipt(); // Skapar ett nytt tomt kvitto
        nextReceiptNumber++; // Ökar kvittonumret
    }

    // Skapar ett nytt tomt kvitto
    private void initializeReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("KVITTO #").append(generateReceiptNumber()).append("\n");
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("-".repeat(40)).append("\n");
        receiptArea.setText(sb.toString()); // Sätter texten i kvittot
    }

    // Uppdaterar kvittot när produkter läggs till
    private void updateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("KVITTO #").append(generateReceiptNumber()).append("\n");
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("-".repeat(40)).append("\n");

        double total = 0;
        double totalVat = 0;

        for (OrderItem item : currentOrder) {
            sb.append(item.toString()).append("\n");
            total += item.getTotal();
            totalVat += item.getVatAmount();
        }

        sb.append("-".repeat(40)).append("\n");
        sb.append(String.format("Total: %.2f kr\n", total));
        sb.append(String.format("Varav moms: %.2f kr\n", totalVat));

        receiptArea.setText(sb.toString());
    }

    // Skapar ett unikt kvittonummer
    private String generateReceiptNumber() {
        return String.format("%d-%s", nextReceiptNumber,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CashRegisterGUI gui = new CashRegisterGUI();
            gui.setVisible(true); // Visar fönstret
        });
    }
}
