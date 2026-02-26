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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Teste pentru logica de progres la citire si jurnalizarea paginilor.
 * Foloseste conexiuni si statement-uri false pentru a simula DB.
 */
public class UpdateProgressServiceTest {

    /**
     * Verifica setarea statusului si a datei de start la inceputul lecturii.
     */
    @Test
    @DisplayName("Test 1 - readingProgress sets start and status")
    public void test1() {
        FakeDb db = new FakeDb(0);
        Carte c = new Carte(1, "T", "A", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, "");

        UpdateProgressService.readingProgress(c, 10, db.conn);

        assertEquals(Status.IN_CURS_DE_CITIRE, c.getStatus());
        assertEquals(LocalDate.now(), c.getDataStart());
        assertEquals(0.1, c.getProcent(), 1e-9);
        assertEquals(10, db.insertPs.params.get(3));
        assertEquals(10, db.insertPs.params.get(4));
    }

    /**
     * Verifica marcarea cartii ca finalizata si setarea datei de finish.
     */
    @Test
    @DisplayName("Test 2 - readingProgress finishes book")
    public void test2() {
        FakeDb db = new FakeDb(50);
        Carte c = new Carte(2, "T2", "A2", "G2", Format.EBOOK, 100, "", Status.IN_CURS_DE_CITIRE, 0.5,
                LocalDate.of(2025, 1, 1), null, 0.0, "");

        UpdateProgressService.readingProgress(c, 100, db.conn);

        assertEquals(Status.FINALIZATA, c.getStatus());
        assertEquals(LocalDate.now(), c.getDataFinish());
        assertEquals(1.0, c.getProcent(), 1e-9);
        assertEquals(50, db.insertPs.params.get(4));
    }

    /**
     * Verifica aruncarea unei exceptii pentru pagina invalida.
     */
    @Test
    @DisplayName("Test 3 - readingProgress invalid page throws")
    public void test3() {
        FakeDb db = new FakeDb(0);
        Carte c = new Carte(3, "T3", "A3", "G3", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, "");

        assertThrows(RuntimeException.class, () -> UpdateProgressService.readingProgress(c, -1, db.conn));
    }

    /**
     * Verifica parametrii folositi la insertul in reading_log.
     * @throws Exception daca apar erori la reflectie
     */
    @Test
    @DisplayName("Test 4 - insertReadingLog sets params")
    public void test4() throws Exception {
        FakeDb db = new FakeDb(0);
        invokePrivateInsert(db.conn, 10, LocalDate.of(2025, 1, 2), 20, 5);

        assertEquals(10, db.insertPs.params.get(1));
        assertEquals(Date.valueOf(LocalDate.of(2025, 1, 2)), db.insertPs.params.get(2));
        assertEquals(20, db.insertPs.params.get(3));
        assertEquals(5, db.insertPs.params.get(4));
        assertEquals(1, db.insertPs.executeUpdateCount);
    }

    /**
     * Verifica acceptarea valorilor 0 pentru pagini.
     * @throws Exception daca apar erori la reflectie
     */
    @Test
    @DisplayName("Test 5 - insertReadingLog allows zero pages")
    public void test5() throws Exception {
        FakeDb db = new FakeDb(0);
        invokePrivateInsert(db.conn, 1, LocalDate.of(2025, 2, 2), 0, 0);

        assertEquals(0, db.insertPs.params.get(3));
        assertEquals(0, db.insertPs.params.get(4));
    }

    /**
     * Verifica folosirea id-ului de carte la insert.
     * @throws Exception daca apar erori la reflectie
     */
    @Test
    @DisplayName("Test 6 - insertReadingLog uses book id")
    public void test6() throws Exception {
        FakeDb db = new FakeDb(0);
        invokePrivateInsert(db.conn, 77, LocalDate.of(2025, 3, 3), 5, 5);

        assertEquals(77, db.insertPs.params.get(1));
    }

    /**
     * Verifica pastrarea statusului cand pagina este 0.
     * @throws Exception daca apar erori la reflectie
     */
    @Test
    @DisplayName("Test 7 - updateBookProgress keeps status when pagina 0")
    public void test7() throws Exception {
        FakeDb db = new FakeDb(0);
        Carte c = new Carte(7, "T", "A", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, "");

        invokePrivateUpdate(db.conn, c, 0);

        assertEquals(Status.NECITITA, c.getStatus());
        assertEquals(0.0, c.getProcent(), 1e-9);
        assertNull(c.getDataStart());
        assertNull(db.updatePs.params.get(3));
    }

    /**
     * Verifica setarea statusului IN_CURS_DE_CITIRE si a datei de start.
     * @throws Exception daca apar erori la reflectie
     */
    @Test
    @DisplayName("Test 8 - updateBookProgress sets start and in progress")
    public void test8() throws Exception {
        FakeDb db = new FakeDb(0);
        Carte c = new Carte(8, "T", "A", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, "");

        invokePrivateUpdate(db.conn, c, 10);

        assertEquals(Status.IN_CURS_DE_CITIRE, c.getStatus());
        assertEquals(LocalDate.now(), c.getDataStart());
        assertEquals(0.1, c.getProcent(), 1e-9);
    }

    /**
     * Verifica finalizarea cartii la atingerea ultimei pagini.
     * @throws Exception daca apar erori la reflectie
     */
    @Test
    @DisplayName("Test 9 - updateBookProgress finishes book")
    public void test9() throws Exception {
        FakeDb db = new FakeDb(0);
        Carte c = new Carte(9, "T", "A", "G", Format.PAPERBACK, 50, "", Status.IN_CURS_DE_CITIRE, 0.5,
                LocalDate.of(2025, 1, 1), null, 0.0, "");

        invokePrivateUpdate(db.conn, c, 50);

        assertEquals(Status.FINALIZATA, c.getStatus());
        assertEquals(LocalDate.now(), c.getDataFinish());
        assertEquals(1.0, c.getProcent(), 1e-9);
        assertNotNull(db.updatePs.params.get(4));
    }

    /**
     * Verifica returnarea ultimei pagini citite din DB.
     * @throws Exception daca apar erori la reflectie
     */
    @Test
    @DisplayName("Test 10 - fetchLastPaginaCurenta returns value")
    public void test10() throws Exception {
        FakeDb db = new FakeDb(33);
        int res = invokePrivateFetch(db.conn, 1);
        assertEquals(33, res);
    }

    /**
     * Verifica returnarea 0 cand nu exista inregistrari.
     * @throws Exception daca apar erori la reflectie
     */
    @Test
    @DisplayName("Test 11 - fetchLastPaginaCurenta empty returns 0")
    public void test11() throws Exception {
        FakeDb db = new FakeDb(null);
        int res = invokePrivateFetch(db.conn, 1);
        assertEquals(0, res);
    }

    /**
     * Verifica returnarea 0 cand valoarea este negativa.
     * @throws Exception daca apar erori la reflectie
     */
    @Test
    @DisplayName("Test 12 - fetchLastPaginaCurenta negative returns 0")
    public void test12() throws Exception {
        FakeDb db = new FakeDb(-5);
        int res = invokePrivateFetch(db.conn, 1);
        assertEquals(0, res);
    }

    /**
     * Apeleaza metoda privata insertReadingLog prin reflectie.
     * @param conn conexiunea folosita
     * @param bookId id-ul cartii
     * @param data data citirii
     * @param pagina pagina curenta
     * @param paginiCitite pagini citite la aceasta intrare
     * @throws Exception daca apar erori la reflectie
     */
    private static void invokePrivateInsert(Connection conn, int bookId, LocalDate data, int pagina, int paginiCitite) throws Exception {
        Method m = UpdateProgressService.class.getDeclaredMethod(
                "insertReadingLog", Connection.class, int.class, LocalDate.class, int.class, int.class);
        m.setAccessible(true);
        m.invoke(null, conn, bookId, data, pagina, paginiCitite);
    }

    /**
     * Apeleaza metoda privata updateBookProgress prin reflectie.
     * @param conn conexiunea folosita
     * @param c cartea actualizata
     * @param pagina pagina curenta
     * @throws Exception daca apar erori la reflectie
     */
    private static void invokePrivateUpdate(Connection conn, Carte c, int pagina) throws Exception {
        Method m = UpdateProgressService.class.getDeclaredMethod(
                "updateBookProgress", Connection.class, Carte.class, int.class);
        m.setAccessible(true);
        m.invoke(null, conn, c, pagina);
    }

    /**
     * Apeleaza metoda privata fetchLastPaginaCurenta prin reflectie.
     * @param conn conexiunea folosita
     * @param bookId id-ul cartii
     * @return ultima pagina citita
     * @throws Exception daca apar erori la reflectie
     */
    private static int invokePrivateFetch(Connection conn, int bookId) throws Exception {
        Method m = UpdateProgressService.class.getDeclaredMethod(
                "fetchLastPaginaCurenta", Connection.class, int.class);
        m.setAccessible(true);
        return (int) m.invoke(null, conn, bookId);
    }

    /**
     * Container simplu pentru conexiune si statement-uri inregistrate.
     */
    private static final class FakeDb {
        private final RecordingPreparedStatement selectPs;
        private final RecordingPreparedStatement insertPs;
        private final RecordingPreparedStatement updatePs;
        private final Connection conn;

        /**
         * Construieste o baza de date falsa cu ultima pagina presetata.
         * @param lastPage ultima pagina citita simulata
         */
        private FakeDb(Integer lastPage) {
            this.selectPs = new RecordingPreparedStatement(resultSetSingleInt(lastPage));
            this.insertPs = new RecordingPreparedStatement(null);
            this.updatePs = new RecordingPreparedStatement(null);
            this.conn = connectionForSql(Map.of(
                    "SELECT pagina_curenta", selectPs.proxy(),
                    "INSERT INTO public.reading_log", insertPs.proxy(),
                    "UPDATE public.books", updatePs.proxy()
            ));
        }
    }

    /**
     * Creeaza o conexiune proxy ce returneaza statement-uri in functie de SQL.
     * @param byContains map de fragmente SQL catre statement-uri
     * @return conexiune proxy
     */
    private static Connection connectionForSql(Map<String, PreparedStatement> byContains) {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("prepareStatement".equals(name) && args != null && args.length > 0) {
                String sql = (String) args[0];
                for (Map.Entry<String, PreparedStatement> entry : byContains.entrySet()) {
                    if (sql.contains(entry.getKey())) {
                        return entry.getValue();
                    }
                }
                throw new IllegalArgumentException("Unexpected SQL: " + sql);
            }
            if ("setAutoCommit".equals(name) || "commit".equals(name) || "rollback".equals(name) || "close".equals(name)) {
                return null;
            }
            return defaultValue(method.getReturnType());
        };
        return (Connection) Proxy.newProxyInstance(
                UpdateProgressServiceTest.class.getClassLoader(),
                new Class[]{Connection.class},
                handler
        );
    }

    /**
     * Creeaza un ResultSet fals care expune un singur int.
     * @param value valoarea simulata
     * @return result set proxy
     */
    private static ResultSet resultSetSingleInt(Integer value) {
        InvocationHandler handler = new InvocationHandler() {
            boolean done = false;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                String name = method.getName();
                if ("next".equals(name)) {
                    if (done || value == null) return false;
                    done = true;
                    return true;
                }
                if ("getInt".equals(name)) {
                    return value == null ? 0 : (int) value;
                }
                if ("close".equals(name)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            }
        };
        return (ResultSet) Proxy.newProxyInstance(
                UpdateProgressServiceTest.class.getClassLoader(),
                new Class[]{ResultSet.class},
                handler
        );
    }

    /**
     * PreparedStatement fals care retine parametrii si poate returna un ResultSet.
     */
    private static final class RecordingPreparedStatement {
        private final Map<Integer, Object> params = new HashMap<>();
        private final ResultSet rs;
        private int executeUpdateCount = 0;

        /**
         * Initializeaza statement-ul fals cu un ResultSet optional.
         * @param rs result set simulat
         */
        private RecordingPreparedStatement(ResultSet rs) {
            this.rs = rs;
        }

        /**
         * Creeaza un proxy PreparedStatement care inregistreaza parametrii.
         * @return proxy PreparedStatement
         */
        private PreparedStatement proxy() {
            InvocationHandler handler = (proxy, method, args) -> {
                String name = method.getName();
                if ("setInt".equals(name) || "setString".equals(name) || "setDouble".equals(name)
                        || "setDate".equals(name)) {
                    params.put((Integer) args[0], args[1]);
                    return null;
                }
                if ("setNull".equals(name)) {
                    params.put((Integer) args[0], null);
                    return null;
                }
                if ("executeQuery".equals(name)) {
                    return rs;
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
                    UpdateProgressServiceTest.class.getClassLoader(),
                    new Class[]{PreparedStatement.class},
                    handler
            );
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
