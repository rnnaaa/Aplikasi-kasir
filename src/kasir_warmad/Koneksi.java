package kasir_warmad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection con;

    public static Connection getKoneksi() {
        if (con == null) {
            try {
                String id = "root";
                String pass = "";
                String url = "jdbc:mysql://localhost:3306/kasir_warmad";

                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(url, id, pass);
                System.out.println("Koneksi berhasil!");
            } catch (ClassNotFoundException | SQLException e) {
                JOptionPane.showMessageDialog(null, "Gagal koneksi: " + e.getMessage());
            }
        }
        return con;
    }

    public static void main(String[] args) {
        getKoneksi(); // untuk test koneksi
    }
}