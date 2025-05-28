package kasir_warmad;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.*;

public class TeleponInputFormat extends JFrame {
    private JTextField KontaksupplierPK;

    public TeleponInputFormat() {
        super("Input Nomor Telepon dengan Format");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 120);
        setLayout(new FlowLayout());

        KontaksupplierPK = new JTextField("(+62) ", 20);
        KontaksupplierPK.setToolTipText("Format: (+kode) nomor, contoh: (+62) 811");

        // Tambah DocumentListener untuk format otomatis
        KontaksupplierPK.getDocument().addDocumentListener(new FormatListener());

        
        add(KontaksupplierPK);
        setVisible(true);
    }

    // FormatListener untuk format otomatis
    private class FormatListener implements DocumentListener {
        private void formatInput() {
            SwingUtilities.invokeLater(() -> {
                String input = KontaksupplierPK.getText();

                // Tetap awalan "(+62) " jika kosong atau hanya awalan
                if (input.isEmpty() || input.equals("(+62) ")) {
                    KontaksupplierPK.setText("(+62) ");
                    KontaksupplierPK.setCaretPosition(KontaksupplierPK.getText().length());
                    return;
                }

                // Hapus karakter non-digit
                String nomor = input.replaceAll("[^\\d]", "");

                // Buat awalan
                String formatted = "(+62) ";

                // Tambahkan nomor setelah awalan
                if (nomor.startsWith("62")) {
                    nomor = nomor.substring(2); // Hapus 62 tambahan kalau ada
                }
                formatted += nomor;

                // Simpan posisi kursor
                int caretPosition = KontaksupplierPK.getCaretPosition();
                KontaksupplierPK.setText(formatted);
                if (caretPosition < 6) caretPosition = 6; // posisi setelah "(+62) "
                if (caretPosition > formatted.length()) caretPosition = formatted.length();
                KontaksupplierPK.setCaretPosition(caretPosition);
            });
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            formatInput();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            formatInput();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            formatInput();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TeleponInputFormat::new);
    }
}
