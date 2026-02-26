package Services;

import Modele_de_date.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import static Logica.Backup.exportCSV;
import static Logica.Backup.exportJSON;
import static Logica.Backup.importCSV;
import static Logica.Backup.importJSON;
import Modele_de_date.Jurnal;

/**
 * Clasa ce are ca scop implementarea unor metode responsabile de importul si exportul din baza de date si in baza de date
 */
public class BackupImportService {
    /**
     * Metoda folosita pentru a importa datele de tip jurnal -  intrarile de jurnal din jormat JSON
     * @param path String; calea catre fisierul de import
     * @param conn Connection; conexiunea cu baza de date
     * @return int: 0 daca nu s-au reusit inserarile si 1 daca s-au reusit
     */
    public static int importReadingLogDinJSON_InDB(String path, Connection conn) {
        ArrayList<Jurnal> jurnal = importJSON(path);
        if (jurnal == null || jurnal.isEmpty()) return 0;

        jurnal.sort(Comparator.comparing(Jurnal::getIdCarte).thenComparing(j -> j.getData() == null ? LocalDate.MIN : j.getData()).thenComparingInt(Jurnal::getPaginaCurenta));
        Map<Long, Integer> lastPageByBook = new HashMap<>();

        int inserted = 0;
        String sql = """
            INSERT INTO public.reading_log (book_id, data, pagina_curenta, pagini_citite)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (book_id, data, pagina_curenta) DO UPDATE
            SET pagini_citite = EXCLUDED.pagini_citite
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Jurnal j : jurnal) {
                long bookId = j.getIdCarte();
                LocalDate d = j.getData();
                int paginaCurenta = j.getPaginaCurenta();

                Integer last = lastPageByBook.get(bookId);
                if (last == null) {
                    last = fetchLastPaginaCurentaFromDB(conn, bookId);
                    lastPageByBook.put(bookId, last);
                }

                int paginiCitite;
                if (last == 0)
                    paginiCitite = Math.max(0, paginaCurenta);
                 else
                    paginiCitite = Math.max(0, paginaCurenta - last);

                ps.setLong(1, bookId);
                ps.setDate(2, (d == null) ? null : Date.valueOf(d));
                ps.setInt(3, paginaCurenta);
                ps.setInt(4, paginiCitite);
                ps.addBatch();

                lastPageByBook.put(bookId, paginaCurenta);
            }

            int[] res = ps.executeBatch();
            conn.commit();

            for (int r : res) {
                if (r > 0 || r == Statement.SUCCESS_NO_INFO) inserted++;
            }

        } catch (Exception e) {
            try { conn.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException("Import reading_log failed: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ignored) {}
        }

        return inserted;
    }

    /**
     * Returnează ultima pagina_curenta din DB pentru cartea dată.
     * Dacă nu există intrări -> 0.
     * @param conn Connection; conexiunea la baza de date
     * @param bookId long, id-ul cartii pentru care se cauta intrari
     * @return int; ultima pagina_curenta din bd sau 0
     */
    private static int fetchLastPaginaCurentaFromDB(Connection conn, long bookId) {
        String q = """
            SELECT pagina_curenta
            FROM public.reading_log
            WHERE book_id = ?
            ORDER BY data DESC, id DESC
            LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int p = rs.getInt(1);
                    return Math.max(0, p);
                }
            }
        } catch (Exception e) {
            System.out.println("WARN: nu se poate citi ultima pagina din DB pentru book_id=" + bookId + ": " + e.getMessage());
        }
        return 0;
    }

    /**
     * Metoda ce importa datele din fisierul CSV in baza de date
     * @param path String, calea catre fisierul CSV
     * @param conn Connection, conexiunea cu baza de date
     * @return int: un numar ce reprezinta numarul de linii daca s-a reusit importarea si 0 in caz contrar
     */
    public static int importCartiCSV_InDB(String path, Connection conn) {
        ArrayList<Carte> carti = importCSV(path);
        if (carti == null || carti.isEmpty()) return 0;
        int inserted = 0;

        String sql = """
            INSERT INTO public.books
            (titlu, autor, gen, tip, pagini_totale, status, imagine,
             procent, data_start, data_finish, scor, review)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (Carte c : carti) {
                ps.setString(1, c.getTitlu());
                ps.setString(2, c.getAutor());
                ps.setString(3, c.getGen());
                ps.setString(4, c.getTip().name());
                ps.setInt(5, c.getNumar_pagini_totale());
                ps.setString(6, c.getStatus().name());
                ps.setString(7, c.getImagine());
                ps.setDouble(8, c.getProcent());

                if (c.getDataStart() == null)
                    ps.setNull(9, Types.DATE);
                else
                    ps.setDate(9, Date.valueOf(c.getDataStart()));

                if (c.getDataFinish() == null)
                    ps.setNull(10, Types.DATE);
                else
                    ps.setDate(10, Date.valueOf(c.getDataFinish()));

                if (c.getScor() == 0)
                    ps.setNull(11, Types.INTEGER);
                else
                    ps.setInt(11, (int) c.getScor());

                ps.setString(12, c.getReview());
                ps.addBatch();
            }

            int[] res = ps.executeBatch();
            conn.commit();

            for (int r : res)
                if (r > 0 || r == Statement.SUCCESS_NO_INFO) inserted++;

        } catch (Exception e) {
            try { conn.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException("Import books failed: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ignored) {}
        }

        return inserted;
    }

    /**
     * Metoda ce are ca rol exportul datelor din baza de date in fisier CSV pentru datele referitoare la carti
     * @param path String, calea catre fisierul de export
     * @return int: numarul de carti exportate, sau ) daca nu se exporta
     */
    public static int exportBooksToCSV(String path) {
        try {
            List<Carte> carte = BookSelectService.fetchBooksFromDB();
            ArrayList<Carte> carti= new ArrayList<>(carte);
            exportCSV(path, carti);
            return carti.size();
        } catch (Exception e) {
            throw new RuntimeException("Export books failed: " + e.getMessage(), e);
        }
    }

    /**
     * Metoda care are ca rol exportul datelor legate de intrarile in jurnal din baza de date in fisiere JSON
     * @param path String; calea catre fisierul de export
     * @param conn Connection; conexiunea cu baza de date
     * @return int: numarul de intrari in jurnal care au fost exportate sau 0 daca exportul a esuat
     */
    public static int exportReadingLogToJSON(String path, Connection conn) {
        ArrayList<Jurnal> jurnal = new ArrayList<>();
        String sql = """
            SELECT id, book_id, data, pagina_curenta
            FROM public.reading_log
            ORDER BY book_id, data, id
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int idCarte = rs.getInt("book_id");
                Date dataSql = rs.getDate("data");
                LocalDate data = dataSql == null ? null : dataSql.toLocalDate();
                int paginaCurenta = rs.getInt("pagina_curenta");

                jurnal.add(new Jurnal(id, idCarte, data, paginaCurenta));
            }
        } catch (Exception e) {
            throw new RuntimeException("Export reading_log failed: " + e.getMessage(), e);
        }

        exportJSON(path, jurnal);
        return jurnal.size();
    }

}
