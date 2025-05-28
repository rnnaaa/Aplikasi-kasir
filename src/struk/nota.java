/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package struk;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import java.sql.Connection;
import java.util.*;
import javax.swing.JOptionPane;
import kasir_warmad.Koneksi;
/**
 *
 * @author Ariel
 */
public class nota {

    public void nota(int idTransaksi) {
        try {
            // Path ke file nota.jrxml (disesuaikan jika berada di folder berbeda)
            String sourceFileName = "src/struk/nota.jrxml";

            // Kompilasi jrxml menjadi jasper
            JasperReport jasperReport = JasperCompileManager.compileReport(sourceFileName);

            // Parameter untuk filtering laporan
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("id_transaksi_kasir", idTransaksi); // nama param harus sama di jrxml jika pakai WHERE

            // Koneksi ke DB
            Connection conn = Koneksi.getKoneksi();

            // Isi laporan dengan data dari database
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            // Tampilkan laporan
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Struk Transaksi");
            viewer.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal mencetak struk: " + e.getMessage());
        }
    }

}
