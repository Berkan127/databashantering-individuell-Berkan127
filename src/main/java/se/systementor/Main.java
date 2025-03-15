package se.systementor;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CashRegisterForm form = new CashRegisterForm();
            form.setVisible(true);
        });
    }
}