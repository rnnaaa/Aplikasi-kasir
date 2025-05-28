package kasir_warmad.sistem;

public class pilihBarang_tambahstok {
    public int id;
    public String nama;

    public pilihBarang_tambahstok(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    @Override
    public String toString() {
        return nama; // atau return id + " - " + nama;
    }
}
