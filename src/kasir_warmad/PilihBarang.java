/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kasir_warmad;

public class PilihBarang {

   private String idBarang;
    private String namaBarang;

    public PilihBarang(String idBarang, String namaBarang) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    @Override
    public String toString() {
        return namaBarang;  // Supaya yang ditampilin di combo box adalah namaBarang
    }}