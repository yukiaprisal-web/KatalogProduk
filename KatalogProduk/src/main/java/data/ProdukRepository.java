
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import db.Database;
import model.Produk;

public class ProdukRepository {

    // CREATE: Menambahkan produk
    public void add(Produk produk) throws SQLException {
        String sql = "INSERT INTO produk (nama, harga, foto) VALUES (?, ?, ?)";

        try (
            Connection koneksi = Database.bukaKoneksi();
            PreparedStatement perintah = koneksi.prepareStatement(sql)
        ) {
            perintah.setString(1, produk.getNama());
            perintah.setLong(2, produk.getHarga());
            perintah.setString(3, produk.getFoto());

            perintah.executeUpdate();
        }
    }

    // READ: Mengambil semua produk
    public ArrayList<Produk> getAll() throws SQLException {
        ArrayList<Produk> daftarProduk = new ArrayList<>();

        String sql = "SELECT id, nama, harga, foto FROM produk ORDER BY id";

        try (
            Connection koneksi = Database.bukaKoneksi();
            PreparedStatement perintah = koneksi.prepareStatement(sql);
            ResultSet hasil = perintah.executeQuery()
        ) {
            while (hasil.next()) {
                long id = hasil.getLong("id");
                String nama = hasil.getString("nama");
                long harga = hasil.getLong("harga");
                String foto = hasil.getString("foto");

                Produk produk = new Produk(id, nama, harga, foto);
                daftarProduk.add(produk);
            }
        }

        return daftarProduk;
    }

    // UPDATE: Mengubah data produk
    public void update(Produk produk) throws SQLException {
        String sql = "UPDATE produk SET nama = ?, harga = ?, foto = ? WHERE id = ?";

        try (
            Connection koneksi = Database.bukaKoneksi();
            PreparedStatement perintah = koneksi.prepareStatement(sql)
        ) {
            perintah.setString(1, produk.getNama());
            perintah.setLong(2, produk.getHarga());
            perintah.setString(3, produk.getFoto());
            perintah.setLong(4, produk.getId());

            perintah.executeUpdate();
        }
    }

    // DELETE: Menghapus produk
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM produk WHERE id = ?";

        try (
            Connection koneksi = Database.bukaKoneksi();
            PreparedStatement perintah = koneksi.prepareStatement(sql)
        ) {
            perintah.setLong(1, id);
            perintah.executeUpdate();
        }
    }
}
