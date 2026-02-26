package Logica;

import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Jurnal;
import Modele_de_date.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clasa de teste pentru Backup
 */
class BackupTest {

    @Test
    @DisplayName("Test 1 - test import csv - un singur rand")
    public void test1() throws Exception {
        Path tempDir = Files.createTempDirectory("book-jurnal-test1-");
        Path csv = tempDir.resolve("carti.csv");
        Files.writeString(csv, "id;titlu;autor;gen;tip;numar_pagini_totale;status;imagine;procent;dataStart;dataFinish;scor;review\n" +
                "1;Ion;Liviu Rebreanu;Roman;PAPERBACK;400;FINALIZATA;ion.jpg;1.0;2025-01-01;2025-01-10;4.5;Foarte buna\n" );
        ArrayList<Carte> carti = Backup.importCSV(csv.toString());
        assertEquals(1, carti.size());
        Carte c = carti.get(0);
        assertAll(
                () -> assertEquals(1, c.getId()),
                () -> assertEquals("Ion", c.getTitlu()),
                () -> assertEquals("Liviu Rebreanu", c.getAutor()),
                () -> assertEquals("Roman", c.getGen()),
                () -> assertEquals(Format.PAPERBACK, c.getTip()),
                () -> assertEquals(400, c.getNumar_pagini_totale()),
                () -> assertEquals(Status.FINALIZATA, c.getStatus()),
                () -> assertEquals("ion.jpg", c.getImagine()),
                () -> assertEquals(1.0, c.getProcent()),
                () -> assertEquals(LocalDate.of(2025, 1, 1), c.getDataStart()),
                () -> assertEquals(LocalDate.of(2025, 1, 10), c.getDataFinish()),
                () -> assertEquals(4.5, c.getScor()),
                () -> assertEquals("Foarte buna", c.getReview())
        );
    }

    @Test
    @DisplayName("Test 2 - test import csv - un singur rand dar cu spatii libere")
    public void test2() throws Exception {
        Path tempDir = Files.createTempDirectory("book-jurnal-test2-");
        Path csv = tempDir.resolve("carti.csv");
        Files.writeString(csv, "id;titlu;autor;gen;tip;numar_pagini_totale;status;imagine;procent;dataStart;dataFinish;scor;review\n" +
                "2;Test;Autor;Gen;PAPERBACK;;NECITITA;;0.0;;;4,00;\n");
        ArrayList<Carte> carti = Backup.importCSV(csv.toString());
        assertEquals(1, carti.size());
        Carte c = carti.get(0);
        assertAll(
                () -> assertEquals(2, c.getId()),
                () -> assertEquals(0, c.getNumar_pagini_totale()),
                () -> assertEquals(Status.NECITITA, c.getStatus()),
                () -> assertNull(c.getDataStart()),
                () -> assertNull(c.getDataFinish()),
                () -> assertEquals(4.0, c.getScor())
        );
    }

    @Test
    @DisplayName("Test 3 - test export csv - un singur rand si header")
    public void test3() throws Exception {
        Path tempDir = Files.createTempDirectory("book-jurnal-test3-");
        Path out = tempDir.resolve("export.csv");
        ArrayList<Carte> carti = new ArrayList<>();
        carti.add(new Carte(1, "Ion", "Liviu Rebreanu", "Roman", Format.PAPERBACK, 400, "ion.jpg", Status.FINALIZATA, 1.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10), 4.5, "Foarte buna"));
        Backup.exportCSV(out.toString(), carti);
        List<String> lines = Files.readAllLines(out);
        assertFalse(lines.isEmpty());
        assertTrue(lines.get(0).contains("id;titlu;autor;gen;tip;numar_pagini_totale;status;imagine;procent;dataStart;dataFinish;scor;review"));
        String row = lines.get(1);
        String[] f = row.split(";", -1);
        assertEquals(13, f.length);
        assertAll(
                () -> assertEquals("1", f[0]),
                () -> assertEquals("Ion", f[1]),
                () -> assertEquals("Liviu Rebreanu", f[2]),
                () -> assertEquals("Roman", f[3]),
                () -> assertEquals("PAPERBACK", f[4]),
                () -> assertEquals("400", f[5]),
                () -> assertEquals("FINALIZATA", f[6]),
                () -> assertEquals("ion.jpg", f[7]),
                () -> assertEquals("1.0", f[8]),
                () -> assertEquals("2025-01-01", f[9]),
                () -> assertEquals("2025-01-10", f[10]),
                () -> assertTrue(f[11].equals("4.5") || f[11].equals("4,5")),
                () -> assertEquals("Foarte buna", f[12])
        );
    }

    @Test
    @DisplayName("Test 4 - test export csv - un singur rand gol si header")
    public void test4() throws Exception {
        Path tempDir = Files.createTempDirectory("book-jurnal-test4-");
        Path out = tempDir.resolve("export.csv");
        Backup.exportCSV(out.toString(), new ArrayList<>());
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
        String expectedHeader = "id;titlu;autor;gen;tip;numar_pagini_totale;status;imagine;procent;dataStart;dataFinish;scor;review";
        assertEquals(expectedHeader, lines.get(0).trim());
    }


    @Test
    @DisplayName("Test 5 - test import JSON - o singura intrare")
    public void test5() throws Exception {
        Path tempDir = Files.createTempDirectory("book-jurnal-test5-");
        Path json = tempDir.resolve("jurnal.json");
        Files.writeString(json, "[\n  {\n    \"id\": 1,\n    \"idCarte\": 101,\n    \"data\": \"2025-01-02\",\n    \"paginaCurenta\": 20\n  }\n]\n");
        ArrayList<Jurnal> jurnal = Backup.importJSON(json.toString());
        assertEquals(1, jurnal.size());
        Jurnal j = jurnal.get(0);
        assertAll(
                () -> assertEquals(1, j.getId()),
                () -> assertEquals(101, j.getIdCarte()),
                () -> assertEquals(LocalDate.of(2025, 1, 2), j.getData()),
                () -> assertEquals(20, j.getPaginaCurenta())
        );
    }

    @Test
    @DisplayName("Test 6 - test import JSON - 2 intrari")
    public void test6() throws Exception {
        Path tempDir = Files.createTempDirectory("book-jurnal-test6-");
        Path json = tempDir.resolve("jurnal.json");
        Files.writeString(json, "[\n  {\n    \"id\": 1,\n    \"idCarte\": 101,\n    \"data\": \"2025-01-02\",\n    \"paginaCurenta\": 20\n  },\n{\n    \"id\": 2,\n    \"idCarte\": 101,\n    \"data\": \"2025-01-05\",\n    \"paginaCurenta\": 45\n  }\n]\n");
        ArrayList<Jurnal> jurnal = Backup.importJSON(json.toString());
        assertEquals(2, jurnal.size());
        Jurnal j1 = jurnal.get(0);
        Jurnal j2 = jurnal.get(1);
        assertAll(
                () -> assertEquals(1, j1.getId()),
                () -> assertEquals(101, j1.getIdCarte()),
                () -> assertEquals(LocalDate.of(2025, 1, 2), j1.getData()),
                () -> assertEquals(20, j1.getPaginaCurenta()),
                () -> assertEquals(2, j2.getId()),
                () -> assertEquals(101, j2.getIdCarte()),
                () -> assertEquals(LocalDate.of(2025, 1, 5), j2.getData()),
                () -> assertEquals(45, j2.getPaginaCurenta())
        );
    }


    @Test
    @DisplayName("Test7 - test export JSON - 2 intrari")
    public void test7() throws Exception {
        Path tempDir = Files.createTempDirectory("book-jurnal-test7-");
        Path json = tempDir.resolve("jurnalExport.json");
        ArrayList<Jurnal> jurnal = new ArrayList<>();
        jurnal.add(new Jurnal(1, 101, LocalDate.of(2025, 1, 2), 20));
        jurnal.add(new Jurnal(2, 102, LocalDate.of(2025, 2, 10), 30));
        Backup.exportJSON(json.toString(), jurnal);
        String content = Files.readString(json);
        assertAll(
                () -> assertTrue(content.contains("[")),
                () -> assertTrue(content.contains("\"id\": 1")),
                () -> assertTrue(content.contains("\"idCarte\": 101")),
                () -> assertTrue(content.contains("\"data\": \"2025-01-02\"")),
                () -> assertTrue(content.contains("\"paginaCurenta\": 30")),
                () -> assertTrue(content.contains("]"))
        );
    }

    @Test
    @DisplayName("Test8 - test export JSON - jurnal gol")
    public void test8() throws Exception {
        Path tempDir = Files.createTempDirectory("book-jurnal-test8-");
        Path out = tempDir.resolve("jurnalExport.json");
        Backup.exportJSON(out.toString(), new ArrayList<>());
        String content = Files.readString(out).trim();
        assertTrue(content.startsWith("["));
        assertTrue(content.endsWith("]"));
    }
}

