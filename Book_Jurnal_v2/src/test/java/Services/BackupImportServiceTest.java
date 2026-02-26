package Services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste pentru import/export CSV si JSON, cu conexiuni si statement-uri simulate.
 */
public class BackupImportServiceTest {

    /**
     * Verifica importul JSON cand fisierul este gol.
     * @throws Exception daca apar erori la citirea fisierului temporar
     */
    @Test
    @DisplayName("Test 1 - import reading log JSON - empty file returns 0")
    public void test1() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test1-");
        Path json = tempDir.resolve("jurnal.json");
        Files.writeString(json, "[]\n");

        int res = BackupImportService.importReadingLogDinJSON_InDB(json.toString(), null);
        assertEquals(0, res);
    }

    /**
     * Verifica calculul paginilor citite pe baza ultimei intrari.
     * @throws Exception daca apar erori la setup-ul testului
     */
    @Test
    @DisplayName("Test 2 - import reading log JSON - paginiCitite based on previous entry")
    public void test2() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test2-");
        Path json = tempDir.resolve("jurnal.json");
        Files.writeString(json,
                "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"idCarte\": 101,\n" +
                "    \"data\": \"2025-01-02\",\n" +
                "    \"paginaCurenta\": 20\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"idCarte\": 101,\n" +
                "    \"data\": \"2025-01-05\",\n" +
                "    \"paginaCurenta\": 30\n" +
                "  }\n" +
                "]\n");

        BatchStatement insert = new BatchStatement(new int[]{1, 1});
        SelectLastPageStatement select = new SelectLastPageStatement(new HashMap<>());
        Connection conn = connectionForSql(Map.of(
                "INSERT INTO public.reading_log", insert.proxy(),
                "SELECT pagina_curenta", select.proxy()
        ));

        int res = BackupImportService.importReadingLogDinJSON_InDB(json.toString(), conn);
        assertEquals(2, res);
        assertEquals(2, insert.batches.size());
        assertEquals(20, insert.batches.get(0).get(4));
        assertEquals(10, insert.batches.get(1).get(4));
    }

    /**
     * Verifica folosirea ultimei pagini din DB pentru calcul.
     * @throws Exception daca apar erori la setup-ul testului
     */
    @Test
    @DisplayName("Test 3 - import reading log JSON -foloseste ultima paginba din baza de date")
    public void test3() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test3-");
        Path json = tempDir.resolve("jurnal.json");
        Files.writeString(json,
                "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"idCarte\": 200,\n" +
                "    \"data\": \"2025-01-02\",\n" +
                "    \"paginaCurenta\": 60\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"idCarte\": 300,\n" +
                "    \"data\": \"2025-01-03\",\n" +
                "    \"paginaCurenta\": 15\n" +
                "  }\n" +
                "]\n");

        Map<Long, Integer> lastPage = new HashMap<>();
        lastPage.put(200L, 50);
        BatchStatement insert = new BatchStatement(new int[]{1, 1});
        SelectLastPageStatement select = new SelectLastPageStatement(lastPage);
        Connection conn = connectionForSql(Map.of(
                "INSERT INTO public.reading_log", insert.proxy(),
                "SELECT pagina_curenta", select.proxy()
        ));

        int res = BackupImportService.importReadingLogDinJSON_InDB(json.toString(), conn);
        assertEquals(2, res);
        assertEquals(200L, insert.batches.get(0).get(1));
        assertEquals(10, insert.batches.get(0).get(4));
        assertEquals(300L, insert.batches.get(1).get(1));
        assertEquals(15, insert.batches.get(1).get(4));
    }

    /**
     * Verifica importul CSV cand fisierul are doar header.
     * @throws Exception daca apar erori la citirea fisierului temporar
     */
    @Test
    @DisplayName("Test 4 - import books CSV - fisier gol return 0")
    public void test4() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test4-");
        Path csv = tempDir.resolve("carti.csv");
        Files.writeString(csv, "id;titlu;autor;gen;tip;numar_pagini_totale;status;imagine;procent;dataStart;dataFinish;scor;review\n");

        int res = BackupImportService.importCartiCSV_InDB(csv.toString(), null);
        assertEquals(0, res);
    }

    /**
     * Verifica inserarea randurilor si tratarea valorilor null.
     * @throws Exception daca apar erori la setup-ul testului
     */
    @Test
    @DisplayName("Test 5 - import books CSV - inserts rows and handles nulls")
    public void test5() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test5-");
        Path csv = tempDir.resolve("carti.csv");
        Files.writeString(csv,
                "id;titlu;autor;gen;tip;numar_pagini_totale;status;imagine;procent;dataStart;dataFinish;scor;review\n" +
                "1;Ion;Liviu Rebreanu;Roman;PAPERBACK;400;FINALIZATA;ion.jpg;1.0;2025-01-01;2025-01-10;4.5;Foarte buna\n" +
                "2;Test;Autor;Gen;PAPERBACK;;NECITITA;;0.0;;;0;\n");

        BatchStatement insert = new BatchStatement(new int[]{1, 1});
        Connection conn = connectionForSql(Map.of(
                "INSERT INTO public.books", insert.proxy()
        ));

        int res = BackupImportService.importCartiCSV_InDB(csv.toString(), conn);
        assertEquals(2, res);
        assertEquals(2, insert.batches.size());
        assertEquals("Ion", insert.batches.get(0).get(1));
        assertEquals(Date.valueOf(LocalDate.of(2025, 1, 1)), insert.batches.get(0).get(9));
        assertEquals(null, insert.batches.get(1).get(9));
        assertEquals(null, insert.batches.get(1).get(10));
        assertEquals(null, insert.batches.get(1).get(11));
    }

    /**
     * Verifica maparea campurilor din CSV in statement.
     * @throws Exception daca apar erori la setup-ul testului
     */
    @Test
    @DisplayName("Test 6 - import books CSV - sets fields correctly")
    public void test6() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test6-");
        Path csv = tempDir.resolve("carti.csv");
        Files.writeString(csv,
                "id;titlu;autor;gen;tip;numar_pagini_totale;status;imagine;procent;dataStart;dataFinish;scor;review\n" +
                "3;Titlu;Autor;Gen;HARDCOVER;300;IN_CURS_DE_CITIRE;img.png;0.5;2025-02-01;2025-02-10;5.0;ok\n");

        BatchStatement insert = new BatchStatement(new int[]{1});
        Connection conn = connectionForSql(Map.of(
                "INSERT INTO public.books", insert.proxy()
        ));

        int res = BackupImportService.importCartiCSV_InDB(csv.toString(), conn);
        assertEquals(1, res);
        assertEquals("Titlu", insert.batches.get(0).get(1));
        assertEquals("Autor", insert.batches.get(0).get(2));
        assertEquals("HARDCOVER", insert.batches.get(0).get(4));
        assertEquals(300, insert.batches.get(0).get(5));
        assertEquals("IN_CURS_DE_CITIRE", insert.batches.get(0).get(6));
        assertEquals(Date.valueOf(LocalDate.of(2025, 2, 1)), insert.batches.get(0).get(9));
        assertEquals(5, insert.batches.get(0).get(11));
    }

    /**
     * Verifica exportul CSV cu rezultat gol.
     * @throws Exception daca apar erori la citirea fisierului temporar
     */
    @Test
    @DisplayName("Test 7 - export books CSV")
    public void test7() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test7-");
        Path csv = tempDir.resolve("export.csv");

        PreparedStatement ps = queryStatement(resultSetFromRows(new ArrayList<>()));
        Connection conn = connectionForSql(Map.of(
                "FROM public.books", ps
        ));

        int res = BackupImportService.exportBooksToCSV(csv.toString());
        assertEquals(203, res);
        List<String> lines = Files.readAllLines(csv);
        assertEquals(204, lines.size());
    }

    /**
     * Verifica exportul CSV pentru un singur rand.
     * @throws Exception daca apar erori la setup-ul testului
     */
    @Test
    @DisplayName("Test 8 - export books CSV - one row")
    public void test8() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test8-");
        Path csv = tempDir.resolve("export.csv");

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
        row.put("review", "Bun");
        rows.add(row);

        PreparedStatement ps = queryStatement(resultSetFromRows(rows));
        Connection conn = connectionForSql(Map.of(
                "FROM public.books", ps
        ));

        int res = BackupImportService.exportBooksToCSV(csv.toString());
        assertEquals(203, res);
        List<String> lines = Files.readAllLines(csv);
        assertEquals(204, lines.size());
        String[] f = lines.get(1).split(";", -1);
        assertEquals("1", f[0]);
        assertEquals("Papusarul din Cracovia", f[1]);
        assertEquals("FINALIZATA", f[6]);
    }

    /**
     * Verifica exportul JSON pentru rezultat gol.
     * @throws Exception daca apar erori la citirea fisierului temporar
     */
    @Test
    @DisplayName("Test 10 - export reading log JSON - empty result")
    public void test10() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test10-");
        Path json = tempDir.resolve("jurnal.json");

        PreparedStatement ps = queryStatement(resultSetFromRows(new ArrayList<>()));
        Connection conn = connectionForSql(Map.of(
                "FROM public.reading_log", ps
        ));

        int res = BackupImportService.exportReadingLogToJSON(json.toString(), conn);
        assertEquals(0, res);
        String content = Files.readString(json).trim();
        assertTrue(content.startsWith("["));
        assertTrue(content.endsWith("]"));
    }

    /**
     * Verifica exportul JSON pentru o singura intrare.
     * @throws Exception daca apar erori la citirea fisierului temporar
     */
    @Test
    @DisplayName("Test 11 - export reading log JSON - one row")
    public void test11() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test11-");
        Path json = tempDir.resolve("jurnal.json");

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("book_id", 101);
        row.put("data", Date.valueOf(LocalDate.of(2025, 1, 2)));
        row.put("pagina_curenta", 20);
        rows.add(row);

        PreparedStatement ps = queryStatement(resultSetFromRows(rows));
        Connection conn = connectionForSql(Map.of(
                "FROM public.reading_log", ps
        ));

        int res = BackupImportService.exportReadingLogToJSON(json.toString(), conn);
        assertEquals(1, res);
        String content = Files.readString(json);
        assertTrue(content.contains("\"id\": 1"));
        assertTrue(content.contains("\"idCarte\": 101"));
        assertTrue(content.contains("\"data\": \"2025-01-02\""));
        assertTrue(content.contains("\"paginaCurenta\": 20"));
    }

    /**
     * Verifica exportul JSON cand data este null.
     * @throws Exception daca apar erori la citirea fisierului temporar
     */
    @Test
    @DisplayName("Test 12 - export reading log JSON - null date")
    public void test12() throws Exception {
        Path tempDir = Files.createTempDirectory("backup-import-test12-");
        Path json = tempDir.resolve("jurnal.json");

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 2);
        row.put("book_id", 202);
        row.put("data", null);
        row.put("pagina_curenta", 5);
        rows.add(row);

        PreparedStatement ps = queryStatement(resultSetFromRows(rows));
        Connection conn = connectionForSql(Map.of(
                "FROM public.reading_log", ps
        ));

        int res = BackupImportService.exportReadingLogToJSON(json.toString(), conn);
        assertEquals(1, res);
        String content = Files.readString(json);
        assertTrue(content.contains("\"data\": \"null\""));
    }

    /**
     * Creeaza o conexiune proxy ce returneaza statement-uri in functie de SQL.
     * @param byContains map de fragmente SQL catre statement-uri
     * @return conexiune proxy
     */
    private static Connection connectionForSql(Map<String, PreparedStatement> byContains) {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("prepareStatement".equals(name) && args != null && args.length > 0 && args[0] instanceof String) {
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
                BackupImportServiceTest.class.getClassLoader(),
                new Class[]{Connection.class},
                handler
        );
    }

    /**
     * Creeaza un PreparedStatement fals pentru executie de query.
     * @param rs result set simulat
     * @return prepared statement proxy
     */
    private static PreparedStatement queryStatement(ResultSet rs) {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("executeQuery".equals(name)) {
                return rs;
            }
            if ("close".equals(name)) {
                return null;
            }
            return defaultValue(method.getReturnType());
        };
        return (PreparedStatement) Proxy.newProxyInstance(
                BackupImportServiceTest.class.getClassLoader(),
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
                BackupImportServiceTest.class.getClassLoader(),
                new Class[]{ResultSet.class},
                handler
        );
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

    /**
     * PreparedStatement fals care retine batch-uri si parametrii setati.
     */
    private static final class BatchStatement {
        private final Map<Integer, Object> current = new HashMap<>();
        private final int[] executeBatchResult;
        private final List<Map<Integer, Object>> batches = new ArrayList<>();

        /**
         * Initializeaza un batch cu rezultatele dorite pentru executeBatch.
         * @param executeBatchResult rezultatul simulat pentru executeBatch
         */
        private BatchStatement(int[] executeBatchResult) {
            this.executeBatchResult = executeBatchResult;
        }

        /**
         * Creeaza un proxy PreparedStatement pentru operatii de batch.
         * @return prepared statement proxy
         */
        private PreparedStatement proxy() {
            InvocationHandler handler = (proxy, method, args) -> {
                String name = method.getName();
                if ("setLong".equals(name) || "setInt".equals(name) || "setString".equals(name)
                        || "setDouble".equals(name) || "setDate".equals(name)) {
                    current.put((Integer) args[0], args[1]);
                    return null;
                }
                if ("setNull".equals(name)) {
                    current.put((Integer) args[0], null);
                    return null;
                }
                if ("addBatch".equals(name)) {
                    batches.add(new HashMap<>(current));
                    return null;
                }
                if ("executeBatch".equals(name)) {
                    return executeBatchResult;
                }
                if ("close".equals(name)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            };
            return (PreparedStatement) Proxy.newProxyInstance(
                    BackupImportServiceTest.class.getClassLoader(),
                    new Class[]{PreparedStatement.class},
                    handler
            );
        }
    }

    /**
     * Statement fals care simuleaza selectarea ultimei pagini citite.
     */
    private static final class SelectLastPageStatement {
        private final Map<Long, Integer> lastPageByBook;
        private Long lastBookId;

        /**
         * Configureaza maparea dintre bookId si ultima pagina.
         * @param lastPageByBook map de ultime pagini pe carte
         */
        private SelectLastPageStatement(Map<Long, Integer> lastPageByBook) {
            this.lastPageByBook = lastPageByBook;
        }

        /**
         * Creeaza un proxy PreparedStatement ce simuleaza selectul ultimei pagini.
         * @return prepared statement proxy
         */
        private PreparedStatement proxy() {
            InvocationHandler handler = (proxy, method, args) -> {
                String name = method.getName();
                if ("setLong".equals(name)) {
                    lastBookId = (Long) args[1];
                    return null;
                }
                if ("executeQuery".equals(name)) {
                    Integer val = lastPageByBook.get(lastBookId);
                    List<Map<String, Object>> rows = new ArrayList<>();
                    if (val != null) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("1", val);
                        rows.add(row);
                    }
                    return resultSetForSingleColumn(rows);
                }
                if ("close".equals(name)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            };
            return (PreparedStatement) Proxy.newProxyInstance(
                    BackupImportServiceTest.class.getClassLoader(),
                    new Class[]{PreparedStatement.class},
                    handler
            );
        }
    }

    /**
     * Creeaza un ResultSet fals pentru un singur coloana (index 1).
     * @param rows lista de randuri simulate
     * @return result set proxy
     */
    private static ResultSet resultSetForSingleColumn(List<Map<String, Object>> rows) {
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
                if ("getInt".equals(name)) {
                    return ((Number) rows.get(idx).get("1")).intValue();
                }
                if ("close".equals(name)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            }
        };
        return (ResultSet) Proxy.newProxyInstance(
                BackupImportServiceTest.class.getClassLoader(),
                new Class[]{ResultSet.class},
                handler
        );
    }
}
