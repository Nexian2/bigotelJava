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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class FormSiswa extends JFrame {

    private final int siswaId;
    private final String namaSiswa;

    private JLabel lblSambut;
    private JButton btnLogout, btnDaftar, btnRefreshSaya;
    private DefaultTableModel modelSaya, modelLomba;

    public FormSiswa(int siswaId, String namaSiswa) {
        this.siswaId = siswaId;
        this.namaSiswa = namaSiswa;

        setTitle("Dashboard Siswa - BIGOTEL");
        setSize(780, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblSambut = new JLabel("Selamat Datang, Siswa: " + namaSiswa);
        lblSambut.setFont(new Font("Sans Serif", Font.BOLD, 16));
        lblSambut.setBounds(20, 15, 500, 30);
        add(lblSambut);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(640, 15, 110, 30);
        add(btnLogout);

        JTabbedPane tab = new JTabbedPane();
        tab.setBounds(20, 55, 740, 470);
        add(tab);

        tab.addTab("Pendaftaran Saya", panelSaya());
        tab.addTab("Daftar Lomba", panelLomba());

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormLogin().setVisible(true);
                dispose();
            }
        });

        muatSaya();
        muatLomba();
    }

    private JPanel panelSaya() {
        JPanel p = new JPanel(null);
        p.setPreferredSize(new java.awt.Dimension(720, 440));

        String[] kolom = {"ID", "Lomba", "Status", "Keputusan Juri", "Catatan Juri"};
        modelSaya = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabel = new JTable(modelSaya);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBounds(10, 10, 700, 350);
        p.add(scroll);

        JLabel info = new JLabel("Hasil penilaian dari juri akan tampil di kolom Keputusan & Catatan.");
        info.setBounds(10, 370, 500, 25);
        p.add(info);

        btnRefreshSaya = new JButton("Refresh");
        btnRefreshSaya.setBounds(600, 370, 110, 30);
        p.add(btnRefreshSaya);

        btnRefreshSaya.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                muatSaya();
            }
        });

        return p;
    }

    private JPanel panelLomba() {
        JPanel p = new JPanel(null);
        p.setPreferredSize(new java.awt.Dimension(720, 440));

        String[] kolom = {"ID", "Nama Lomba", "Deskripsi"};
        modelLomba = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabel = new JTable(modelLomba);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBounds(10, 10, 700, 350);
        p.add(scroll);

        JLabel info = new JLabel("Pilih lomba lalu klik tombol daftar.");
        info.setBounds(10, 370, 400, 25);
        p.add(info);

        btnDaftar = new JButton("Daftar Lomba Terpilih");
        btnDaftar.setBounds(500, 370, 210, 30);
        p.add(btnDaftar);

        btnDaftar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                daftarLomba(tabel);
            }
        });

        return p;
    }

    private void muatSaya() {
        modelSaya.setRowCount(0);
        String sql = "SELECT p.id, l.nama_lomba, p.status, "
                + "(SELECT pn.keputusan FROM penilaian pn WHERE pn.pendaftaran_id = p.id ORDER BY pn.waktu DESC LIMIT 1) AS keputusan, "
                + "(SELECT pn.catatan FROM penilaian pn WHERE pn.pendaftaran_id = p.id ORDER BY pn.waktu DESC LIMIT 1) AS catatan "
                + "FROM pendaftaran p "
                + "JOIN lomba l ON l.id = p.lomba_id "
                + "WHERE p.user_id = ? "
                + "ORDER BY p.id";
        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                return;
            }
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, siswaId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String keputusan = rs.getString("keputusan");
                modelSaya.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama_lomba"),
                    rs.getString("status"),
                    keputusan == null ? "Menunggu penilaian" : ("lolos".equals(keputusan) ? "Lolos" : "Tidak Lolos"),
                    rs.getString("catatan") == null ? "-" : rs.getString("catatan")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat pendaftaran: " + ex.getMessage());
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

    private void daftarLomba(JTable tabel) {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu lomba yang mau diikuti!");
            return;
        }

        int lombaId = Integer.parseInt(modelLomba.getValueAt(baris, 0).toString());
        String namaLomba = modelLomba.getValueAt(baris, 1).toString();

        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                return;
            }

            PreparedStatement cek = conn.prepareStatement(
                    "SELECT id FROM pendaftaran WHERE user_id=? AND lomba_id=?");
            cek.setInt(1, siswaId);
            cek.setInt(2, lombaId);
            if (cek.executeQuery().next()) {
                JOptionPane.showMessageDialog(this,
                        "Kamu sudah terdaftar di lomba \"" + namaLomba + "\"!");
                return;
            }

            int konfirmasi = JOptionPane.showConfirmDialog(this,
                    "Daftar ke lomba \"" + namaLomba + "\"?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (konfirmasi != JOptionPane.YES_OPTION) {
                return;
            }

            PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO pendaftaran (user_id, lomba_id, status) VALUES (?, ?, 'Dokumen dalam Tinjauan')");
            pst.setInt(1, siswaId);
            pst.setInt(2, lombaId);
            pst.executeUpdate();

            muatSaya();
            JOptionPane.showMessageDialog(this,
                    "Pendaftaran ke \"" + namaLomba + "\" berhasil!\nStatus: Dokumen dalam Tinjauan");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mendaftar: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormSiswa(2, "budi").setVisible(true);
            }
        });
    }
}
