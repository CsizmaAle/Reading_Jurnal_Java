package Modele_de_date;

import java.time.LocalDate;

/**
 * Clasa ce defineste obiecte de tip carte
 */
public class Carte {
    private int id;
    private String titlu;
    private String autor;
    private String gen;
    private Format tip;
    private int numar_pagini_totale;
    private Status status;
    private String imagine;
    private double procent;
    private LocalDate dataStart;
    private LocalDate dataFinish;
    private double scor;
    private String review;

    /**
     * Constructor fara parametrii pentru clasa Carte
     */
    public Carte() {
    }

    /**
     * Constructor cu parametrii pentru clasa carte
     * @param id int; id-ul cartii
     * @param titlu String; titlul cartii
     * @param autor String; autorul cartii
     * @param gen String; genul cartii
     * @param tip Format; formatul cartii: EBOOK, PAPERBACK, HARDCOVER
     * @param numar_pagini_totale int; numarul de pagini ale cartii
     * @param imagine String; o adresa catre imaginea - coperta a cartii
     */
    public Carte(int id, String titlu, String autor, String gen, Format tip, int numar_pagini_totale, String imagine) {
        this.id = id;
        this.titlu = titlu;
        this.autor = autor;
        this.gen = gen;
        this.tip = tip;
        this.numar_pagini_totale = numar_pagini_totale;
        this.status = Status.NECITITA;
        this.imagine = imagine;
    }

    /**
     * Constructor cu parametrii pentru clasa carte
     * @param id int; id-ul cartii
     * @param titlu String; titlul cartii
     * @param autor String; autorul cartii
     * @param gen String; genul cartii
     * @param tip Format; formatul cartii: EBOOK, PAPERBACK, HARDCOVER
     * @param nrPagini int; numarul de pagini ale cartii
     * @param imagine String; o adresa catre imaginea - coperta a cartii
     * @param status Status; statusul cartii
     * @param dataStart LocalDate; data la care a fost inceputa cartea
     * @param dataFinish LocalDate; data la care a fost terminata cartea
     * @param procent double; cat la suta din carte s-a citit [0,1]
     * @param review String; review-ul lasat de cititor cartii
     * @param scor double; socrul x/5 cat ii ofera cititorul cartii
     */
    public Carte(int id, String titlu, String autor, String gen, Format tip, int nrPagini, String imagine, Status status, double procent, LocalDate dataStart, LocalDate dataFinish, double scor, String review) {
        this.id = id;
        this.titlu = titlu;
        this.autor = autor;
        this.gen = gen;
        this.tip = tip;
        this.numar_pagini_totale = nrPagini;
        this.status = status;
        this.imagine = imagine;
        this.dataStart = dataStart;
        this.dataFinish = dataFinish;
        this.scor = scor;
        this.review = review;
        this.procent = procent;

    }

    /**
     * Metoda de tip setter pentru titlu unei carti
     * @param titlu String; reprezinta titlul unei carti
     */
    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    /**
     * Metoda de tip setter pentru autorul unei carti
     * @param autor String; reprezinta autorul unei carti
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * Metoda de tip setter pentru genul unei carti
     * @param gen String; reprezinta genul unei carti
     */
    public void setGen(String gen) {
        this.gen = gen;
    }

    /**
     * Metoda de tip setter pentru tipul cartii
     * @param tip Format; tipul cartii: EBOOK, PAPERBACK, HARDCOVER
     */
    public void setTip(Format tip) {
        this.tip = tip;
    }

    /**
     * Metoda de tip setter pentru numarul total de pagini al unei carti
     * @param numar_pagini_totale int; nr total de pagini al cartii
     */
    public void setNumar_pagini_totale(int numar_pagini_totale) {
        this.numar_pagini_totale = numar_pagini_totale;
    }

    /**
     * Motoda de tip setter pentru statusul cartii
     * @param status Status; statusul cartii care poate fi: NECITATA, CITITA, IN_CURS_DE_CITIRE, ABANDONATA
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Metoda de tip setter pentru imaginea - coperta a cartii
     * @param imagine String; adresa imaginii - coperta a cartii
     */
    public void setImagine(String imagine) {
        this.imagine = imagine;
    }

    /**
     * Metoda de tip setter pentru scorul cartii (/5)
     * @param scor double; scorul cartii din 5
     */
    public void setScor(double scor) {
        this.scor = scor;
    }

    /**
     * Metoda de tip setter pentru review-ul cartii
     * @param review String; review-ul cartii
     */
    public void setReview(String review) {
        this.review = review;
    }

    /**
     * Metoda de tip setter pentru data de start a cartii
     * @param dataStart LocalDate; data la care s-a inceput cartea
     */
    public void setDataStart(LocalDate dataStart) {
        this.dataStart = dataStart;
    }

    /**
     * Metoda de tip setter pentru data de finish a cartii
     * @param dataFinish LocalDate; data la care s-a terminat de citit cartea
     */
    public void setDataFinish(LocalDate dataFinish) {
        this.dataFinish = dataFinish;
    }

    /**
     * Metod de tip setter pentru id-ul cartii
     * @param id int; id-ul cartii
     */
    public void setId(int id) {
        this.id=id;
    }

    /**
     * Metoda de tip getter pentru id-ul unei carti
     * @return int; id-ul cartii
     */
    public int getId() {
        return id;
    }

    /**
     * Metoda de tip getter pentru titlul unei carti
     * @return String; titlul unei carti
     */
    public String getTitlu() {
        return titlu;
    }

    /**
     * Metoda de tip getter pentru autorul unei carti
     * @return String; autorul unei carti
     */
    public String getAutor() {
        return autor;
    }

    /**
     * Metoda de tip getter pentru genul unei carti
     * @return String; genul unei carti
     */
    public String getGen() {
        return gen;
    }

    /**
     * Metoda de tip getter pentru tipul unei carti
     * @return Format; tipul unei carti: EBOOK, PAPERBACK, HARDCOVER
     */
    public Format getTip() {
        return tip;
    }

    /**
     * Metoda de tip getter pentru numarul total de pagini al unei carti
     * @return int; numarul total de pagini al unei carti
     */
    public int getNumar_pagini_totale() {
        return numar_pagini_totale;
    }

    /**
     * Metoda de tip getter pentru statusul unei carti
     * @return Status; statusul unei carti: ABANDONATA, NECITITA, IN_CURS_DE_CITIRE, CITITA
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Metoda de tip getter pentru imaginea - coperta unei carti
     * @return String; adresa pentru imaginea - coperta a unei carti
     */
    public String getImagine() {
        return imagine;
    }

    /**
     * Metoda de tip getter pentru scorul cartii
     * @return double; scorul cartii
     */
    public double getScor() {
        return scor;
    }

    /**
     * Metoda de tip getter pentru review-ul unei carti
     * @return String; review-ul unei carti
     */
    public String getReview() {
        return review;
    }

    /**
     * Metoda de tip getter pentru data la care s-a inceput citirea unei carti
     * @return LocalDate; data la care s-a inceput citirea unei carti
     */
    public LocalDate getDataStart() {
        return dataStart;
    }

    /**
     * Metoda de tip getter pentru data la care s-a terminat citirea unei carti
     * @return LocalDate; data la care s-a terminat citirea unei carti
     */
    public LocalDate getDataFinish() {
        return dataFinish;
    }

    /**
     * Metoda de tip getter pentru procentul de citire al unei carti
     * @return double; cat la suta din carte s-a citit, valoare intre 0 si 1
     */
    public double getProcent() {
        return procent;
    }

    /**
     * Metoda de tip setter pentru procentul de citire al unei carti
     * @param procent double; cat la suta din carte s-a citit, valoare intre 0 si 1
     */
    public void setProcent(double procent) {
        this.procent=procent;
    }

    /**
     * Metoda care transforma obiectul cu toate caracteristicile lui in string pentru afisare
     * @return Strig; obiectul transformat in String pentru afisare
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carte:").append(titlu).append(" - ").append(autor);
        sb.append("\n\t Id: ").append(id);
        sb.append("\n\t Gen: ").append(gen);
        sb.append("\n\t Tip: ").append(tip);
        sb.append("\n\t Imagine: ").append(imagine);
        sb.append("\n\t Numar_pagini_totale: ").append(numar_pagini_totale);
        sb.append("\n\t Status: ").append(status);
        sb.append("\n\t Procent: ").append(procent*100);
        sb.append("\n\t DataStart: ").append(dataStart);
        sb.append("\n\t DataFinish: ").append(dataFinish);
        sb.append("\n\t Scor: ").append(scor);
        sb.append("\n\t Review: ").append(review);
        return sb.toString();
    }
}
