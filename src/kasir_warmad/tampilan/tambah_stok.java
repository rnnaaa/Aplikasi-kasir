/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package kasir_warmad.tampilan;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import kasir_warmad.Kelola_barang;
import kasir_warmad.Koneksi;
import kasir_warmad.PilihBarang;

public class tambah_stok extends javax.swing.JDialog {

    HashMap<String, String> barangMap = new HashMap<>();
    private final String awalanNoTelepon = "(+62)";  // Variabel awalan
    private Kelola_barang Kelola_barang;

    /**
     * Creates new form tambah_stok
     */
    public tambah_stok(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.Kelola_barang = Kelola_barang;
        pilihBarang();
        KontaksupplierPK.setText(awalanNoTelepon);  // Pasang awalan otomatis

        KontaksupplierPK.getDocument().addDocumentListener(new DocumentListener() {
            private void formatInput() {
                SwingUtilities.invokeLater(() -> {
                    String text = KontaksupplierPK.getText();

                    // Kalau kosong, kasih default awal
                    if (text.isEmpty()) {
                        KontaksupplierPK.setText("(+62) ");
                        KontaksupplierPK.setCaretPosition(KontaksupplierPK.getText().length());
                        return;
                    }

                    // Pastikan tanda kurung dan plus ada di awal, kalau gak ada, tambahkan
                    if (!text.startsWith("(+")) {
                        text = "(+" + text;
                    }
                    if (!text.contains(")")) {
                        text = text + ")";
                    }

                    // Cari posisi tanda tutup kurung ')'
                    int closeParenIndex = text.indexOf(')');
                    if (closeParenIndex < 3) { // Minimal harus ada 3 char di dalam kurung
                        // tambahkan 62 default kalau kurang dari 3 char di dalam kurung
                        text = "(+62) ";
                        KontaksupplierPK.setText(text);
                        KontaksupplierPK.setCaretPosition(text.length());
                        return;
                    }

                    // Ambil kode negara di dalam tanda kurung
                    String kodeNegara = text.substring(2, closeParenIndex);

                    // Ambil nomor setelah spasi setelah tanda kurung
                    String nomor = "";
                    if (text.length() > closeParenIndex + 2) {
                        nomor = text.substring(closeParenIndex + 2);
                        // Hapus karakter yang bukan digit
                        nomor = nomor.replaceAll("[^\\d]", "");
                    }

                    // Format ulang
                    String formatted = "(+" + kodeNegara + ") " + nomor;

                    // Jika beda dengan text sekarang, update
                    if (!formatted.equals(text)) {
                        int caretPos = KontaksupplierPK.getCaretPosition();
                        KontaksupplierPK.setText(formatted);
                        if (caretPos > formatted.length()) {
                            caretPos = formatted.length();
                        }
                        KontaksupplierPK.setCaretPosition(caretPos);
                    }
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
        });
    }

    private void kosongkan() {
        // Reset semua input setelah simpan berhasil
        PilihbarangPK.setSelectedIndex(-1); // Tidak ada item yang dipilih
        StokPK.setText(""); // Kosongkan input stok
        TglkadaluarsaPK.setDate(null); // Kosongkan tanggal

    }

    private void pilihBarang() {
        PilihbarangPK.removeAllItems();
        barangMap.clear();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/kasir_warmad", "root", ""); PreparedStatement pst = conn.prepareStatement("SELECT id_barang_jual, nama_barang FROM barang_jual"); ResultSet rs = pst.executeQuery()) {

            PilihbarangPK.removeAllItems();  // Kosongkan dulu biar ga dobel
            while (rs.next()) {
                String idBarang = rs.getString("id_barang_jual");
                String namaBarang = rs.getString("nama_barang");
                PilihbarangPK.addItem(new PilihBarang(idBarang, namaBarang));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal memuat data barang: " + ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        PilihbarangPK = new javax.swing.JComboBox<>();
        StokPK = new javax.swing.JTextField();
        BatalPK = new javax.swing.JButton();
        SimpanPK = new javax.swing.JButton();
        TglkadaluarsaPK = new com.toedter.calendar.JDateChooser();
        TglinputPK = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        HargajualPK = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        HargabeliPK = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        NamasupplierPK = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        AlamatsupplierPK = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        KontaksupplierPK = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(600, 444));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel1.setText("Tambah Stok");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 19, -1, -1));

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel2.setText("Pilih Barang");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 65, -1, -1));

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel3.setText(" Stok Barang");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 149, -1, -1));

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel4.setText("Tanggal Kadaluarsa");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 225, -1, -1));

        jPanel1.add(PilihbarangPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 101, 505, -1));

        StokPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                StokPKKeyTyped(evt);
            }
        });
        jPanel1.add(StokPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 185, 505, -1));

        BatalPK.setText("Batal");
        BatalPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BatalPKActionPerformed(evt);
            }
        });
        jPanel1.add(BatalPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(434, 754, -1, -1));

        SimpanPK.setText("Simpan");
        SimpanPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SimpanPKActionPerformed(evt);
            }
        });
        jPanel1.add(SimpanPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(333, 754, -1, -1));
        jPanel1.add(TglkadaluarsaPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 261, 505, -1));
        jPanel1.add(TglinputPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 337, 505, -1));

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel5.setText("Tanggal Input");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 301, -1, -1));

        HargajualPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                HargajualPKKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                HargajualPKKeyTyped(evt);
            }
        });
        jPanel1.add(HargajualPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 411, 71, -1));

        jLabel6.setText("Harga Jual");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 377, 71, -1));

        HargabeliPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                HargabeliPKKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                HargabeliPKKeyTyped(evt);
            }
        });
        jPanel1.add(HargabeliPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 485, 71, -1));

        jLabel7.setText("Harga Beli");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 451, 79, -1));
        jPanel1.add(NamasupplierPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 559, 71, -1));

        jLabel8.setText("Nama Supplier");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 525, -1, -1));
        jPanel1.add(AlamatsupplierPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 633, 71, -1));

        jLabel9.setText("Alamat Supplier");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 599, -1, -1));

        KontaksupplierPK.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                KontaksupplierPKFocusGained(evt);
            }
        });
        KontaksupplierPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KontaksupplierPKKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                KontaksupplierPKKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                KontaksupplierPKKeyTyped(evt);
            }
        });
        jPanel1.add(KontaksupplierPK, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 707, 71, -1));

        jLabel10.setText("Kontak Supplier");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 673, -1, -1));

        jScrollPane1.setViewportView(jPanel1);

        getContentPane().add(jScrollPane1);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void SimpanPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SimpanPKActionPerformed
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

// 🟢 Ambil data dari form input
        PilihBarang selectedBarang = (PilihBarang) PilihbarangPK.getSelectedItem();
        String idBarang = selectedBarang.getIdBarang();
        String stok = StokPK.getText();
        String tglKadaluarsa = sdf.format(TglkadaluarsaPK.getDate());
        String tglInput = sdf.format(TglinputPK.getDate());
        String hargaJual = HargajualPK.getText();
        String hargaBeli = HargabeliPK.getText();
        String namaSupplier = NamasupplierPK.getText();
        String alamatSupplier = AlamatsupplierPK.getText();
        String kontakSupplier = KontaksupplierPK.getText();

// 🧹 Bersihkan angka dari simbol dan huruf
        String hargaJualBersih = hargaJual.replaceAll("[^\\d]", "");
        String hargaBeliBersih = hargaBeli.replaceAll("[^\\d]", "");
        String stokBersih = stok.replaceAll("[^\\d]", "");

        try {
            Connection conn = Koneksi.getKoneksi();

            // 🔑 Generate ID Manual
            String idSupplierStr = "";
            String idStok = "";
            try {
                idSupplierStr = generateIdSupplier(conn);
                idStok = generateIdStok(conn);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Gagal generate ID: " + ex.getMessage());
                return;
            }

            // 🟢 Simpan ke tabel supplier_barang
            String insertSupplier = "INSERT INTO supplier_barang (id_supplier_barang, nama_supplier, alamat_supplier, kontak_supplier) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psSupplier = conn.prepareStatement(insertSupplier)) {
                psSupplier.setString(1, idSupplierStr);
                psSupplier.setString(2, namaSupplier);
                psSupplier.setString(3, alamatSupplier);
                psSupplier.setString(4, kontakSupplier);
                psSupplier.executeUpdate();
            }

            // 🟢 Simpan ke tabel stok_gudang
            String insertStok = "INSERT INTO stok_gudang (id_stok_gudang, id_barang_jual, id_supplier_barang, harga_jual, harga_beli, jumlah_stok, tanggal_kadaluarsa, tanggal_input) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psStok = conn.prepareStatement(insertStok)) {
                psStok.setString(1, idStok);
                psStok.setString(2, idBarang);
                psStok.setString(3, idSupplierStr);
                psStok.setString(4, hargaJualBersih);
                psStok.setString(5, hargaBeliBersih);
                psStok.setString(6, stokBersih);
                psStok.setString(7, tglKadaluarsa);
                psStok.setString(8, tglInput);
                psStok.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "Data stok berhasil disimpan!");
            Kelola_barang.tampilkanBarangKeTabelK();
            // 🧼 Reset form
            PilihbarangPK.setSelectedIndex(0);
            StokPK.setText("");
            TglkadaluarsaPK.setDate(null);
            TglinputPK.setDate(null);
            HargajualPK.setText("");
            HargabeliPK.setText("");
            NamasupplierPK.setText("");
            AlamatsupplierPK.setText("");
            KontaksupplierPK.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data: " + e.getMessage());
        }


    }//GEN-LAST:event_SimpanPKActionPerformed

    private void StokPKKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StokPKKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // Mengabaikan input selain angka
        }
    }//GEN-LAST:event_StokPKKeyTyped

    private void HargajualPKKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HargajualPKKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // Mengabaikan input selain angka
        }
    }//GEN-LAST:event_HargajualPKKeyTyped

    private void HargabeliPKKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HargabeliPKKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // Mengabaikan input selain angka
        }
    }//GEN-LAST:event_HargabeliPKKeyTyped

    private void HargabeliPKKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HargabeliPKKeyReleased
        String input = HargabeliPK.getText().replaceAll("[^\\d]", ""); // Hapus karakter non-digit
        if (!input.isEmpty()) {
            HargabeliPK.setText("Rp " + input);
        } else {
            HargabeliPK.setText("");
        }
    }//GEN-LAST:event_HargabeliPKKeyReleased

    private void HargajualPKKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HargajualPKKeyReleased
        String input = HargajualPK.getText().replaceAll("[^\\d]", ""); // Hapus karakter non-digit
        if (!input.isEmpty()) {
            HargajualPK.setText("Rp " + input);
        } else {
            HargajualPK.setText("");
        }
    }//GEN-LAST:event_HargajualPKKeyReleased

    private void KontaksupplierPKKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KontaksupplierPKKeyReleased


    }//GEN-LAST:event_KontaksupplierPKKeyReleased

    private void KontaksupplierPKKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KontaksupplierPKKeyTyped

    }//GEN-LAST:event_KontaksupplierPKKeyTyped

    private void KontaksupplierPKFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_KontaksupplierPKFocusGained

    }//GEN-LAST:event_KontaksupplierPKFocusGained

    private void KontaksupplierPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KontaksupplierPKKeyPressed

    }//GEN-LAST:event_KontaksupplierPKKeyPressed

    private void BatalPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BatalPKActionPerformed
        this.dispose();

        // Buka kembali tampilan Kelola Barang
        Kelola_barang kelola = new Kelola_barang(); // asumsi kamu punya JFrame bernama KelolaBarang
        kelola.setVisible(true);
    }//GEN-LAST:event_BatalPKActionPerformed
    private String generateIdSupplier(Connection conn) throws SQLException {
        String prefix = "SPLRBRNG25";
        String query = "SELECT id_supplier_barang FROM supplier_barang ORDER BY id_supplier_barang DESC LIMIT 1";
        try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("id_supplier_barang");
                int number = Integer.parseInt(lastId.substring(10));
                return prefix + String.format("%03d", number + 1);
            }
        }
        return prefix + "001";
    }

    private String generateIdStok(Connection conn) throws SQLException {
        String prefix = "STKBRNG25";
        String query = "SELECT id_stok_gudang FROM stok_gudang ORDER BY id_stok_gudang DESC LIMIT 1";
        try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("id_stok_gudang");
                int number = Integer.parseInt(lastId.substring(9));
                return prefix + String.format("%03d", number + 1);
            }
        }
        return prefix + "001";
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(tambah_barang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(tambah_barang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(tambah_barang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(tambah_barang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                tambah_stok dialog = new tambah_stok(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AlamatsupplierPK;
    private javax.swing.JButton BatalPK;
    private javax.swing.JTextField HargabeliPK;
    private javax.swing.JTextField HargajualPK;
    private javax.swing.JTextField KontaksupplierPK;
    private javax.swing.JTextField NamasupplierPK;
    private javax.swing.JComboBox<PilihBarang> PilihbarangPK;
    public javax.swing.JButton SimpanPK;
    private javax.swing.JTextField StokPK;
    private com.toedter.calendar.JDateChooser TglinputPK;
    private com.toedter.calendar.JDateChooser TglkadaluarsaPK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
