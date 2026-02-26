package Services;

import Baza_de_date.BDConnectionManager;
import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Status;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa de servicii ce are ca rol selectarea unor carti in functie de status
 */
public class BookSelectService {

    /**
     * Metoda ce selecteaza cartile din baza de date cu un anumit status
     * @param stat Status; statusul pentru care se filtreaza cartie
     * @return {@code ArrayList<Carte>}; lista de carti care indeplinesc acest criteriu de selectie
     */
    public static ArrayList<Carte> BookFilter( Status stat){
        ArrayList<Carte> carte = new ArrayList<>();
        try(Connection conn = BDConnectionManager.getConnection()){
            String sql= """
                SELECT id, titlu, autor, gen, tip, pagini_totale, status, imagine, procent, data_start, data_finish, scor, review 
                FROM public.books
                WHERE status = ?
                """;

            try(PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, stat.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String titlu = rs.getString("titlu");
                        String autor = rs.getString("autor");
                        String gen = rs.getString("gen");
                        String tipText = rs.getString("tip");
                        int paginiTotale = rs.getInt("pagini_totale");
                        String statusText = rs.getString("status");
                        String imagine = rs.getString("imagine");
                        double procent = rs.getDouble("procent");
                        java.sql.Date dataStartSql = rs.getDate("data_start");
                        java.sql.Date dataFinishSql = rs.getDate("data_finish");
                        Integer scor = (Integer) rs.getObject("scor");
                        String review = rs.getString("review");

                        Format tip = tipText == null ? Format.PAPERBACK : Format.valueOf(tipText);
                        Status status = statusText == null ? Status.NECITITA : Status.valueOf(statusText);

                        Carte c = new Carte(id, titlu, autor, gen, tip, paginiTotale, imagine, status, procent, dataStartSql == null ? null : dataStartSql.toLocalDate(), dataFinishSql == null ? null : dataFinishSql.toLocalDate(), scor == null ? 0 : scor, review);
                        carte.add(c);
                    }
                }
            }catch (Exception e) {
                System.out.println("WARN: nu pot filtra cartile: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return carte;

    }

    /**
     * Metoda ce preia toate cartile din baza de date
     * @return {@code List<Carte>} lista cu toate cartile din biblioteca
     */
    public static List<Carte> fetchBooksFromDB() {
        ArrayList<Carte> books = new ArrayList<>();
        String sql = """
                SELECT id, titlu, autor, gen, tip, pagini_totale, status, imagine, procent, data_start, data_finish, scor, review
                FROM public.books
                ORDER BY id
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String titlu = rs.getString("titlu");
                String autor = rs.getString("autor");
                String gen = rs.getString("gen");
                String tipText = rs.getString("tip");
                int paginiTotale = rs.getInt("pagini_totale");
                String statusText = rs.getString("status");
                String imagine = rs.getString("imagine");
                double procent = rs.getDouble("procent");
                Date dataStartSql = rs.getDate("data_start");
                Date dataFinishSql = rs.getDate("data_finish");
                Integer scor = (Integer) rs.getObject("scor");
                String review = rs.getString("review");

                Format tip = tipText == null ? Format.PAPERBACK : Format.valueOf(tipText);
                Status status = statusText == null ? Status.NECITITA : Status.valueOf(statusText);

                Carte carte = new Carte(id, titlu, autor, gen, tip, paginiTotale, imagine, status, procent, dataStartSql == null ? null : dataStartSql.toLocalDate(), dataFinishSql == null ? null : dataFinishSql.toLocalDate(), scor == null ? 0 : scor, review);
                books.add(carte);
            }
        } catch (Exception e) {
            System.out.println("WARN: cannot load books: " + e.getMessage());
        }

        return books;
    }
}
