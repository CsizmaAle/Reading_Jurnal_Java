package Modele_de_date;

import java.time.LocalDate;

/**
 * Clasa care defineste o intrare de jurnal de carte
 */
public class Jurnal {
    private int id;
    private int idCarte;
    private LocalDate data;
    private int paginaCurenta;
    private int paginiCitite;

    /**
     * Constructor fara parametrii pentru clasa Jurnal
     */
    public Jurnal() {}

    /**
     * Constructor cu parametrii pentru clasa Jurnal
     * @param id int; id-ul intrarii de jurnal
     * @param idCarte int; id-ul cartii pentru care se face intrarea
     * @param data LocalDate; data la care se face notarea
     * @param paginaCurenta int; numarul de pagini la care a ramas cititorul
     */
    public Jurnal(int id, int idCarte, LocalDate data, int paginaCurenta) {
        this.id = id;
        this.idCarte = idCarte;
        this.data = data;
        if (paginaCurenta < 0) {
            throw new IllegalArgumentException("Pagina curenta nu poate fi negativa");
        }
        this.paginaCurenta = paginaCurenta;
    }

    /**
     * Metoda te tip setter care seteaza id-ul notarii
     * @param id int; id-ul care se seteaza pentru intrarea curenta
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Metoda te tip setter care seteaza id-ul cartii aferente intrarii curente in jurnal
     * @param idCarte int; id-ul cartii pentru intrarea curenta in jurnal
     */
    public void setIdCarte(int idCarte) {
        this.idCarte = idCarte;
    }

    /**
     * Metoda te tip setter care seteaza datat aferenta intrarii in jurnal
     * @param data LocalDate; data pentru intrarea curenta in jurnal
     */
    public void setData(LocalDate data) {
        this.data = data;
    }

    /**
     * Metoda de tip setter pentru pagina la care s-a oprit din citit utilizatorul pentru intrarea acurenta
     * @param paginaCurenta int; numarul paginii la care s-a oprit utilizatorul
     */
    public void setPaginaCurenta(int paginaCurenta) {
        if (paginaCurenta < 0) {
            throw new IllegalArgumentException("Pagina curenta nu poate fi negativa");
        }
        this.paginaCurenta = paginaCurenta;
    }

    /**
     *Metoda de tip setter pentru numarul de pagini citite de utilizator pentru intrarea acurenta
     * @param paginiCitite int; numarul de pagini citite de utilizator pana la introduceerea inregistrarii
     */
    public void setPaginiCitite(int paginiCitite) {
        if (paginiCitite <=0) {
            throw new IllegalArgumentException("Numarul de pagini citite trebuie sa fie mai mare decat 0");
        }
        this.paginiCitite = paginiCitite;
    }

    /**
     * Metoda de tip getter pentru id-ul inregistrarii
     * @return int; id-ul inregistrarii
     */
    public int getId() {
        return id;
    }

    /**
     * Metoda de tip getter pentru id-ul cartii pentru care se face inregistrarea
     * @return int; id-ul cartii pentru care se face inregistrarea
     */
    public int getIdCarte() {
        return idCarte;
    }

    /**
     * Metoda de tip getter pentru data la care se face/s-a facut inregistrarea
     * @return LocaDate; data la care s-a facut inregisrearea
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Metoda de tip getter pentru pagina curenta intrdusa de utilizator pentru inregistrare
     * @return int; pagina curenta
     */
    public int getPaginaCurenta() {
        return paginaCurenta;
    }

    /**
     * Metoda de tip getter pentru numarul de pagini citite de utiizator de la inregistrarea precedenta pentru cartea actuala pana in prezent
     * @return int; numarul de pagini citite de utilizator
     */
    public int getPaginiCitite() {
        return paginiCitite;
    }

    /**
     * Metoda care transforma un obiect de tip jurnal intr-un string
     * @return String; obiectul jurnal transformat in string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("data=").append(data);
        sb.append("; id=").append(id);
        sb.append("; idCarte=").append(idCarte);
        sb.append("; paginaCurenta=").append(paginaCurenta);
        sb.append("; paginiCitite=").append(paginiCitite);

        return sb.toString();
    }

}
