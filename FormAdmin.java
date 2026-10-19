package Koneksi;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class FormAdmin extends JFrame {

    private final int adminId;
    private final String namaAdmin;

    private JLabel lblSambut;
    private JButton btnLogout;

    private DefaultTableModel modelUser, modelLomba, modelPendaftaran;
    private JTextField txtUsername, txtPassword, txtNamaLomba;
    private JComboBox<String> cbRole;
    private JTextArea txtDeskripsi;

    private JButton btnTambahUser, btnHapusUser, btnTambahLomba, btnHapusLomba, btnRefreshPendaftaran;

    public FormAdmin(int adminId, String namaAdmin) {
        this.adminId = adminId;
        this.namaAdmin = namaAdmin;

        setTitle("Dashboard Admin - BIGOTEL");
        setSize(780, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblSambut = new JLabel("Selamat Datang, Admin: " + namaAdmin);
        lblSambut.setFont(new Font("Sans Serif", Font.BOLD, 16));
        lblSambut.setBounds(20, 15, 500, 30);
        add(lblSambut);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(640, 15, 110, 30);
        add(btnLogout);

        JTabbedPane tab = new JTabbedPane();
        tab.setBounds(20, 55, 740, 500);
        add(tab);

        tab.addTab("Pengguna", panelPengguna());
        tab.addTab("Lomba", panelLomba());
        tab.addTab("Pendaftaran & Penilaian", panelPendaftaran());

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormLogin().setVisible(true);
                dispose();
            }
        });

        muatUsers();
        muatLomba();
        muatPendaftaran();
    }

    private JPanel panelPengguna() {
        JPanel p = new JPanel(null);
        p.setPreferredSize(new java.awt.Dimension(720, 460));

        String[] kolom = {"ID", "Username", "Password", "Role"};
        modelUser = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabel = new JTable(modelUser);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBounds(10, 10, 700, 220);
        p.add(scroll);

        JLabel l1 = new JLabel("Username:");
        l1.setBounds(10, 245, 90, 25);
        p.add(l1);
        txtUsername = new JTextField();
        txtUsername.setBounds(105, 245, 200, 25);
        p.add(txtUsername);

        JLabel l2 = new JLabel("Password:");
        l2.setBounds(10, 280, 90, 25);
        p.add(l2);
        txtPassword = new JTextField();
        txtPassword.setBounds(105, 280, 200, 25);
        p.add(txtPassword);

        JLabel l3 = new JLabel("Role:");
        l3.setBounds(10, 315, 90, 25);
        p.add(l3);
        cbRole = new JComboBox<>(new String[]{"siswa", "juri", "admin"});
        cbRole.setBounds(105, 315, 200, 25);
        p.add(cbRole);

        btnTambahUser = new JButton("Tambah Pengguna");
        btnTambahUser.setBounds(330, 245, 170, 30);
        p.add(btnTambahUser);

        btnHapusUser = new JButton("Hapus Terpilih");
        btnHapusUser.setBounds(330, 280, 170, 30);
        p.add(btnHapusUser);

        btnTambahUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tambahUser();
            }
        });

        btnHapusUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hapusUser(tabel);
            }
        });

        return p;
    }

    private JPanel panelLomba() {
        JPanel p = new JPanel(null);
        p.setPreferredSize(new java.awt.Dimension(720, 460));

        String[] kolom = {"ID", "Nama Lomba", "Deskripsi"};
        modelLomba = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabel = new JTable(modelLomba);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBounds(10, 10, 700, 220);
        p.add(scroll);

        JLabel l1 = new JLabel("Nama Lomba:");
        l1.setBounds(10, 245, 100, 25);
        p.add(l1);
        txtNamaLomba = new JTextField();
        txtNamaLomba.setBounds(115, 245, 300, 25);
        p.add(txtNamaLomba);

        JLabel l2 = new JLabel("Deskripsi:");
        l2.setBounds(10, 280, 100, 25);
        p.add(l2);
        txtDeskripsi = new JTextArea();
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        JScrollPane scrollDesk = new JScrollPane(txtDeskripsi);
        scrollDesk.setBounds(115, 280, 300, 80);
        p.add(scrollDesk);

        btnTambahLomba = new JButton("Tambah Lomba");
        btnTambahLomba.setBounds(440, 245, 160, 30);
        p.add(btnTambahLomba);

        btnHapusLomba = new JButton("Hapus Terpilih");
        btnHapusLomba.setBounds(440, 280, 160, 30);
        p.add(btnHapusLomba);

        btnTambahLomba.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tambahLomba();
            }
        });

        btnHapusLomba.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hapusLomba(tabel);
            }
        });

        return p;
    }

    private JPanel panelPendaftaran() {
        JPanel p = new JPanel(null);
        p.setPreferredSize(new java.awt.Dimension(720, 460));

        String[] kolom = {"ID", "Peserta", "Lomba", "Status", "Keputusan", "Catatan"};
        modelPendaftaran = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabel = new JTable(modelPendaftaran);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBounds(10, 10, 700, 350);
        p.add(scroll);

        btnRefreshPendaftaran = new JButton("Refresh");
        btnRefreshPendaftaran.setBounds(10, 375, 120, 30);
        p.add(btnRefreshPendaftaran);

        btnRefreshPendaftaran.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                muatPendaftaran();
            }
        });

        return p;
    }

    private void muatUsers() {
        modelUser.setRowCount(0);
        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                return;
            }
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT id, username, password, role FROM users ORDER BY id");
            while (rs.next()) {
                modelUser.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat pengguna: " + ex.getMessage());
        }
    }

    private void muatLomba() {
        modelLomba.setRowCount(0);
        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                return;
            }
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT id, nama_lomba, deskripsi FROM lomba ORDER BY id");
            while (rs.next()) {
                modelLomba.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama_lomba"),
                    rs.getString("deskripsi")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat lomba: " + ex.getMessage());
        }
    }

    private void muatPendaftaran() {
        modelPendaftaran.setRowCount(0);
        String sql = "SELECT p.id, u.username, l.nama_lomba, p.status, "
                + "(SELECT pn.keputusan FROM penilaian pn WHERE pn.pendaftaran_id = p.id ORDER BY pn.waktu DESC LIMIT 1) AS keputusan, "
                + "(SELECT pn.catatan FROM penilaian pn WHERE pn.pendaftaran_id = p.id ORDER BY pn.waktu DESC LIMIT 1) AS catatan "
                + "FROM pendaftaran p "
                + "JOIN users u ON u.id = p.user_id "
                + "JOIN lomba l ON l.id = p.lomba_id "
                + "ORDER BY p.id";
        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                return;
            }
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                String keputusan = rs.getString("keputusan");
                modelPendaftaran.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("nama_lomba"),
                    rs.getString("status"),
                    keputusan == null ? "-" : ("lolos".equals(keputusan) ? "Lolos" : "Tidak Lolos"),
                    rs.getString("catatan") == null ? "-" : rs.getString("catatan")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat pendaftaran: " + ex.getMessage());
        }
    }

    private void tambahUser() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = cbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!");
            return;
        }

        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                return;
            }

            PreparedStatement cek = conn.prepareStatement("SELECT id FROM users WHERE username=?");
            cek.setString(1, username);
            if (cek.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Username \"" + username + "\" sudah dipakai!");
                return;
            }

            PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);
            pst.executeUpdate();

            txtUsername.setText("");
            txtPassword.setText("");
            muatUsers();
            JOptionPane.showMessageDialog(this,
                    "Pengguna \"" + username + "\" berhasil ditambahkan sebagai " + role + ".");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menambah pengguna: " + ex.getMessage());
        }
    }

    private void hapusUser(JTable tabel) {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu pengguna yang akan dihapus!");
            return;
        }

        int id = Integer.parseInt(modelUser.getValueAt(baris, 0).toString());
        String username = modelUser.getValueAt(baris, 1).toString();

        if (id == adminId) {
            JOptionPane.showMessageDialog(this, "Tidak bisa menghapus akun sendiri!");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Hapus pengguna \"" + username + "\"?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                return;
            }
            PreparedStatement pst = conn.prepareStatement("DELETE FROM users WHERE id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            muatUsers();
            JOptionPane.showMessageDialog(this, "Pengguna \"" + username + "\" berhasil dihapus.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus pengguna: " + ex.getMessage());
        }
    }

    private void tambahLomba() {
        String nama = txtNamaLomba.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lomba tidak boleh kosong!");
            return;
        }

        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                return;
            }
            PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO lomba (nama_lomba, deskripsi) VALUES (?, ?)");
            pst.setString(1, nama);
            pst.setString(2, deskripsi.isEmpty() ? null : deskripsi);
            pst.executeUpdate();

            txtNamaLomba.setText("");
            txtDeskripsi.setText("");
            muatLomba();
            JOptionPane.showMessageDialog(this, "Lomba \"" + nama + "\" berhasil ditambahkan.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menambah lomba: " + ex.getMessage());
        }
    }

    private void hapusLomba(JTable tabel) {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu lomba yang akan dihapus!");
            return;
        }

        int id = Integer.parseInt(modelLomba.getValueAt(baris, 0).toString());
        String nama = modelLomba.getValueAt(baris, 1).toString();

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Hapus lomba \"" + nama + "\"?\nData pendaftaran lomba ini tidak ikut terhapus.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                return;
            }
            PreparedStatement pst = conn.prepareStatement("DELETE FROM lomba WHERE id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            muatLomba();
            JOptionPane.showMessageDialog(this, "Lomba \"" + nama + "\" berhasil dihapus.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus lomba: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormAdmin(1, "admin").setVisible(true);
            }
        });
    }
}
