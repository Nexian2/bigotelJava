package Koneksi;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class FormJuri extends JFrame {

    private final int juriId;
    private final String namaJuri;

    private JLabel lblSambut, lblPeserta, lblKeputusan, lblCatatan;
    private JTable tabel;
    private DefaultTableModel model;
    private JRadioButton rbLolos, rbTidakLolos;
    private JTextArea txtCatatan;
    private JButton btnSimpan, btnRefresh, btnBersih, btnLogout;

    public FormJuri(int juriId, String namaJuri) {
        this.juriId = juriId;
        this.namaJuri = namaJuri;

        setTitle("Dashboard Juri - BIGOTEL");
        setSize(780, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        lblSambut = new JLabel("Selamat Datang, Juri: " + namaJuri);
        lblSambut.setFont(new Font("Sans Serif", Font.BOLD, 16));
        lblSambut.setBounds(30, 15, 500, 30);
        add(lblSambut);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(640, 15, 110, 30);
        add(btnLogout);

        String[] kolom = {"ID", "Peserta", "Lomba", "Status", "Keputusan", "Catatan"};
        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabel = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBounds(30, 55, 720, 250);
        add(scroll);

        lblPeserta = new JLabel("Peserta terpilih: -");
        lblPeserta.setFont(new Font("Sans Serif", Font.BOLD, 13));
        lblPeserta.setBounds(30, 315, 720, 25);
        add(lblPeserta);

        JPanel panelPenilaian = new JPanel();
        panelPenilaian.setLayout(null);
        panelPenilaian.setBounds(30, 350, 720, 150);
        add(panelPenilaian);

        lblKeputusan = new JLabel("Keputusan:");
        lblKeputusan.setBounds(0, 5, 100, 25);
        panelPenilaian.add(lblKeputusan);

        rbLolos = new JRadioButton("Lolos");
        rbLolos.setBounds(100, 5, 100, 25);
        panelPenilaian.add(rbLolos);

        rbTidakLolos = new JRadioButton("Tidak Lolos");
        rbTidakLolos.setBounds(210, 5, 130, 25);
        panelPenilaian.add(rbTidakLolos);

        ButtonGroup grup = new ButtonGroup();
        grup.add(rbLolos);
        grup.add(rbTidakLolos);

        lblCatatan = new JLabel("Catatan:");
        lblCatatan.setBounds(0, 40, 100, 25);
        panelPenilaian.add(lblCatatan);

        txtCatatan = new JTextArea();
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        JScrollPane scrollCatatan = new JScrollPane(txtCatatan);
        scrollCatatan.setBounds(100, 40, 380, 95);
        panelPenilaian.add(scrollCatatan);

        btnSimpan = new JButton("Kirim Pemberitahuan");
        btnSimpan.setBounds(500, 40, 210, 40);
        panelPenilaian.add(btnSimpan);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(500, 90, 100, 30);
        panelPenilaian.add(btnRefresh);

        btnBersih = new JButton("Bersihkan");
        btnBersih.setBounds(610, 90, 100, 30);
        panelPenilaian.add(btnBersih);

        tabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                isiDariTabel();
            }
        });

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanNilai();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                muatData();
            }
        });

        btnBersih.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bersihkanForm();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormLogin().setVisible(true);
                dispose();
            }
        });

        muatData();
    }

    private void muatData() {
        model.setRowCount(0);
        String sql = "SELECT p.id, u.username, l.nama_lomba, p.status, pn.keputusan, pn.catatan "
                + "FROM pendaftaran p "
                + "JOIN users u ON u.id = p.user_id "
                + "JOIN lomba l ON l.id = p.lomba_id "
                + "LEFT JOIN penilaian pn ON pn.pendaftaran_id = p.id AND pn.juri_id = ? "
                + "ORDER BY p.id";

        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Koneksi database gagal!");
                return;
            }

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, juriId);
            ResultSet rs = pst.executeQuery();

            boolean kosong = true;
            while (rs.next()) {
                kosong = false;
                String keputusan = rs.getString("keputusan");
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("nama_lomba"),
                    rs.getString("status"),
                    keputusan == null ? "-" : tampilKeputusan(keputusan),
                    rs.getString("catatan") == null ? "-" : rs.getString("catatan")
                });
            }

            if (kosong) {
                JOptionPane.showMessageDialog(this, "Belum ada peserta yang mendaftar.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data: " + ex.getMessage()
                    + "\nPastikan tabel penilaian sudah dibuat (lihat SQUEL.sql).");
        }

        bersihkanForm();
    }

    private String tampilKeputusan(String keputusan) {
        return "lolos".equals(keputusan) ? "Lolos" : "Tidak Lolos";
    }

    private void isiDariTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            return;
        }

        String peserta = model.getValueAt(baris, 1) + " - " + model.getValueAt(baris, 2);
        lblPeserta.setText("Peserta terpilih: " + peserta);

        String keputusan = model.getValueAt(baris, 4).toString();
        String catatan = model.getValueAt(baris, 5).toString();

        if ("Lolos".equals(keputusan)) {
            rbLolos.setSelected(true);
        } else if ("Tidak Lolos".equals(keputusan)) {
            rbTidakLolos.setSelected(true);
        } else {
            rbLolos.setSelected(false);
            rbTidakLolos.setSelected(false);
        }

        txtCatatan.setText("-".equals(catatan) ? "" : catatan);
    }

    private void simpanNilai() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu peserta yang akan dinilai!");
            return;
        }

        if (!rbLolos.isSelected() && !rbTidakLolos.isSelected()) {
            JOptionPane.showMessageDialog(this, "Pilih keputusan: Lolos atau Tidak Lolos!");
            return;
        }

        String catatan = txtCatatan.getText().trim();
        boolean lolos = rbLolos.isSelected();

        if (!lolos && catatan.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Catatan wajib diisi! Jelaskan alasan peserta tidak lolos.",
                    "Catatan Kosong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String peserta = model.getValueAt(baris, 1).toString();
        String lomba = model.getValueAt(baris, 2).toString();
        String keputusanTeks = lolos ? "Lolos" : "Tidak Lolos";

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Kirim pemberitahuan hasil ke peserta \"" + peserta + "\"?\n"
                + "Lomba: " + lomba + "\nKeputusan: " + keputusanTeks,
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        int pendaftaranId = Integer.parseInt(model.getValueAt(baris, 0).toString());
        String keputusan = lolos ? "lolos" : "tidak_lolos";
        String status = lolos ? "Lolos" : "Tidak Lolos";

        try {
            Connection conn = Koneksi.configDB();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Koneksi database gagal!");
                return;
            }

            String sqlNilai = "INSERT INTO penilaian (pendaftaran_id, juri_id, keputusan, catatan) "
                    + "VALUES (?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE keputusan=VALUES(keputusan), catatan=VALUES(catatan), waktu=NOW()";
            PreparedStatement pstNilai = conn.prepareStatement(sqlNilai);
            pstNilai.setInt(1, pendaftaranId);
            pstNilai.setInt(2, juriId);
            pstNilai.setString(3, keputusan);
            pstNilai.setString(4, catatan.isEmpty() ? null : catatan);
            pstNilai.executeUpdate();

            String sqlStatus = "UPDATE pendaftaran SET status=? WHERE id=?";
            PreparedStatement pstStatus = conn.prepareStatement(sqlStatus);
            pstStatus.setString(1, status);
            pstStatus.setInt(2, pendaftaranId);
            pstStatus.executeUpdate();

            muatData();

            JOptionPane.showMessageDialog(this,
                    "Pemberitahuan hasil berhasil dikirim ke peserta \"" + peserta + "\"!\n"
                    + "Status pendaftaran: " + status
                    + (catatan.isEmpty() ? "" : "\nCatatan: " + catatan));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan nilai: " + ex.getMessage());
        }
    }

    private void bersihkanForm() {
        lblPeserta.setText("Peserta terpilih: -");
        rbLolos.setSelected(false);
        rbTidakLolos.setSelected(false);
        txtCatatan.setText("");
        tabel.clearSelection();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormJuri(1, "juri").setVisible(true);
            }
        });
    }
}
