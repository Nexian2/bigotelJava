package Koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection conn;
    
    public static Connection configDB() {
        try {
            String url = "jdbc:mysql://localhost:3306/db_bigottalent";
            String user = "root";
            String pass = "";
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            conn = DriverManager.getConnection(url, user, pass);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Koneksi Database Gagal: " + e.getMessage());
        }
        return conn;
    }
}
