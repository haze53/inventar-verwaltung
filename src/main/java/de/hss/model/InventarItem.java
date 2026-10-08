package de.hss.model;

public class InventarItem {

    private Integer id;
    private String name;
    private String kategorie;
    private int anzahl;
    private String lagerort;
    private String barcode;
    private String notiz;

    public InventarItem() {
        this.anzahl = 1;
    }

    public InventarItem(Integer id, String name, String kategorie, int anzahl,
                        String lagerort, String barcode, String notiz) {
        this.id = id;
        this.name = name;
        this.kategorie = kategorie;
        this.anzahl = anzahl;
        this.lagerort = lagerort;
        this.barcode = barcode;
        this.notiz = notiz;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
    }

    public String getLagerort() {
        return lagerort;
    }

    public void setLagerort(String lagerort) {
        this.lagerort = lagerort;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getNotiz() {
        return notiz;
    }

    public void setNotiz(String notiz) {
        this.notiz = notiz;
    }
}
