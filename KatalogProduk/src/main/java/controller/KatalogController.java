
package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import data.ProdukRepository;
import model.Produk;

public class KatalogController {

    @FXML
    private TextField namaField;

    @FXML
    private TextField hargaField;

    @FXML
    private ComboBox<String> fotoBox;

    @FXML
    private Label pesanLabel;

    @FXML
    private Label jumlahLabel;

    @FXML
    private TilePane katalogPane;

    @FXML
    private Button simpanButton;

    private final ProdukRepository repository =
            new ProdukRepository();

    // Menyimpan ID produk yang sedang diedit
    private long selectedId = -1;

    // Inisialisasi aplikasi
    @FXML
    private void initialize() {

        fotoBox.getItems().addAll(
                "roti.png",
                "susu.png",
                "kopi.png"
        );

        fotoBox.setValue("roti.png");

        try {
            showData();
        } catch (SQLException e) {
            pesanLabel.setText(
                    "Gagal membaca database. Lihat Output."
            );
            e.printStackTrace();
        }
    }

    // READ: Menampilkan seluruh data produk
    private void showData() throws SQLException {

        ArrayList<Produk> daftar = repository.getAll();

        katalogPane.getChildren().clear();

        for (Produk produk : daftar) {

            VBox kartu = createProductCard(produk);

            katalogPane.getChildren().add(kartu);
        }

        jumlahLabel.setText(
                "Jumlah produk: " + daftar.size()
        );
    }

    // Membuat tampilan kartu produk
    private VBox createProductCard(Produk produk) {

        ImageView fotoView = new ImageView();

        fotoView.setFitWidth(170);
        fotoView.setFitHeight(110);
        fotoView.setPreserveRatio(true);

        StackPane bingkai = new StackPane();

        bingkai.setPrefSize(180, 120);
        bingkai.setMinSize(180, 120);
        bingkai.setMaxSize(180, 120);

        URL lokasi = getClass().getResource(
                "/image/" + produk.getFoto()
        );

        if (lokasi != null) {

            Image gambar = new Image(
                    lokasi.toExternalForm(),
                    170,
                    110,
                    true,
                    true
            );

            if (!gambar.isError()) {
                fotoView.setImage(gambar);
                bingkai.getChildren().add(fotoView);
            } else {
                bingkai.getChildren().add(
                        new Label("Gambar tidak terbaca")
                );
            }

        } else {

            bingkai.getChildren().add(
                    new Label("Gambar tidak tersedia")
            );
        }

        Label namaLabel = new Label(produk.getNama());
        namaLabel.setWrapText(true);
        namaLabel.getStyleClass().add("nama-produk");

        Label hargaLabel = new Label(
                "Rp" + produk.getHarga()
        );

        Label idLabel = new Label(
                "ID: " + produk.getId()
        );

        idLabel.getStyleClass().add("muted");

        // Tombol Update
        Button updateButton = new Button("Update");

        updateButton.setOnAction(
                e -> editProduct(produk)
        );

        // Tombol Delete
        Button deleteButton = new Button("Delete");

        deleteButton.setOnAction(
                e -> deleteProduct(produk)
        );

        VBox kartu = new VBox();

        kartu.setSpacing(8);
        kartu.setMinWidth(220);
        kartu.setPrefWidth(220);
        kartu.setMaxWidth(220);

        kartu.getStyleClass().add("kartu-produk");

        kartu.getChildren().addAll(
                bingkai,
                namaLabel,
                hargaLabel,
                idLabel,
                updateButton,
                deleteButton
        );

        return kartu;
    }

    // UPDATE: Memasukkan data produk ke formulir
    private void editProduct(Produk produk) {

        selectedId = produk.getId();

        namaField.setText(produk.getNama());

        hargaField.setText(
                String.valueOf(produk.getHarga())
        );

        fotoBox.setValue(produk.getFoto());

        simpanButton.setText("Simpan Perubahan");

        pesanLabel.setText(
                "Sedang mengedit produk ID: " + selectedId
        );
    }

    // CREATE dan UPDATE: Menyimpan data produk
    @FXML
    private void saveProduct() {

        try {

            String nama = namaField.getText().trim();
            String hargaText = hargaField.getText().trim();
            String foto = fotoBox.getValue();

            // Validasi nama
            if (nama.isEmpty()) {

                pesanLabel.setText(
                        "Nama produk tidak boleh kosong."
                );

                return;
            }

            // Validasi harga
            if (hargaText.isEmpty()) {

                pesanLabel.setText(
                        "Harga produk harus diisi."
                );

                return;
            }

            long harga = Long.parseLong(hargaText);

            if (harga <= 0) {

                pesanLabel.setText(
                        "Harga harus lebih dari 0."
                );

                return;
            }

            // Validasi foto
            if (foto == null || foto.isEmpty()) {

                pesanLabel.setText(
                        "Silakan pilih foto produk."
                );

                return;
            }

            Produk produk;

            if (selectedId == -1) {

                // CREATE
                produk = new Produk(
                        nama,
                        harga,
                        foto
                );

                repository.add(produk);

                pesanLabel.setText(
                        "Produk berhasil ditambahkan."
                );

            } else {

                // UPDATE
                produk = new Produk(
                        selectedId,
                        nama,
                        harga,
                        foto
                );

                repository.update(produk);

                pesanLabel.setText(
                        "Produk berhasil diperbarui."
                );
            }

            // Reset formulir dan tampilkan data terbaru
            resetForm();

            showData();

        } catch (NumberFormatException e) {

            pesanLabel.setText(
                    "Harga harus berupa angka bulat."
            );

        } catch (IllegalArgumentException e) {

            pesanLabel.setText(e.getMessage());

        } catch (SQLException e) {

            pesanLabel.setText(
                    "Gagal menyimpan data. Lihat Output."
            );

            e.printStackTrace();
        }
    }

    // DELETE: Menghapus produk dari database
    private void deleteProduct(Produk produk) {

        Alert konfirmasi = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        konfirmasi.setTitle("Konfirmasi Hapus");

        konfirmasi.setHeaderText(
                "Hapus Produk?"
        );

        konfirmasi.setContentText(
                "Apakah kamu yakin ingin menghapus produk "
                + produk.getNama() + "?"
        );

        konfirmasi.showAndWait().ifPresent(jawaban -> {

            if (jawaban == ButtonType.OK) {

                try {

                    repository.delete(produk.getId());

                    // Jika produk yang dihapus sedang diedit
                    if (selectedId == produk.getId()) {
                        resetForm();
                    }

                    // Memperbarui katalog
                    showData();

                    pesanLabel.setText(
                            "Produk berhasil dihapus."
                    );

                } catch (SQLException e) {

                    pesanLabel.setText(
                            "Gagal menghapus produk."
                    );

                    e.printStackTrace();
                }
            }
        });
    }

    // Membatalkan proses edit
    @FXML
    private void cancelEdit() {

        resetForm();

        pesanLabel.setText(
                "Form berhasil direset."
        );
    }

    // Mengembalikan formulir ke kondisi awal
    private void resetForm() {

        selectedId = -1;

        namaField.clear();
        hargaField.clear();

        fotoBox.setValue("roti.png");

        simpanButton.setText("Simpan");
    }
}
