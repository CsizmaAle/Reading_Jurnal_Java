package Services;

import Baza_de_date.BDConnectionManager;
import Modele_de_date.Carte;
import java.sql.*;

/**
 * Clasa ce are ca rol realizarea unor operatii legate de carti pe baza de date
 */
public class BookEditService {
    /**
     * Metoda ce sterge o anumita carte din biblioteca in functie de id-ul unei carti
     * @param bookId int; id-ul cartii ce urmeza a fi stearsa
     */
    public static void deleteBook(int bookId) {
        String sql = "DELETE FROM public.books WHERE id = ?";

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Delete book failed: " + e.getMessage(), e);
        }
    }

    /**
     * Metoda ce are ca rol modificarea datelor legate de o carte in functie de ce4 date a introdus utilizatorul
     * @param book Carte; cartea ce se doreste a fi modificata si care contine deja datele introduse de utilizator
     */
    public static void updateBook(Carte book) {
        String sql = """
                UPDATE public.books
                SET titlu = ?,
                    autor = ?,
                    gen = ?,
                    tip = ?,
                    pagini_totale = ?,
                    status = ?,
                    imagine = ?,
                    procent = ?,
                    data_start = ?,
                    data_finish = ?,
                    scor = ?,
                    review = ?,
                    updated_at = now()
                WHERE id = ?
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitlu());
            ps.setString(2, book.getAutor());
            ps.setString(3, book.getGen());
            ps.setString(4, book.getTip() == null ? null : book.getTip().name());
            ps.setInt(5, book.getNumar_pagini_totale());
            ps.setString(6, book.getStatus() == null ? null : book.getStatus().name());
            ps.setString(7, book.getImagine());
            ps.setDouble(8, book.getProcent());

            if (book.getDataStart() == null) {
                ps.setNull(9, Types.DATE);
            } else {
                ps.setDate(9, Date.valueOf(book.getDataStart()));
            }

            if (book.getDataFinish() == null) {
                ps.setNull(10, Types.DATE);
            } else {
                ps.setDate(10, Date.valueOf(book.getDataFinish()));
            }

            if (book.getScor() <= 0) {
                ps.setNull(11, Types.INTEGER);
            } else {
                ps.setInt(11, (int) book.getScor());
            }

            ps.setString(12, book.getReview());
            ps.setInt(13, book.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Update book failed: " + e.getMessage(), e);
        }
    }

    /**
     * Metoda ce eeste responsabila de adaugarea unei noi cartio in biblioteca
     * @param book Carte; obiect de tip carte ce urmeaza afi adaugat in biblioteca
     */
    public static void addBook(Carte book) {
        String sql = """
                insert into public.books (titlu, autor, gen, tip, pagini_totale, status, imagine,
                    procent, data_start, data_finish, scor, review)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = BDConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, book.getTitlu());
                ps.setString(2, book.getAutor());
                ps.setString(3, book.getGen());
                ps.setString(4, book.getTip() == null ? null : book.getTip().name());
                ps.setInt(5, book.getNumar_pagini_totale());
                ps.setString(6, book.getStatus() == null ? null : book.getStatus().name());
                ps.setString(7, book.getImagine());
                ps.setDouble(8, book.getProcent());

                if (book.getDataStart() == null)
                    ps.setNull(9, Types.DATE);
                 else
                    ps.setDate(9, Date.valueOf(book.getDataStart()));

                if (book.getDataFinish() == null)
                    ps.setNull(10, Types.DATE);
                 else
                    ps.setDate(10, Date.valueOf(book.getDataFinish()));

                if (book.getScor() <= 0)
                    ps.setNull(11, Types.INTEGER);
                 else
                    ps.setInt(11, (int) book.getScor());

                ps.setString(12, book.getReview());
                ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
