package Koneksi;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class FormLogin extends JFrame {

    private JLabel lblJudul, lblUser, lblPass;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnCancel;

    public FormLogin() {
        setTitle("Login - BIGOTEL");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblJudul = new JLabel("LOGIN SISTEM");
        lblJudul.setFont(new Font("Sans Serif", Font.BOLD, 18));
        lblJudul.setBounds(130, 20, 150, 30);
        add(lblJudul);

        lblUser = new JLabel("Username:");
        lblUser.setBounds(40, 70, 100, 25);
        add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(140, 70, 200, 25);
        add(txtUser);

        lblPass = new JLabel("Password:");
        lblPass.setBounds(40, 110, 100, 25);
        add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(140, 110, 200, 25);
        add(txtPass);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(140, 160, 90, 30);
        add(btnLogin);

        btnCancel = new JButton("Batal");
        btnCancel.setBounds(250, 160, 90, 30);
        add(btnCancel);

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUser.setText("");
                txtPass.setText("");
                txtUser.requestFocus();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUser.getText();
                String password = new String(txtPass.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(FormLogin.this, "Username dan Password tidak boleh kosong!");
                    return;
                }

                prosesLogin(username, password);
            }
        });
    }

    private void prosesLogin(String username, String password) {
        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Koneksi database gagal!");
                return;
            }

            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("id");

                if ("juri".equals(role)) {
                    JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat Bertugas, Juri " + username);
                    FormLogin.this.dispose();
                    new FormJuri(userId, username).setVisible(true);
                    return;
                }

                if ("admin".equals(role)) {
                    JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat Datang, Admin " + username);
                    FormLogin.this.dispose();
                    new FormAdmin(userId, username).setVisible(true);
                    return;
                }

                if ("siswa".equals(role)) {
                    JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat Datang, " + username);
                    FormLogin.this.dispose();
                    new FormSiswa(userId, username).setVisible(true);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat Datang, " + username);
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password Salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormLogin().setVisible(true);
            }
        });
    }
}
