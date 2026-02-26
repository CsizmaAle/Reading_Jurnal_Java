package Baza_de_date;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clasa ce are ca rol legatura cu baza de date
 */
public final class BDConnectionManager {
    private static final String URL  = System.getenv().getOrDefault("DB_URL", "...");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "...");
    private static final String PASS = System.getenv().getOrDefault("DB_PASS", "...");




    /**
     * Constructor fara parametrii
     */
    private void DBConnectionManager() {}

    /**
     * Metoda ce obtine conexiunea cu baza de date
     * @return Connection, obiect care reprezinta conexiunea cu baza de date
     * @throws SQLException arunca exceptie in cazul in care conexiunea cu baza de date esueaza
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
