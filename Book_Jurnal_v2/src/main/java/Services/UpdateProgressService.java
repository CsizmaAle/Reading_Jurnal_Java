package Services;

import Modele_de_date.Carte;
import Modele_de_date.Status;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Clasa de servicii ce are ca rol inregistrarea progresului de lectura a unei carti
 */
public class UpdateProgressService {

    /**
     * Metoda principala care inregistreaza progresul cititorului
     * @param carte Carte; cartea pentru care se inregistreaza progresul
     * @param paginaCurenta int; pagina curenta pentru carte
     * @param conn Connection; conexiunea cu baza de date
     */
    public static void readingProgress(Carte carte, int paginaCurenta, Connection conn) {
        try {
            if (carte == null)  throw new IllegalArgumentException("Cartea este null");
            if (paginaCurenta < 0 || paginaCurenta > carte.getNumar_pagini_totale()) throw new IllegalArgumentException("Pagina curenta invalida");

            conn.setAutoCommit(false);
            int lastPagina = fetchLastPaginaCurenta(conn, carte.getId());
            int paginiCitite = Math.max(0, paginaCurenta - lastPagina);
            insertReadingLog(conn, carte.getId(), LocalDate.now(), paginaCurenta, paginiCitite);
            updateBookProgress(conn, carte, paginaCurenta);

            conn.commit();
        } catch (Exception ex) {
            try { conn.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException("Eroare la inregistrarea progresului: " + ex.getMessage(), ex);
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }

    /**
     * Metoda ce permite in scrierea bazei de date a unei noi intrari in jurnalul de lectura
     * @param conn Connection, obiect ce reprezinta conexiunea cu baza de date
     * @param bookId int; id-ul cartii pentru care se face intrarea in jurnalul de lectura
     * @param data LocalDate; data curenta
     * @param paginaCurenta int; pagina la care a ajuns utilizatorul
     * @param paginiCitite int; numarul de pagini citite
     * @throws Exception exceptie ce se arunca in cazul in care procesul de inserare esueaza
     */
    private static void insertReadingLog(Connection conn, int bookId, LocalDate data, int paginaCurenta, int paginiCitite) throws Exception {
        String sql = """
                INSERT INTO public.reading_log (book_id, data, pagina_curenta, pagini_citite)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setDate(2, Date.valueOf(data));
            ps.setInt(3, paginaCurenta);
            ps.setInt(4, paginiCitite);
            ps.executeUpdate();
        }
    }

    /**
     * Metoda ce mpodifica statusul unei carti la nevoie in timpul inregistrarii progresului.
     * Poate fi utila cand cartea se finalizeaza sau se incepe lectura ei
     * @param conn Connection; obiect ce reprezinta conexiunea cu baza de date
     * @param carte Carte; cartea pentru care se modifica statusul
     * @param paginaCurenta int; pagin ala care a ajuns utilizatorul in carte
     * @throws Exception esceptie ce este aruncata in cazum, in care esueaza mnodificarea statusului in baza de date
     */
    private static void updateBookProgress(Connection conn, Carte carte, int paginaCurenta) throws Exception {
        int total = carte.getNumar_pagini_totale();
        double procent = total == 0 ? 0 : (double) paginaCurenta / total;

        LocalDate dataStart = carte.getDataStart();
        LocalDate dataFinish = carte.getDataFinish();
        Status status = carte.getStatus();

        if (paginaCurenta > 0 && dataStart == null) {
            dataStart = LocalDate.now();
        }

        if (paginaCurenta == total && total > 0) {
            status = Status.FINALIZATA;
            dataFinish = LocalDate.now();
            procent = 1;
        } else if (paginaCurenta > 0) {
            status = Status.IN_CURS_DE_CITIRE;
        }

        String sql = """
                UPDATE public.books
                SET procent = ?,
                    status = ?,
                    data_start = ?,
                    data_finish = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, procent);
            ps.setString(2, status == null ? null : status.name());
            if (dataStart == null) {
                ps.setNull(3, java.sql.Types.DATE);
            } else {
                ps.setDate(3, Date.valueOf(dataStart));
            }
            if (dataFinish == null) {
                ps.setNull(4, java.sql.Types.DATE);
            } else {
                ps.setDate(4, Date.valueOf(dataFinish));
            }
            ps.setInt(5, carte.getId());
            ps.executeUpdate();
        }

        carte.setProcent(procent);
        carte.setStatus(status);
        carte.setDataStart(dataStart);
        carte.setDataFinish(dataFinish);
    }

    /**
     * Metoda de tip getter/fetcher care preia din baza de date, din tabela responsanila cu reading log, ultima pagina la care a ajuns utilizatorul pentru cartea cu un anumit id
     * Metoda este utilizata pentru a calcula numarul de pagini citite in sedinta curenta
     * @param conn Connection; obiect ce reprezinta conexiunea cu baza de date
     * @param bookId int; id-ul cartii pentru care se cauta utliam intrare in jurnal si ultimul pagian_curenta
     * @return int; ultima pagina_curenta prentru cartea cautata
     */
    private static int fetchLastPaginaCurenta(Connection conn, int bookId) {
        String sql = """
                SELECT pagina_curenta
                FROM public.reading_log
                WHERE book_id = ?
                ORDER BY data DESC, id DESC
                LIMIT 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Math.max(0, rs.getInt(1));
                }
            }
        } catch (Exception e) {
            System.out.println("WARN: nu pot citi ultima pagina pentru book_id=" + bookId + ": " + e.getMessage());
        }
        return 0;
    }



}
