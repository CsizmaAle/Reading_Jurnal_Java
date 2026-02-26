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

public class TestUtilitaraCarte {

    @Test
    @DisplayName("Test 1- adaugareCarte - IN_CURS_DE_CITIRE: calculeaza procent, seteaza dataStart, adauga in lista")
    void test1() {
        ArrayList<Carte> carti = new ArrayList<>();
        String input = "Titlu Test\nAutor Test\nGen Test\n2\n\n200\nIN_CURS_DE_CITIRE\n2025-01-10\n50\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner sc = new Scanner(System.in);
        UtilitaraCarte.adaugareCarte(carti, sc);
        assertEquals(1, carti.size());
        Carte c = carti.get(0);

        assertAll(
                () -> assertEquals(1, c.getId()),
                () -> assertEquals("Titlu Test", c.getTitlu()),
                () -> assertEquals("Autor Test", c.getAutor()),
                () -> assertEquals("Gen Test", c.getGen()),
                () -> assertEquals(Format.PAPERBACK, c.getTip()),
                () -> assertEquals("", c.getImagine()),
                () -> assertEquals(200, c.getNumar_pagini_totale()),
                () -> assertEquals(Status.IN_CURS_DE_CITIRE, c.getStatus()),
                () -> assertEquals(LocalDate.of(2025, 1, 10), c.getDataStart()),
                () -> assertNull(c.getDataFinish()),
                () -> assertEquals(0.25, c.getProcent(), 1e-9),
                () -> assertEquals(0.0, c.getScor(), 1e-9),
                () -> assertEquals("", c.getReview())
        );
    }

    @Test
    @DisplayName("Test 2 - filtrareCrati -  alege cartile cu un anumit status")
    void test2() {
        ArrayList<Carte> carti = new ArrayList<>();
        carti.add(new Carte(1, "A", "X", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, ""));
        carti.add(new Carte(2, "B", "Y", "G", Format.PAPERBACK, 100, "", Status.FINALIZATA, 1.0, LocalDate.of(2025,1,1), LocalDate.of(2025,1,2), 4.0, ""));
        System.setIn(new ByteArrayInputStream("2\n".getBytes()));
        Scanner sc = new Scanner(System.in);
        ArrayList<Carte> rez = UtilitaraCarte.filtrareCarti(carti, sc);
        assertEquals(1, rez.size());
        assertEquals(Status.FINALIZATA, rez.get(0).getStatus());
    }


    @Test
    @DisplayName("Test 3 - deleteBook - id inexistent: lista ramane neschimbata")
    void test3() {
        ArrayList<Carte> carti = new ArrayList<>();
        carti.add(new Carte(1, "A", "X", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, ""));
        System.setIn(new ByteArrayInputStream("999\n".getBytes()));
        Scanner sc = new Scanner(System.in);
        UtilitaraCarte.deleteBook(carti, sc);
        assertEquals(1, carti.size());
        assertEquals(1, carti.get(0).getId());
    }

    @Test
    @DisplayName("Test 4 - deleteBook - sterge cartea cu id existent ")
    void test4() {
        ArrayList<Carte> carti = new ArrayList<>();
        carti.add(new Carte(1, "A", "X", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, ""));
        carti.add(new Carte(2, "B", "Y", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, ""));
        System.setIn(new ByteArrayInputStream("1\n".getBytes()));
        Scanner sc = new Scanner(System.in);
        UtilitaraCarte.deleteBook(carti, sc);
        assertEquals(1, carti.size());
        assertEquals(2, carti.get(0).getId());
    }

    @Test
    @DisplayName("Test 5 - changeStatus - schimba statusul cand id si status1 corespund")
    void test5() {
        ArrayList<Carte> carti = new ArrayList<>();
        Carte c = new Carte(10, "A", "X", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, "");
        carti.add(c);
        UtilitaraCarte.changeStatus(carti, 10, Status.NECITITA, Status.ABANDONATA);
        assertEquals(Status.ABANDONATA, c.getStatus());
    }

    @Test
    @DisplayName("Test 6 - getBooksByStatus - returneaza doar cartile cu statusul cerut")
    void test6() {
        ArrayList<Carte> carti = new ArrayList<>();
        carti.add(new Carte(1, "A", "X", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, ""));
        carti.add(new Carte(2, "B", "Y", "G", Format.PAPERBACK, 100, "", Status.FINALIZATA, 1.0, LocalDate.of(2025,1,1), LocalDate.of(2025,1,2), 4.0, ""));
        carti.add(new Carte(3, "C", "Z", "G", Format.PAPERBACK, 100, "", Status.FINALIZATA, 1.0, LocalDate.of(2025,2,1), LocalDate.of(2025,2,2), 5.0, ""));
        ArrayList<Carte> rez = UtilitaraCarte.getBooksByStatus(carti, Status.FINALIZATA);

        assertEquals(2, rez.size());
        assertAll(
                () -> assertEquals(2, rez.get(0).getId()),
                () -> assertEquals(3, rez.get(1).getId())
        );
    }

}
