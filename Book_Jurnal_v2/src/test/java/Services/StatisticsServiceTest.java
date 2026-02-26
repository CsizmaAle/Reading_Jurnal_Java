package Services;

import Modele_de_date.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste pentru calculele statistice legate de carti si jurnal.
 * Foloseste un driver JDBC fals si result set-uri simulate.
 */
public class StatisticsServiceTest {

    /**
     * Verifica numararea cartilor cand statusul este null.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 1 - countBooksBasedOnStatus null returns count")
    public void test1() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(List.of(row("1", 5)))));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            int res = StatisticiService.countBooksBasedOnStatus(null);
            assertEquals(5, res);
        }
    }

    /**
     * Verifica setarea parametrului status in query.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 2 - countBooksBasedOnStatus sets status param")
    public void test2() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(List.of(row("1", 2)))));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            int res = StatisticiService.countBooksBasedOnStatus(Status.FINALIZATA);
            assertEquals(2, res);
        }
        assertEquals("FINALIZATA", driver.lastParams.get(1));
    }

    /**
     * Verifica rezultatul 0 cand query-ul nu intoarce randuri.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 3 - countBooksBasedOnStatus empty result returns 0")
    public void test3() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(new ArrayList<>())));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            int res = StatisticiService.countBooksBasedOnStatus(Status.NECITITA);
            assertEquals(0, res);
        }
    }

    /**
     * Verifica procentul cand totalul este 0.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 4 - procentCitite total 0 returns 0")
    public void test4() throws Exception {
        List<ResultSet> results = List.of(
                resultSetFromRows(List.of(row("1", 0))),
                resultSetFromRows(List.of(row("1", 0)))
        );
        FakeDriver driver = new FakeDriver(results);
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(0.0, StatisticiService.procentCitite(), 1e-9);
        }
    }

    /**
     * Verifica calculul procentului de carti citite.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 5 - procentCitite computes percentage")
    public void test5() throws Exception {
        List<ResultSet> results = List.of(
                resultSetFromRows(List.of(row("1", 10))),
                resultSetFromRows(List.of(row("1", 4)))
        );
        FakeDriver driver = new FakeDriver(results);
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(40.0, StatisticiService.procentCitite(), 1e-9);
        }
    }

    /**
     * Verifica cazul in care procentul este 100%.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 6 - procentCitite 100 percent")
    public void test6() throws Exception {
        List<ResultSet> results = List.of(
                resultSetFromRows(List.of(row("1", 1))),
                resultSetFromRows(List.of(row("1", 1)))
        );
        FakeDriver driver = new FakeDriver(results);
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(100.0, StatisticiService.procentCitite(), 1e-9);
        }
    }

    /**
     * Verifica media paginilor cand totalul este 0.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 7 - mediePagini zero rows returns 0")
    public void test7() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(List.of(row("cnt", 0, "total", 0)))));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(0, StatisticiService.mediePagini());
        }
    }

    /**
     * Verifica calculul mediei de pagini.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 8 - mediePagini average calc")
    public void test8() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(List.of(row("cnt", 2, "total", 50)))));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(25, StatisticiService.mediePagini());
        }
    }

    /**
     * Verifica media paginilor pentru rezultat gol.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 9 - mediePagini empty result returns 0")
    public void test9() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(new ArrayList<>())));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(0, StatisticiService.mediePagini());
        }
    }

    /**
     * Verifica setul gol de zile citite.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 10 - getReadingDays empty")
    public void test10() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(new ArrayList<>())));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Set<Integer> days = StatisticiService.getReadingDays(2025, 1);
            assertEquals(0, days.size());
        }
    }

    /**
     * Verifica maparea corecta a doua zile distincte.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 11 - getReadingDays two days")
    public void test11() throws Exception {
        List<Map<String, Object>> rows = List.of(row("day", 1), row("day", 15));
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Set<Integer> days = StatisticiService.getReadingDays(2025, 1);
            assertTrue(days.contains(1));
            assertTrue(days.contains(15));
        }
    }

    /**
     * Verifica eliminarea duplicatelor in setul de zile.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 12 - getReadingDays unique set")
    public void test12() throws Exception {
        List<Map<String, Object>> rows = List.of(row("day", 1), row("day", 1), row("day", 2));
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Set<Integer> days = StatisticiService.getReadingDays(2025, 1);
            assertEquals(2, days.size());
        }
    }

    /**
     * Verifica map-ul gol pentru pagini pe zi.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 13 - getPagesPerDay empty")
    public void test13() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(new ArrayList<>())));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Map<Integer, Integer> pages = StatisticiService.getPagesPerDay(2025, 1);
            assertEquals(0, pages.size());
        }
    }

    /**
     * Verifica maparea corecta pentru doua zile.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 14 - getPagesPerDay two rows")
    public void test14() throws Exception {
        List<Map<String, Object>> rows = List.of(
                row("day", 1, "total", 10),
                row("day", 2, "total", 5)
        );
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Map<Integer, Integer> pages = StatisticiService.getPagesPerDay(2025, 1);
            assertEquals(10, pages.get(1));
            assertEquals(5, pages.get(2));
        }
    }

    /**
     * Verifica maparea corecta pentru un singur rand.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 15 - getPagesPerDay one row")
    public void test15() throws Exception {
        List<Map<String, Object>> rows = List.of(row("day", 10, "total", 20));
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Map<Integer, Integer> pages = StatisticiService.getPagesPerDay(2025, 1);
            assertEquals(1, pages.size());
            assertEquals(20, pages.get(10));
        }
    }

    /**
     * Verifica map-ul gol pentru carti finalizate pe luni.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 16 - getFinishedBooksPerMonth empty")
    public void test16() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(new ArrayList<>())));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Map<Integer, Integer> res = StatisticiService.getFinishedBooksPerMonth(2025);
            assertEquals(0, res.size());
        }
    }

    /**
     * Verifica maparea pe luni pentru doua randuri.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 17 - getFinishedBooksPerMonth two rows")
    public void test17() throws Exception {
        List<Map<String, Object>> rows = List.of(
                row("month", 1, "total", 2),
                row("month", 2, "total", 1)
        );
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Map<Integer, Integer> res = StatisticiService.getFinishedBooksPerMonth(2025);
            assertEquals(2, res.get(1));
            assertEquals(1, res.get(2));
        }
    }

    /**
     * Verifica maparea pe luni pentru un singur rand.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 18 - getFinishedBooksPerMonth one row")
    public void test18() throws Exception {
        List<Map<String, Object>> rows = List.of(row("month", 12, "total", 4));
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Map<Integer, Integer> res = StatisticiService.getFinishedBooksPerMonth(2025);
            assertEquals(4, res.get(12));
        }
    }

    /**
     * Verifica map-ul gol pentru carti finalizate pe ani.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 19 - getFinishedBooksPerYear empty")
    public void test19() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(new ArrayList<>())));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Map<Integer, Integer> res = StatisticiService.getFinishedBooksPerYear();
            assertEquals(0, res.size());
        }
    }

    /**
     * Verifica maparea pe ani pentru doua randuri.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 20 - getFinishedBooksPerYear two rows")
    public void test20() throws Exception {
        List<Map<String, Object>> rows = List.of(
                row("year", 2023, "total", 5),
                row("year", 2024, "total", 1)
        );
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Map<Integer, Integer> res = StatisticiService.getFinishedBooksPerYear();
            assertEquals(5, res.get(2023));
            assertEquals(1, res.get(2024));
        }
    }

    /**
     * Verifica maparea pe ani pentru un singur rand.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 21 - getFinishedBooksPerYear one row")
    public void test21() throws Exception {
        List<Map<String, Object>> rows = List.of(row("year", 2025, "total", 3));
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            Map<Integer, Integer> res = StatisticiService.getFinishedBooksPerYear();
            assertEquals(3, res.get(2025));
        }
    }

    /**
     * Verifica media scorurilor cand avg este null.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 22 - medieScorCartiCitite null avg returns 0")
    public void test22() throws Exception {
        List<Map<String, Object>> rows = List.of(row("avg_score", null));
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(0.0, StatisticiService.medieScorCartiCitite(), 1e-9);
        }
    }

    /**
     * Verifica media scorurilor cand exista valoare.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 23 - medieScorCartiCitite returns avg")
    public void test23() throws Exception {
        List<Map<String, Object>> rows = List.of(row("avg_score", 4.2));
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(rows)));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(4.2, StatisticiService.medieScorCartiCitite(), 1e-9);
        }
    }

    /**
     * Verifica media scorurilor pentru rezultat gol.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 24 - medieScorCartiCitite empty result")
    public void test24() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(new ArrayList<>())));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(0.0, StatisticiService.medieScorCartiCitite(), 1e-9);
        }
    }

    /**
     * Verifica numarul 0 de carti finalizate intr-o luna.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 25 - countFinishedBooksInMonth zero")
    public void test25() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(List.of(row("total", 0)))));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(0, StatisticiService.countFinishedBooksInMonth(2025, 1));
        }
    }

    /**
     * Verifica numarul de carti finalizate intr-o luna.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 26 - countFinishedBooksInMonth returns count")
    public void test26() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(List.of(row("total", 3)))));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(3, StatisticiService.countFinishedBooksInMonth(2025, 2));
        }
    }

    /**
     * Verifica rezultatul 0 pentru rezultat gol.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 27 - countFinishedBooksInMonth empty result")
    public void test27() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(new ArrayList<>())));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(0, StatisticiService.countFinishedBooksInMonth(2025, 3));
        }
    }

    /**
     * Verifica suma 0 a paginilor citite intr-o luna.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 28 - sumPagesReadInMonth zero")
    public void test28() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(List.of(row("total", 0)))));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(0, StatisticiService.sumPagesReadInMonth(2025, 1));
        }
    }

    /**
     * Verifica suma paginilor citite intr-o luna.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 29 - sumPagesReadInMonth returns sum")
    public void test29() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(List.of(row("total", 120)))));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(120, StatisticiService.sumPagesReadInMonth(2025, 2));
        }
    }

    /**
     * Verifica suma 0 cand nu exista randuri.
     * @throws Exception daca apar erori la configurarea driverului fals
     */
    @Test
    @DisplayName("Test 30 - sumPagesReadInMonth empty result")
    public void test30() throws Exception {
        FakeDriver driver = new FakeDriver(List.of(resultSetFromRows(new ArrayList<>())));
        try (DriverSwap ignored = new DriverSwap(driver)) {
            assertEquals(0, StatisticiService.sumPagesReadInMonth(2025, 3));
        }
    }

    /**
     * Construieste un map de rand din perechi cheie/valoare.
     * @param kv perechi cheie/valoare
     * @return map cu valorile setate
     */
    private static Map<String, Object> row(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }

    /**
     * Driver JDBC fals care intoarce pe rand result set-uri predefinite.
     */
    private static final class FakeDriver implements Driver {
        private final Queue<ResultSet> results = new ArrayDeque<>();
        private final Map<Integer, Object> lastParams = new HashMap<>();

        /**
         * Initializeaza driverul fals cu o lista de result set-uri.
         * @param results lista de result set-uri simulate
         */
        private FakeDriver(List<ResultSet> results) {
            this.results.addAll(results);
        }

        /**
         * Creeaza o conexiune proxy care livreaza result set-uri predefinite.
         * @param url URL-ul JDBC
         * @param info proprietatile driverului
         * @return conexiune proxy pentru test
         */
        @Override
        public Connection connect(String url, Properties info) {
            InvocationHandler handler = (proxy, method, args) -> {
                String name = method.getName();
                if ("prepareStatement".equals(name)) {
                    ResultSet rs = results.isEmpty() ? resultSetFromRows(new ArrayList<>()) : results.remove();
                    return preparedStatementProxy(rs, lastParams);
                }
                if ("close".equals(name)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            };
            return (Connection) Proxy.newProxyInstance(
                    StatisticsServiceTest.class.getClassLoader(),
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
            if ("setString".equals(name) || "setInt".equals(name) || "setDate".equals(name)) {
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
                StatisticsServiceTest.class.getClassLoader(),
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
            boolean lastWasNull = false;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                String name = method.getName();
                if ("next".equals(name)) {
                    idx++;
                    return idx < rows.size();
                }
                if ("wasNull".equals(name)) {
                    return lastWasNull;
                }
                if (idx < 0 || idx >= rows.size()) {
                    return defaultValue(method.getReturnType());
                }
                Map<String, Object> row = rows.get(idx);
                Object key = args == null || args.length == 0 ? null : args[0];
                Object v = row.get(String.valueOf(key));
                if ("getInt".equals(name)) {
                    lastWasNull = (v == null);
                    return v == null ? 0 : ((Number) v).intValue();
                }
                if ("getString".equals(name)) {
                    lastWasNull = (v == null);
                    return v == null ? null : v.toString();
                }
                if ("getDouble".equals(name)) {
                    lastWasNull = (v == null);
                    return v == null ? 0.0 : ((Number) v).doubleValue();
                }
                if ("getDate".equals(name)) {
                    lastWasNull = (v == null);
                    return v;
                }
                if ("getObject".equals(name)) {
                    lastWasNull = (v == null);
                    return v;
                }
                if ("close".equals(name)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            }
        };
        return (ResultSet) Proxy.newProxyInstance(
                StatisticsServiceTest.class.getClassLoader(),
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
