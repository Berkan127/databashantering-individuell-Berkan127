package se.systementor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CashRegisterForm extends JFrame {
    // GUI-komponenter
    private JPanel panel1; // Huvudpanelen
    private JPanel panelRight; // Panel för kvitto
    private JPanel panelLeft; // Panel för produktknappar
    private JTextArea receiptArea; // Textområde för kvittot
    private JPanel buttonsPanel; // Panel för produktknappar
    private JTextField textField1; // Textfält för antal produkter
    private JButton addButton; // Knapp för att lägga till produkt
    private JButton payButton; // Knapp för att genomföra betalning

    private Database database = new Database(); // Databasanslutning
    private List<Product> selectedProducts = new ArrayList<>(); // Lista med valda produkter
    private Product lastSelectedProduct; // Senast valda produkt

    public CashRegisterForm() {
        setTitle("Kassa System"); // Fönstertitel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Stänger programmet vid stängning
        setContentPane(panel1); // Använder huvudpanelen

        // Sätter bakgrundsfärg på vänster panel
        panelLeft.setBackground(Color.GREEN);

        // Skapar en panel för produktknappar med grid-layout
        buttonsPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // 2 kolumner, 5 px mellanrum
        buttonsPanel.setBackground(Color.GREEN);

        // Hämtar alla produkter från databasen och skapar knappar för dem
        for (Product product : database.getAllProducts()) {
            JButton button = new JButton(product.getName() + " - " + product.getPrice() + " kr");
            button.setBackground(Color.WHITE);
            button.addActionListener(e -> {
                lastSelectedProduct = product; // Sparar den valda produkten
                textField1.setText("1"); // Förval antal till 1
            });
            buttonsPanel.add(button); // Lägger till knappen i panelen
        }
        panelLeft.add(buttonsPanel, BorderLayout.CENTER);

        // Skapar kvitto-området
        receiptArea = new JTextArea(20, 30); // 20 rader, 30 kolumner
        receiptArea.setEditable(false); // Gör textområdet skrivskyddat
        JScrollPane scrollPane = new JScrollPane(receiptArea); // Lägger till rullningsfunktion
        panelRight.add(scrollPane, BorderLayout.CENTER);

        // Kopplar knapparna till sina metoder
        addButton.addActionListener(e -> addProductToReceipt());
        payButton.addActionListener(e -> processPayment());

        // Anpassar fönstrets storlek och placering
        pack(); // Anpassar storlek baserat på komponenterna
        setLocationRelativeTo(null); // Centrerar fönstret på skärmen
        setSize(800, 600); // Sätter standardstorlek
    }

    // Metod för att lägga till vald produkt till kvittot
    private void addProductToReceipt() {
        if (lastSelectedProduct == null) { // Om ingen produkt är vald
            JOptionPane.showMessageDialog(this, "Välj en produkt först!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(textField1.getText()); // Hämtar antal från textfältet
            if (quantity <= 0) throw new NumberFormatException(); // Kastar fel om antalet är 0 eller negativt

            double totalPrice = lastSelectedProduct.getPrice() * quantity; // Beräknar totalpris

            // Lägger till produktens information i kvittot
            receiptArea.append(String.format("%-20s %3d * %6.2f = %8.2f kr\n",
                    lastSelectedProduct.getName(), quantity, lastSelectedProduct.getPrice(), totalPrice));

            // Lägger till produkten i listan det antal gånger som valts
            for (int i = 0; i < quantity; i++) {
                selectedProducts.add(lastSelectedProduct);
            }
        } catch (NumberFormatException e) { // Fångar fel om inmatningen är ogiltig
            JOptionPane.showMessageDialog(this, "Ange ett giltigt antal!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metod för att genomföra betalning
    private void processPayment() {
        if (selectedProducts.isEmpty()) { // Kollar om det finns produkter i listan
            JOptionPane.showMessageDialog(this, "Inga produkter valda!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double total = selectedProducts.stream().mapToDouble(Product::getPrice).sum(); // Räknar totalpris
        receiptArea.append("\nTotal: " + total + " kr\nTACK FÖR DITT KÖP!\n\n"); // Visar totalpris och tackmeddelande

        OrderDAO orderDAO = new OrderDAO(); // Skapar objekt för att spara order
        orderDAO.saveOrder(selectedProducts); // Sparar ordern i databasen

        // Återställer fält efter betalning
        selectedProducts.clear(); // Rensar produktlistan
        lastSelectedProduct = null; // Nollställer vald produkt
        textField1.setText(""); // Tömmer antal-fältet
    }

    // Huvudmetod som startar programmet
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CashRegisterForm form = new CashRegisterForm(); // Skapar fönstret
            form.setVisible(true); // Visar fönstret
        });
    }
}
