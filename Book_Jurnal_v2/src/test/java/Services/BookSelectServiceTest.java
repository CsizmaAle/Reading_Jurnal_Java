package Services;

import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste pentru selectia cartilor din baza de date, inclusiv maparea campurilor.
 * Foloseste un driver JDBC fals pentru a simula rezultate.
 */
public class BookSelectServiceTest {

    /**
     * Verifica filtrarea dupa status si lista goala.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 1 - BookFilter sets status param and returns empty list")
    public void test1() throws Exception {
        FakeDriver driver = new FakeDriver(resultSetFromRows(new ArrayList<>()));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            ArrayList<Carte> res = BookSelectService.BookFilter(Status.NECITITA);
            assertNotNull(res);
            assertEquals(0, res.size());
        }
        assertEquals("NECITITA", driver.lastParams.get(1));
    }

    /**
     * Verifica maparea corecta a unei singure carti.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 2 - BookFilter maps one row")
    public void test2() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("titlu", "Ion");
        row.put("autor", "Liviu Rebreanu");
        row.put("gen", "Roman");
        row.put("tip", "PAPERBACK");
        row.put("pagini_totale", 400);
        row.put("status", "FINALIZATA");
        row.put("imagine", "ion.jpg");
        row.put("procent", 1.0);
        row.put("data_start", Date.valueOf(LocalDate.of(2025, 1, 1)));
        row.put("data_finish", Date.valueOf(LocalDate.of(2025, 1, 10)));
        row.put("scor", 5);
        row.put("review", "ok");
        rows.add(row);

        FakeDriver driver = new FakeDriver(resultSetFromRows(rows));
        ArrayList<Carte> res;
        try (DriverSwap ignored = new DriverSwap(driver)) {
            res = BookSelectService.BookFilter(Status.FINALIZATA);
        }

        assertEquals(1, res.size());
        Carte c = res.get(0);
        assertEquals(1, c.getId());
        assertEquals("Ion", c.getTitlu());
        assertEquals(Format.PAPERBACK, c.getTip());
        assertEquals(Status.FINALIZATA, c.getStatus());
    }

    /**
     * Verifica valorile implicite cand campurile sunt null.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 3 - BookFilter defaults for null fields")
    public void test3() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 2);
        row.put("titlu", "T");
        row.put("autor", "A");
        row.put("gen", "G");
        row.put("tip", null);
        row.put("pagini_totale", 100);
        row.put("status", null);
        row.put("imagine", "");
        row.put("procent", 0.0);
        row.put("data_start", null);
        row.put("data_finish", null);
        row.put("scor", null);
        row.put("review", "");
        rows.add(row);

        FakeDriver driver = new FakeDriver(resultSetFromRows(rows));
        ArrayList<Carte> res;
        try (DriverSwap ignored = new DriverSwap(driver)) {
            res = BookSelectService.BookFilter(Status.NECITITA);
        }

        assertEquals(1, res.size());
        Carte c = res.get(0);
        assertEquals(Format.PAPERBACK, c.getTip());
        assertEquals(Status.NECITITA, c.getStatus());
        assertEquals(0, c.getScor());
    }

    /**
     * Driver JDBC fals care ofera un ResultSet predefinit.
     */
    private static final class FakeDriver implements Driver {
        private final ResultSet rs;
        private final Map<Integer, Object> lastParams = new HashMap<>();

        /**
         * Initializeaza driverul fals cu un ResultSet predefinit.
         * @param rs result set simulat
         */
        private FakeDriver(ResultSet rs) {
            this.rs = rs;
        }

        /**
         * Creeaza o conexiune proxy care returneaza statement-uri pentru test.
         * @param url URL-ul JDBC
         * @param info proprietatile driverului
         * @return conexiune proxy pentru test
         */
        @Override
        public Connection connect(String url, Properties info) {
            InvocationHandler handler = (proxy, method, args) -> {
                String name = method.getName();
                if ("prepareStatement".equals(name)) {
                    return preparedStatementProxy(rs, lastParams);
                }
                if ("close".equals(name)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            };
            return (Connection) Proxy.newProxyInstance(
                    BookSelectServiceTest.class.getClassLoader(),
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
     * Creeaza un PreparedStatement fals care seteaza parametri si returneaza rs.
     * @param rs result set simulat
     * @param params map de parametri setati
     * @return prepared statement proxy
     */
    private static PreparedStatement preparedStatementProxy(ResultSet rs, Map<Integer, Object> params) {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("setString".equals(name) || "setInt".equals(name)) {
                params.put((Integer) args[0], args[1]);
                return null;
            }
            if ("executeQuery".equals(name)) {
                return rs;
            }
            if ("close".equals(name)) {
                return null;
            }
            return defaultValue(method.getReturnType());
        };
        return (PreparedStatement) Proxy.newProxyInstance(
                BookSelectServiceTest.class.getClassLoader(),
                new Class[]{PreparedStatement.class},
                handler
        );
    }

    /**
     * Creeaza un ResultSet fals dintr-o lista de randuri.
     * @param rows lista de randuri simulate
     * @return result set proxy
     */
    private static ResultSet resultSetFromRows(List<Map<String, Object>> rows) {
        InvocationHandler handler = new InvocationHandler() {
            int idx = -1;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                String name = method.getName();
                if ("next".equals(name)) {
                    idx++;
                    return idx < rows.size();
                }
                if (idx < 0 || idx >= rows.size()) {
                    return defaultValue(method.getReturnType());
                }
                Map<String, Object> row = rows.get(idx);
                if ("getInt".equals(name)) {
                    Object v = row.get(args[0]);
                    return v == null ? 0 : ((Number) v).intValue();
                }
                if ("getString".equals(name)) {
                    Object v = row.get(args[0]);
                    return v == null ? null : v.toString();
                }
                if ("getDouble".equals(name)) {
                    Object v = row.get(args[0]);
                    return v == null ? 0.0 : ((Number) v).doubleValue();
                }
                if ("getDate".equals(name)) {
                    return row.get(args[0]);
                }
                if ("getObject".equals(name)) {
                    return row.get(args[0]);
                }
                if ("close".equals(name)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            }
        };
        return (ResultSet) Proxy.newProxyInstance(
                BookSelectServiceTest.class.getClassLoader(),
                new Class[]{ResultSet.class},
                handler
        );
    }

    /**
     * Inlocuieste temporar driver-ele JDBC cu un driver de test.
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
