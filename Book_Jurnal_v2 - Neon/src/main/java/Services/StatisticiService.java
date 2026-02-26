package Services;

import Baza_de_date.BDConnectionManager;
import Modele_de_date.Status;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Clasa de servicii ca are ca rol realizarea de statistici
 */
public class StatisticiService {

    /**
     * Metoda ce are ca rol numararea cartilor ce au un anumit status.
     * @param status Status; statusul pentru care se face numararea
     * @return int; numarul de carti ce au acest status
     */
    public static int countBooksBasedOnStatus(Status status) {
        String sql = status == null
                ? "select count(*) from public.books"
                : "select count(*) from public.books where status = ?";
        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null) {
                ps.setString(1, status.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            System.out.println("WARN: cannot count " + status + " books: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Metoda ce calculeaza procentajul de carti citite din biblioteca
     * @return double; procentajul de carti citite din biblioteca
     */
    public static double procentCitite(){
        int total = countBooksBasedOnStatus(null);
        int read = countBooksBasedOnStatus(Status.FINALIZATA);
        if (total == 0) {
            return 0;
        }
        return (read * 100.0) / total;
    }

    /**
     * metoda ce calculeaza media de pagini citite per numarul de intrari in jurnal
     * @return int; media de pagini citite in functie de intrarile in jurnal
     */
    public static int mediePagini(){
        int nrZile = 0;
        int pagini = 0;
        String sql = """
                SELECT count(*) AS cnt, COALESCE(sum(pagini_citite), 0) AS total
                FROM public.reading_log
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                nrZile = rs.getInt("cnt");
                pagini = rs.getInt("total");
            }

        } catch (Exception e) {
            System.out.println("WARN: Failed to calculate mean(pages): " + e.getMessage());
            return 0;
        }

        if (nrZile == 0) return 0;
        return pagini/nrZile;
    }

    /**
     * Metoda ce selecteaza din baza de date, din reading_log, un set de date calendaristice in care s-au realizat inregistrari
     * @param year int; anul in care s-au realizat inregistrarile. Folosit pentru determinarea datelor
     * @param month int; luna in care s-au realizat inregistrarile. Folosit pentru determinarea datelor
     * @return {@code Set<Integer>}; set de date ce contine datele pentru luna si anul selectate si care sunt unice intre ele
     */
    public static Set<Integer> getReadingDays(int year, int month) {
        Set<Integer> days = new HashSet<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);
        String sql = """
                SELECT DISTINCT EXTRACT(DAY FROM data) AS day
                FROM public.reading_log
                WHERE data >= ? AND data < ?
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    days.add(rs.getInt("day"));
                }
            }
        } catch (Exception e) {
            System.out.println("WARN: Failed to load reading days: " + e.getMessage());
        }

        return days;
    }

    /**
     * Metoda ce are ca rol maparea paginilor citite per zile pentru realizarea de grafice
     * @param year int; anul in care s-au realizat inregistrarile. Folosit pentru determinarea datelor
     * @param month int; luna in care s-au realizat inregistrarile. Folosit pentru determinarea datelor
     * @return {@code Map<Integer, Integer>}; dictionar de date care contine maparea zilei cu numarul de pagini citite
     */
    public static Map<Integer, Integer> getPagesPerDay(int year, int month) {
        Map<Integer, Integer> pages = new LinkedHashMap<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);
        String sql = """
                SELECT EXTRACT(DAY FROM data) AS day, COALESCE(sum(pagini_citite), 0) AS total
                FROM public.reading_log
                WHERE data >= ? AND data < ?
                GROUP BY day
                ORDER BY day
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    pages.put(rs.getInt("day"), rs.getInt("total"));
            }
        } catch (Exception e) {
            System.out.println("WARN: Failed to load pages per day: " + e.getMessage());
        }

        return pages;
    }

    /**
     * Metoda ce mapeaza numarul  de carit finalizate in functie de lunile unui an ales de utilizator
     * @param year int; anul pentru care se cauta date
     * @return {@code Map<Integer, Integer>}; dictionar ce contine luna si numarul de carti finalizate pentru luna respectiva
     */
    public static Map<Integer, Integer> getFinishedBooksPerMonth(int year) {
        Map<Integer, Integer> count = new LinkedHashMap<>();
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);
        String sql = """
                SELECT EXTRACT(MONTH FROM data_finish) AS month, COUNT(*) AS total
                FROM public.books
                WHERE status = ? AND data_finish >= ? AND data_finish < ?
                GROUP BY month
                ORDER BY month
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Status.FINALIZATA.name());
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    count.put(rs.getInt("month"), rs.getInt("total"));
            }
        } catch (Exception e) {
            System.out.println("WARN: Failed to load finished books per month: " + e.getMessage());
        }

        return count;
    }

    /**
     * Metoda ce mapeaza numarul  de carit finalizate in functie de ani, incepand de la prima inregistrare din tabela de carti
     * @return {@code Map<Integer, Integer>}; dictionar ce contine anul si numarul de carti finalizate pentru anul respectiv
     */
    public static Map<Integer, Integer> getFinishedBooksPerYear() {
        Map<Integer, Integer> count = new TreeMap<>();
        String sql = """
                SELECT EXTRACT(YEAR FROM data_finish) AS year, COUNT(*) AS total
                FROM public.books
                WHERE status = ? AND data_finish IS NOT NULL
                GROUP BY year
                ORDER BY year
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Status.FINALIZATA.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    count.put(rs.getInt("year"), rs.getInt("total"));
            }
        } catch (Exception e) {
            System.out.println("WARN: Failed to load finished books per year: " + e.getMessage());
        }

        return count;
    }

    /**
     * Metoda ce calculeaza media scorurilor cartilor citite din biblioteca
     * @return doubel; media cartilor din biblioteca (0-5)
     */
    public static double medieScorCartiCitite() {
        String sql = """
                SELECT AVG(scor) AS avg_score
                FROM public.books
                WHERE status = ? AND scor IS NOT NULL
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Status.FINALIZATA.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_score");
                    return rs.wasNull() ? 0 : avg;
                }
            }
        } catch (Exception e) {
            System.out.println("WARN: Failed to calculate average score: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Metoda ce returneaza numarul de carti citite intr-o luna dintr-un anumit an
     * @param year int; anul pentru care se cauta numarul de carti finalizate
     * @param month int; luna pentru  care se acuta numarul de carti finalizate
     * @return int; numarul de carti citite in luna si anul mentionate
     */
    public static int countFinishedBooksInMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);
        String sql = """
                SELECT COUNT(*) AS total
                FROM public.books
                WHERE status = ? AND data_finish >= ? AND data_finish < ?
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Status.FINALIZATA.name());
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (Exception e) {
            System.out.println("WARN: Failed to count finished books in month: " + e.getMessage());
            return 0;
        }
    }
    /**
     * Metoda ce returneaza numarul de pagini citite intr-o luna dintr-un anumit an
     * @param year int; anul pentru care se calculeaza numarul de pagini citite
     * @param month int; luna pentru  care se calculeaza numarul de pagini citite
     * @return int; numarul de pagini citite in luna si anul mentionate
     */
    public static int sumPagesReadInMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);
        String sql = """
                SELECT COALESCE(SUM(pagini_citite), 0) AS total
                FROM public.reading_log
                WHERE data >= ? AND data < ?
                """;

        try (Connection conn = BDConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (Exception e) {
            System.out.println("WARN: Failed to sum pages in month: " + e.getMessage());
            return 0;
        }
    }
}
