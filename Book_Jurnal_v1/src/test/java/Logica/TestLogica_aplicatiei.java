package Logica;

import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Jurnal;
import Modele_de_date.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TestLogica_aplicatiei {
    @Test
    @DisplayName("Test 1 -  reading progress + adaugare in jurnal")
    void test1() {
        ArrayList<Carte> carti = new ArrayList<>();
        ArrayList<Jurnal> jurnal = new ArrayList<>();
        LocalDate now = LocalDate.now();
        Carte c = new Carte(10, "Titlu", "Autor", "Gen", Format.PAPERBACK, 100, "img", Status.IN_CURS_DE_CITIRE, 0.0, null, null, 0.0, "");
        carti.add(c);
        System.setIn(new ByteArrayInputStream("25\n".getBytes()));
        Scanner sc = new Scanner(System.in);
        Logica_aplicatiei.readingProgress(carti, jurnal, 10, sc);

        assertEquals(1, jurnal.size());
        assertAll(
                () -> assertEquals(1, jurnal.get(0).getId()),
                () -> assertEquals(10, jurnal.get(0).getIdCarte()),
                () -> assertEquals(LocalDate.now(), jurnal.get(0).getData()),
                () -> assertEquals(25, jurnal.get(0).getPaginaCurenta()),
                () -> assertEquals(0.25, c.getProcent(), 1e-9),
                () -> assertEquals(Status.IN_CURS_DE_CITIRE, c.getStatus()),
                () -> assertEquals(1,jurnal.get(jurnal.size()-1).getId()),
                () -> assertEquals(now,jurnal.get(jurnal.size()-1).getData()),
                () -> assertEquals(10,jurnal.get(jurnal.size()-1).getIdCarte()),
                () -> assertEquals(25,jurnal.get(jurnal.size()-1).getPaginaCurenta())
        );
    }

    @Test
    @DisplayName("Test 2 - start reading - seteaza status IN_CURS_DE_CITIRE si dataStart=azi")
    void test2() {
        Carte c = new Carte(1, "T", "A", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, "");
        Logica_aplicatiei.startReading(c);
        assertAll(
                () -> assertEquals(Status.IN_CURS_DE_CITIRE, c.getStatus()),
                () -> assertEquals(LocalDate.now(), c.getDataStart())
        );
    }

    @Test
    @DisplayName("Test 3 - detaliiCarte - carte gasita: afiseaza toString()")
    void test3() {
        ArrayList<Carte> carti = new ArrayList<>();
        Carte c = new Carte(7, "CarteX", "Autor", "Gen", Format.PAPERBACK, 123, "img", Status.NECITITA, 0.0, null, null, 0.0, "");
        carti.add(c);
        System.setIn(new ByteArrayInputStream("7\n".getBytes()));
        Scanner sc = new Scanner(System.in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        Logica_aplicatiei.detaliiCarte(carti, sc);
        System.setOut(originalOut);
        String output = out.toString();

        assertAll(
                () -> assertTrue(output.contains("CarteX")),
                () -> assertTrue(output.contains("Autor")),
                () -> assertTrue(output.contains("Gen")),
                () -> assertTrue(output.contains("PAPERBACK")),
                () -> assertTrue(output.contains("7")),
                () -> assertTrue(output.contains("123")),
                () -> assertTrue(output.contains("img")),
                () -> assertTrue(output.contains("NECITITA")),
                () -> assertTrue(output.contains("0.0"))
        );
    }

    @Test
    @DisplayName("Test 4 - editareCarte - modifica titlu si iese")
    void test4() {
        ArrayList<Carte> carti = new ArrayList<>();
        Carte c = new Carte(3, "Vechi", "Autor", "Gen", Format.PAPERBACK, 200, "", Status.NECITITA, 0.0, null, null, 0.0, "");
        carti.add(c);
        System.setIn(new ByteArrayInputStream(("3\n1\nNou\n0\n").getBytes()));
        Scanner sc = new Scanner(System.in);
        Logica_aplicatiei.editareCarte(carti, sc);
        assertEquals("Nou", c.getTitlu());
    }

    @Test
    @DisplayName("Test 5 - campObliogatoriu - testeaza daca s-a introdus text, nu rand gol/enter/space")
    void test5() {
        System.setIn(new ByteArrayInputStream(("\n   \nabc\n").getBytes()));
        Scanner sc = new Scanner(System.in);
        String rez = Logica_aplicatiei.campObliogatoriu(sc, "Titlu:");
        assertEquals("abc", rez);
    }

    @Test
    @DisplayName("Test 6 - finishReading testata prin preagingProgress - este apelata: seteaza status, procent, dataFinish, scor si review")
    void test6() {
        ArrayList<Carte> carti = new ArrayList<>();
        ArrayList<Jurnal> jurnal = new ArrayList<>();
        Carte c = new Carte(1, "Test", "Autor", "Gen", Format.PAPERBACK, 100, "img", Status.IN_CURS_DE_CITIRE, 0.0, LocalDate.of(2025, 1, 1), null, 0.0, "");
        carti.add(c);
        jurnal.add(new Jurnal(1, 1, LocalDate.now().minusDays(1), 20));
        System.setIn(new ByteArrayInputStream("100\n4.75\nFoarte buna\n".getBytes()));
        Scanner sc = new Scanner(System.in);
        Logica_aplicatiei.readingProgress(carti, jurnal, 1, sc);

        assertAll(
                () -> assertEquals(Status.FINALIZATA, c.getStatus()),
                () -> assertEquals(1.0, c.getProcent(), 1e-9),
                () -> assertEquals(LocalDate.now(), c.getDataFinish()),
                () -> assertEquals(4.75, c.getScor(), 1e-9),
                () -> assertEquals("Foarte buna", c.getReview())
        );

        assertEquals(2, jurnal.size());
        Jurnal j = jurnal.get(1);
        assertAll(
                () -> assertEquals(2, j.getId()),
                () -> assertEquals(1, j.getIdCarte()),
                () -> assertEquals(100, j.getPaginaCurenta()),
                () -> assertEquals(LocalDate.now(), j.getData())
        );
    }
}
