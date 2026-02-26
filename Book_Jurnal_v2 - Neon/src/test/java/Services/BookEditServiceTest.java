package Services;

import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste pentru operatiile de editare carti (add/update/delete).
 * Foloseste un driver JDBC fals pentru a intercepta SQL si parametrii setati.
 */
public class BookEditServiceTest {

    /**
     * Verifica daca deleteBook seteaza id-ul si executa update-ul.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 1 - deleteBook sets id param and executes update")
    public void test1() throws Exception {
        FakeDriver driver = new FakeDriver();
        try (DriverSwap ignored = new DriverSwap(driver)) {
            BookEditService.deleteBook(7);
        }
        RecordingPreparedStatement ps = driver.lastStatement;
        assertNotNull(ps);
        assertEquals(7, ps.params.get(1));
        assertEquals(1, ps.executeUpdateCount);
    }

    /**
     * Verifica daca SQL-ul contine comanda de delete.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 2 - deleteBook uses delete SQL")
    public void test2() throws Exception {
        FakeDriver driver = new FakeDriver();
        try (DriverSwap ignored = new DriverSwap(driver)) {
            BookEditService.deleteBook(1);
        }
        assertNotNull(driver.lastSql);
        assertTrue(driver.lastSql.contains("DELETE FROM public.books"));
    }

    /**
     * Verifica daca deleteBook accepta id=0 fara exceptii.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 3 - deleteBook accepts id 0")
    public void test3() throws Exception {
        FakeDriver driver = new FakeDriver();
        try (DriverSwap ignored = new DriverSwap(driver)) {
            BookEditService.deleteBook(0);
        }
        RecordingPreparedStatement ps = driver.lastStatement;
        assertEquals(0, ps.params.get(1));
        assertEquals(1, ps.executeUpdateCount);
    }

    /**
     * Verifica setarea null pentru date si scor cand lipsesc.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 4 - updateBook sets null dates and score when missing")
    public void test4() throws Exception {
        FakeDriver driver = new FakeDriver();
        Carte book = new Carte(10, "T", "A", "G", Format.PAPERBACK, 100, "img", Status.NECITITA,
                0.2, null, null, 0.0, "rev");

        try (DriverSwap ignored = new DriverSwap(driver)) {
            BookEditService.updateBook(book);
        }

        RecordingPreparedStatement ps = driver.lastStatement;
        assertNull(ps.params.get(9));
        assertNull(ps.params.get(10));
        assertNull(ps.params.get(11));
        assertEquals(10, ps.params.get(13));
    }

    /**
     * Verifica setarea corecta a datelor si scorului la update.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 5 - updateBook sets dates and score")
    public void test5() throws Exception {
        FakeDriver driver = new FakeDriver();
        Carte book = new Carte(11, "T2", "A2", "G2", Format.HARDCOVER, 250, "img2", Status.FINALIZATA, 1.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10), 4.5, "ok");

        try (DriverSwap ignored = new DriverSwap(driver)) {
            BookEditService.updateBook(book);
        }

        RecordingPreparedStatement ps = driver.lastStatement;
        assertEquals(Date.valueOf(LocalDate.of(2025, 1, 1)), ps.params.get(9));
        assertEquals(Date.valueOf(LocalDate.of(2025, 1, 10)), ps.params.get(10));
        assertEquals(4, ps.params.get(11));
        assertEquals("FINALIZATA", ps.params.get(6));
    }

    /**
     * Verifica acceptarea valorilor null pentru tip si status.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 6 - updateBook allows null tip/status")
    public void test6() throws Exception {
        FakeDriver driver = new FakeDriver();
        Carte book = new Carte(12, "T3", "A3", "G3", Format.PAPERBACK, 120, "img3", Status.NECITITA, 0.5, LocalDate.of(2025, 2, 2), null, 2.0, "");
        book.setTip(null);
        book.setStatus(null);

        try (DriverSwap ignored = new DriverSwap(driver)) {
            BookEditService.updateBook(book);
        }

        RecordingPreparedStatement ps = driver.lastStatement;
        assertNull(ps.params.get(4));
        assertNull(ps.params.get(6));
    }

    /**
     * Verifica setarea null pentru date si scor la addBook.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 7 - addBook sets null dates and score when missing")
    public void test7() throws Exception {
        FakeDriver driver = new FakeDriver();
        Carte book = new Carte(0, "T", "A", "G", Format.EBOOK, 90, "", Status.NECITITA, 0.0, null, null, 0.0, "");

        try (DriverSwap ignored = new DriverSwap(driver)) {
            BookEditService.addBook(book);
        }

        RecordingPreparedStatement ps = driver.lastStatement;
        assertNull(ps.params.get(9));
        assertNull(ps.params.get(10));
        assertNull(ps.params.get(11));
        assertEquals("EBOOK", ps.params.get(4));
    }

    /**
     * Verifica setarea datelor si scorului la addBook.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 8 - addBook sets dates and score")
    public void test8() throws Exception {
        FakeDriver driver = new FakeDriver();
        Carte book = new Carte(0, "T2", "A2", "G2", Format.PAPERBACK, 300, "img", Status.IN_CURS_DE_CITIRE, 0.7, LocalDate.of(2025, 3, 3), LocalDate.of(2025, 3, 10), 5.0, "ok");

        try (DriverSwap ignored = new DriverSwap(driver)) {
            BookEditService.addBook(book);
        }

        RecordingPreparedStatement ps = driver.lastStatement;
        assertEquals(Date.valueOf(LocalDate.of(2025, 3, 3)), ps.params.get(9));
        assertEquals(Date.valueOf(LocalDate.of(2025, 3, 10)), ps.params.get(10));
        assertEquals(5, ps.params.get(11));
    }

    /**
     * Verifica setarea review-ului si procentului.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 9 - addBook sets review and procent")
    public void test9() throws Exception {
        FakeDriver driver = new FakeDriver();
        Carte book = new Carte(0, "T3", "A3", "G3", Format.PAPERBACK, 200, "img", Status.NECITITA, 0.25, null, null, 0.0, "review");

        try (DriverSwap ignored = new DriverSwap(driver)) {
            BookEditService.addBook(book);
        }

        RecordingPreparedStatement ps = driver.lastStatement;
        assertEquals(0.25, (double) ps.params.get(8));
        assertEquals("review", ps.params.get(12));
    }

    /**
     * Driver JDBC fals care captureaza SQL-ul si statement-ul creat.
     */
    private static final class FakeDriver implements Driver {
        private String lastSql;
        private RecordingPreparedStatement lastStatement;

        /**
         * Creeaza o conexiune proxy ce captureaza SQL-ul pregatit.
         * @param url URL-ul JDBC
         * @param info proprietatile driverului
         * @return conexiune proxy pentru test
         */
        @Override
        public Connection connect(String url, Properties info) {
            InvocationHandler handler = (proxy, method, args) -> {
                String name = method.getName();
                if ("prepareStatement".equals(name)) {
                    lastSql = (String) args[0];
                    lastStatement = new RecordingPreparedStatement();
                    return lastStatement.proxy();
                }
                if ("close".equals(name)) return null;
                return defaultValue(method.getReturnType());
            };
            return (Connection) Proxy.newProxyInstance(
                    BookEditServiceTest.class.getClassLoader(),
                    new Class[]{Connection.class},
                    handler
            );
        }

        /**
         * Accepta orice URL pentru driverul fals.
         * @param url URL-ul JDBC
         * @return true pentru orice URL
         */
        @Override
        public boolean acceptsURL(String url) {
            return true;
        }

        /**
         * Nu expune proprietati de driver.
         * @param url URL-ul JDBC
         * @param info proprietatile driverului
         * @return array gol de proprietati
         */
        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
            return new DriverPropertyInfo[0];
        }

        /**
         * Versiunea majora a driverului fals.
         * @return versiunea majora
         */
        @Override
        public int getMajorVersion() {
            return 1;
        }

        /**
         * Versiunea minora a driverului fals.
         * @return versiunea minora
         */
        @Override
        public int getMinorVersion() {
            return 0;
        }

        /**
         * Driverul fals nu este JDBC compliant.
         * @return false
         */
        @Override
        public boolean jdbcCompliant() {
            return false;
        }

        /**
         * Logger implicit pentru driverul fals.
         * @return logger global
         */
        @Override
        public java.util.logging.Logger getParentLogger() {
            return java.util.logging.Logger.getGlobal();
        }
    }

    /**
     * PreparedStatement fals care retine parametrii si numarul de executeUpdate.
     */
    private static final class RecordingPreparedStatement {
        private final Map<Integer, Object> params = new HashMap<>();
        private int executeUpdateCount = 0;

        /**
         * Creeaza un proxy PreparedStatement care inregistreaza parametrii.
         * @return proxy PreparedStatement
         */
        private PreparedStatement proxy() {
            InvocationHandler handler = (proxy, method, args) -> {
                String name = method.getName();
                if ("setInt".equals(name) || "setString".equals(name) || "setDouble".equals(name)
                        || "setDate".equals(name) || "setLong".equals(name)) {
                    params.put((Integer) args[0], args[1]);
                    return null;
                }
                if ("setNull".equals(name)) {
                    params.put((Integer) args[0], null);
                    return null;
                }
                if ("executeUpdate".equals(name)) {
                    executeUpdateCount++;
                    return 1;
                }
                if ("close".equals(name)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            };
            return (PreparedStatement) Proxy.newProxyInstance(
                    BookEditServiceTest.class.getClassLoader(),
                    new Class[]{PreparedStatement.class},
                    handler
            );
        }
    }

    /**
     * Inlocuieste temporar driver-ele JDBC inregistrate cu un driver fals.
     */
    private static final class DriverSwap implements AutoCloseable {
        private final List<Driver> original = new ArrayList<>();
        private final Driver replacement;

        /**
         * Inlocuieste driver-ele JDBC cu un driver de test.
         * @param replacement driverul fals
         * @throws SQLException daca apar erori la inregistrare
         */
        private DriverSwap(Driver replacement) throws SQLException {
            this.replacement = replacement;
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver d = drivers.nextElement();
                original.add(d);
                DriverManager.deregisterDriver(d);
            }
            DriverManager.registerDriver(replacement);
        }

        /**
         * Restaureaza driver-ele JDBC originale.
         * @throws SQLException daca apar erori la deregistrare
         */
        @Override
        public void close() throws SQLException {
            DriverManager.deregisterDriver(replacement);
            for (Driver d : original) {
                DriverManager.registerDriver(d);
            }
        }
    }

    /**
     * Returneaza valori implicite pentru tipuri primitive la proxy.
     * @param returnType tipul cerut
     * @return valoarea implicita pentru tipul dat
     */
    private static Object defaultValue(Class<?> returnType) {
        if (returnType == void.class) return null;
        if (returnType == boolean.class) return false;
        if (returnType == byte.class) return (byte) 0;
        if (returnType == short.class) return (short) 0;
        if (returnType == int.class) return 0;
        if (returnType == long.class) return 0L;
        if (returnType == float.class) return 0f;
        if (returnType == double.class) return 0.0;
        if (returnType == char.class) return '\0';
        return null;
    }
}
