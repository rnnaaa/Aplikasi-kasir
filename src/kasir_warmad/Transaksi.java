/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasir_warmad;

import desain.GradientPanel;
import com.mysql.cj.xdevapi.Statement;
import javax.swing.*;                     // Semua komponen GUI (JFrame, JButton, JTextField, dll)
import javax.swing.table.DefaultTableModel; // Untuk manipulasi tabel (add row, hapus row, dll)
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import java.awt.Font;
import javax.swing.UIManager;

/**
 *
 * @author ThinkPad
 */
public class Transaksi extends javax.swing.JFrame {

    /**
     * Creates new form Laporan
     */
    public Transaksi() {
        initComponents();

        PotonganhargaT.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                hitungKeseluruhanDanKembalian();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                hitungKeseluruhanDanKembalian();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                hitungKeseluruhanDanKembalian();
            }
        });

        PembayaranT.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                hitungKeseluruhanDanKembalian();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                hitungKeseluruhanDanKembalian();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                hitungKeseluruhanDanKembalian();
            }
        });

    }

    private void hitungKeseluruhanDanKembalian() {
        try {
            int totalSemua = Integer.parseInt(TotalsemuaT.getText());
            int potongan = 0;
            int pembayaran = 0;
            boolean pembayaranValid = false;

            if (!PotonganhargaT.getText().isEmpty()) {
                potongan = Integer.parseInt(PotonganhargaT.getText());
            }

            if (!PembayaranT.getText().isEmpty()) {
                pembayaran = Integer.parseInt(PembayaranT.getText());
                pembayaranValid = true;
            }

            int keseluruhan = totalSemua - potongan;
            KeseluruhanT.setText(String.valueOf(keseluruhan));

            // Hitung kembalian hanya jika pembayaran valid
            if (pembayaranValid) {
                int kembalian = pembayaran - keseluruhan;
                KembalianT.setText(String.valueOf(kembalian));
            } else {
                KembalianT.setText(""); // kosongkan jika belum bayar
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Pastikan semua inputan angka valid!");
        }
    }

    private void ambilDataBarangDariBarcode() {
        String barcode = kodeBarangTxt.getText();  // Ambil barcode dari TextField

        if (!barcode.isEmpty()) {
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/kasir_warmad", "root", "");
                String sql = "SELECT nama_barang, harga_jual FROM barang_jual WHERE barcode_barang = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, barcode);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String namaBarang = rs.getString("nama_barang");
                    double hargaBarang = rs.getDouble("harga_jual");

                    namaBarangTxt.setText(namaBarang);
                    HargaT.setText(String.valueOf(hargaBarang));
                } else {
                    JOptionPane.showMessageDialog(null, "Barcode tidak ditemukan di data barang!");
                    namaBarangTxt.setText("");
                    HargaT.setText("");
                }

                rs.close();
                pst.close();
                con.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mengambil data.");
            }
        }
    }

    private void bayar() {
        Connection conn = null;
        try {
            conn = Koneksi.getKoneksi();
            conn.setAutoCommit(false); // Mulai transaksi manual

            int totalHarga = Integer.parseInt(TotalsemuaT.getText());
            int diskon = PotonganhargaT.getText().isEmpty() ? 0 : Integer.parseInt(PotonganhargaT.getText());
            int pembayaran = Integer.parseInt(PembayaranT.getText());
            int kembalian = Integer.parseInt(KembalianT.getText());

            // Langkah 1: Cek stok untuk semua barang
            for (int i = 0; i < TabelT.getRowCount(); i++) {
                String barcode = TabelT.getValueAt(i, 0).toString();
                int jumlahBeli = Integer.parseInt(TabelT.getValueAt(i, 3).toString());

                String sqlCek = "SELECT sg.jumlah_stok FROM stok_gudang sg JOIN barang_jual bj ON sg.id_barang_jual = bj.id_barang_jual WHERE bj.barcode_barang = ?";
                PreparedStatement pstCek = conn.prepareStatement(sqlCek);
                pstCek.setString(1, barcode);
                ResultSet rsCek = pstCek.executeQuery();

                if (rsCek.next()) {
                    int stokTersedia = rsCek.getInt("jumlah_stok");
                    String namaBarang = TabelT.getValueAt(i, 1).toString(); // kolom 1 = Nama Barang

                    if (stokTersedia < jumlahBeli) {
                        throw new Exception("Stok tidak cukup untuk \"" + namaBarang + "\". Stok tersedia: " + stokTersedia + ", diminta: " + jumlahBeli);
                    }
                }

            }

            // Langkah 2: Simpan transaksi utama
            String insertTransaksi = "INSERT INTO transaksi_kasir (id_pengguna_aplikasi, total_harga, diskon, pembayaran, kembalian, metode_pembayaran, tanggal) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertTransaksi, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, 1); // Misalnya: user id dari sesi
            stmt.setInt(2, totalHarga);
            stmt.setInt(3, diskon);
            stmt.setInt(4, pembayaran);
            stmt.setInt(5, kembalian);
            stmt.setString(6, metodeBayarBox.getSelectedItem().toString());
            stmt.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            int idTransaksi = 0;
            if (rs.next()) {
                idTransaksi = rs.getInt(1);
            }

            // Langkah 3: Simpan detail transaksi dan kurangi stok
            for (int i = 0; i < TabelT.getRowCount(); i++) {
                String barcode = TabelT.getValueAt(i, 0).toString();
                int jumlah = Integer.parseInt(TabelT.getValueAt(i, 3).toString());
                int harga = Integer.parseInt(TabelT.getValueAt(i, 2).toString());
                int subtotal = Integer.parseInt(TabelT.getValueAt(i, 4).toString());

                // Ambil ID barang
                String getIdBarang = "SELECT id_barang_jual FROM barang_jual WHERE barcode_barang = ?";
                PreparedStatement pstId = conn.prepareStatement(getIdBarang);
                pstId.setString(1, barcode);
                ResultSet rsId = pstId.executeQuery();

                if (rsId.next()) {
                    int idBarangJual = rsId.getInt("id_barang_jual");

                    // Simpan detail transaksi
                    String insertDetail = "INSERT INTO detail_transaksi_pelanggan (id_transaksi_kasir, id_barang_jual, jumlah, harga_satuan, subtotal, diskon) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstDetail = conn.prepareStatement(insertDetail);
                    pstDetail.setInt(1, idTransaksi);
                    pstDetail.setInt(2, idBarangJual);
                    pstDetail.setInt(3, jumlah);
                    pstDetail.setInt(4, harga);
                    pstDetail.setInt(5, subtotal);
                    pstDetail.setInt(6, 0); // diskon per item
                    pstDetail.executeUpdate();

                    // Update stok (kurangi)
                    String updateStok = "UPDATE stok_gudang SET jumlah_stok = jumlah_stok - ? WHERE id_barang_jual = ?";
                    PreparedStatement pstStok = conn.prepareStatement(updateStok);
                    pstStok.setInt(1, jumlah);
                    pstStok.setInt(2, idBarangJual);
                    pstStok.executeUpdate();
                } else {
                    throw new Exception("ID Barang tidak ditemukan untuk barcode: " + barcode);
                }
            }

            conn.commit(); // Simpan semua perubahan
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!");

            // Cetak struk jika diinginkan
            int cetak = JOptionPane.showConfirmDialog(null, "Cetak struk sekarang?", "Cetak Struk", JOptionPane.YES_NO_OPTION);
            if (cetak == JOptionPane.YES_OPTION) {
                struk.nota n = new struk.nota();
                n.nota(idTransaksi);
            }

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Batalkan jika ada kesalahan
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal rollback: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Kembalikan ke auto
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
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

        jPanel2 = new GradientPanel();
        jLabel5 = new javax.swing.JLabel();
        PembayaranT = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        KeseluruhanT = new javax.swing.JTextField();
        JumlahT = new javax.swing.JTextField();
        bayarBtn = new javax.swing.JButton();
        HargaT = new javax.swing.JTextField();
        PotonganhargaT = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        KembalianT = new javax.swing.JTextField();
        TambahkanT = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        TotalsemuaT = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabelT = new javax.swing.JTable();
        HapusT = new javax.swing.JButton();
        namaBarangTxt = new javax.swing.JTextField();
        metodeBayarBox = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        kodeBarangTxt = new javax.swing.JTextField();
        panel1 = new java.awt.Panel();
        DashboardT = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(800, 600));
        jPanel2.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Total Semua");

        PembayaranT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PembayaranTActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Keseluruhan");

        jLabel3.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nama Barang");

        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Potongan Harga");

        jLabel2.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Harga Barang");

        KeseluruhanT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KeseluruhanTActionPerformed(evt);
            }
        });

        JumlahT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JumlahTActionPerformed(evt);
            }
        });

        bayarBtn.setText("BAYAR");
        bayarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bayarBtnActionPerformed(evt);
            }
        });

        HargaT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HargaTActionPerformed(evt);
            }
        });

        PotonganhargaT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PotonganhargaTActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Kembalian");

        jLabel7.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Pembayaran");

        KembalianT.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                KembalianTFocusLost(evt);
            }
        });
        KembalianT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KembalianTActionPerformed(evt);
            }
        });

        TambahkanT.setFont(new java.awt.Font("Segoe UI Variable", 1, 12)); // NOI18N
        TambahkanT.setText("TAMBAHKAN");
        TambahkanT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TambahkanTActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Kode Barang");

        jLabel4.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Jumlah barang");

        TotalsemuaT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TotalsemuaTActionPerformed(evt);
            }
        });

        TabelT.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        TabelT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama", "Harga", "Jumlah", "Total"
            }
        ));
        jScrollPane1.setViewportView(TabelT);

        HapusT.setFont(new java.awt.Font("Segoe UI Variable", 1, 12)); // NOI18N
        HapusT.setText("HAPUS");
        HapusT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HapusTActionPerformed(evt);
            }
        });

        metodeBayarBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "Qris" }));
        metodeBayarBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metodeBayarBoxActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Metode Pembayaran");

        kodeBarangTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                kodeBarangTxtFocusLost(evt);
            }
        });
        kodeBarangTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kodeBarangTxtActionPerformed(evt);
            }
        });

        panel1.setForeground(new java.awt.Color(255, 255, 255));

        DashboardT.setFont(new java.awt.Font("Segoe UI Variable", 1, 12)); // NOI18N
        DashboardT.setText("Dashboard");
        DashboardT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DashboardTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(DashboardT, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DashboardT)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kodeBarangTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HargaT, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jLabel3)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(namaBarangTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(141, 141, 141)
                        .addComponent(TambahkanT, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HapusT, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(JumlahT, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(panel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TotalsemuaT, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PotonganhargaT, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(76, 76, 76)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel8)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(KeseluruhanT, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(41, 41, 41)
                            .addComponent(jLabel10)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PembayaranT, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(metodeBayarBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(KembalianT, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                        .addComponent(bayarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(97, 97, 97))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(namaBarangTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(kodeBarangTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TambahkanT, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(HapusT, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(HargaT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(JumlahT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(bayarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(TotalsemuaT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(KeseluruhanT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(metodeBayarBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(PotonganhargaT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(PembayaranT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(KembalianT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(150, 150, 150))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void DashboardTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DashboardTActionPerformed
        String role = Session.getCurrentUserRole();
Dashboard da = new Dashboard(role);
da.setVisible(true);
da.pack();
da.setLocationRelativeTo(null);
dispose();

    }//GEN-LAST:event_DashboardTActionPerformed

    private void kodeBarangTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kodeBarangTxtActionPerformed
        // TODO add your handling code here:
        ambilDataBarangDariBarcode();
    }//GEN-LAST:event_kodeBarangTxtActionPerformed

    private void kodeBarangTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kodeBarangTxtFocusLost
        // TODO add your handling code here:
        String barcode = kodeBarangTxt.getText();  // Ambil barcode dari TextField

        if (!barcode.isEmpty()) {
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/kasir_warmad", "root", "");
                String sql = "SELECT nama_barang, harga_jual FROM barang_jual WHERE barcode_barang = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, barcode);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String namaBarang = rs.getString("nama_barang");
                    int hargaBarang = rs.getInt("harga_jual");

                    namaBarangTxt.setText(namaBarang);
                    HargaT.setText(String.valueOf(hargaBarang));
                } else {
                    JOptionPane.showMessageDialog(null, "Kode barang tidak ditemukan!");
                    namaBarangTxt.setText("");
                    HargaT.setText("");
                }

                rs.close();
                pst.close();
                con.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mengambil data.");
            }
        }
    }//GEN-LAST:event_kodeBarangTxtFocusLost

    private void metodeBayarBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_metodeBayarBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_metodeBayarBoxActionPerformed

    private void HapusTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HapusTActionPerformed
        // Kosongkan isi tabel
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus semua data transaksi?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Lakukan penghapusan seperti di atas
        }

        DefaultTableModel model = (DefaultTableModel) TabelT.getModel();
        model.setRowCount(0); // Menghapus semua baris

        // Kosongkan semua field transaksi
        TotalsemuaT.setText("");
        KeseluruhanT.setText("");
        PotonganhargaT.setText("");
        PembayaranT.setText("");
        KembalianT.setText("");

        // Kosongkan input barang jika perlu
        namaBarangTxt.setText("");
        kodeBarangTxt.setText("");
        HargaT.setText("");
        JumlahT.setText("");

        JOptionPane.showMessageDialog(this, "Data transaksi telah dikosongkan.");
    }//GEN-LAST:event_HapusTActionPerformed

    private void TotalsemuaTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TotalsemuaTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TotalsemuaTActionPerformed

    private void TambahkanTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahkanTActionPerformed
        try {
            DefaultTableModel model = (DefaultTableModel) TabelT.getModel();

            String nama = namaBarangTxt.getText();
            String kode = kodeBarangTxt.getText();
            int harga = Integer.parseInt(HargaT.getText());
            int jumlah = Integer.parseInt(JumlahT.getText());
            int total = harga * jumlah;

            // Tambahkan baris ke tabel
            model.addRow(new Object[]{kode, nama, harga, jumlah, total});

            // Ambil total sebelumnya dari field (jika kosong anggap 0)
            int totalSebelumnya = 0;
            try {
                totalSebelumnya = Integer.parseInt(TotalsemuaT.getText());
            } catch (NumberFormatException ex) {
                totalSebelumnya = 0;
            }

            // Tambahkan total baru ke total sebelumnya
            int totalBaru = totalSebelumnya + total;

            // Tampilkan ke field total
            TotalsemuaT.setText(String.valueOf(totalBaru));
            KeseluruhanT.setText(String.valueOf(totalBaru)); // jika belum ada diskon

            // Kosongkan input setelah ditambahkan
            namaBarangTxt.setText("");
            kodeBarangTxt.setText("");
            HargaT.setText("");
            JumlahT.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Masukkan angka yang valid untuk harga dan jumlah!");
        }
    }//GEN-LAST:event_TambahkanTActionPerformed

    private void KembalianTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KembalianTActionPerformed

    }//GEN-LAST:event_KembalianTActionPerformed

    private void KembalianTFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_KembalianTFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_KembalianTFocusLost

    private void PotonganhargaTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PotonganhargaTActionPerformed

    }//GEN-LAST:event_PotonganhargaTActionPerformed

    private void HargaTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HargaTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_HargaTActionPerformed

    private void bayarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bayarBtnActionPerformed
        bayar();
    }//GEN-LAST:event_bayarBtnActionPerformed

    private void KeseluruhanTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KeseluruhanTActionPerformed
        hitungKeseluruhanDanKembalian();
    }//GEN-LAST:event_KeseluruhanTActionPerformed

    private void PembayaranTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PembayaranTActionPerformed
        hitungKeseluruhanDanKembalian();
    }//GEN-LAST:event_PembayaranTActionPerformed

    private void JumlahTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JumlahTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_JumlahTActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(new FlatArcOrangeIJTheme());
            UIManager.put("Button.arc", 999);
            UIManager.put("defaultFont", new Font("Poppins", Font.BOLD, 14));
        } catch (Exception ex) {
            System.err.println("Gagal mengatur tema FlatLaf Arc Orange.");
            ex.printStackTrace();
        }

        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Transaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DashboardT;
    private javax.swing.JButton HapusT;
    private javax.swing.JTextField HargaT;
    private javax.swing.JTextField JumlahT;
    private javax.swing.JTextField KembalianT;
    private javax.swing.JTextField KeseluruhanT;
    private javax.swing.JTextField PembayaranT;
    private javax.swing.JTextField PotonganhargaT;
    private javax.swing.JTable TabelT;
    private javax.swing.JButton TambahkanT;
    private javax.swing.JTextField TotalsemuaT;
    private javax.swing.JButton bayarBtn;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField kodeBarangTxt;
    private javax.swing.JComboBox<String> metodeBayarBox;
    private javax.swing.JTextField namaBarangTxt;
    private java.awt.Panel panel1;
    // End of variables declaration//GEN-END:variables
}
