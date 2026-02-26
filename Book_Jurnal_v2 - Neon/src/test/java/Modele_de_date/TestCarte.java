package Modele_de_date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clasa de test pentru clasa Carte
 */
public class TestCarte {

    @Test
    @DisplayName("Test 1 pentru gettere si creare de obiecte")
    public void TestCarte1() {
        Carte c = new Carte(1, "Titlu", "Autor", "Gen", Format.EBOOK, 123, "img.png");

        assertEquals(1, c.getId());
        assertEquals("Titlu", c.getTitlu());
        assertEquals("Autor", c.getAutor());
        assertEquals("Gen", c.getGen());
        assertEquals(Format.EBOOK, c.getTip());
        assertEquals(123, c.getNumar_pagini_totale());
        assertEquals("img.png", c.getImagine());
        assertEquals(Status.NECITITA, c.getStatus());
    }

    @Test
    @DisplayName("Test 2 pentru gettere, settere si creare de obiecte")
    void TestCarte2() {
        Carte c = new Carte();
        c.setId(7);
        c.setTitlu("A");
        c.setAutor("B");
        c.setGen("C");
        c.setTip(Format.HARDCOVER);
        c.setNumar_pagini_totale(250);
        c.setStatus(Status.IN_CURS_DE_CITIRE);
        c.setImagine("cover.jpg");
        c.setProcent(0.4);
        c.setDataStart(LocalDate.of(2025, 1, 1));
        c.setDataFinish(LocalDate.of(2025, 2, 1));
        c.setScor(4.5);
        c.setReview("ok");

        assertEquals(7, c.getId());
        assertEquals("A", c.getTitlu());
        assertEquals("B", c.getAutor());
        assertEquals("C", c.getGen());
        assertEquals(Format.HARDCOVER, c.getTip());
        assertEquals(250, c.getNumar_pagini_totale());
        assertEquals(Status.IN_CURS_DE_CITIRE, c.getStatus());
        assertEquals("cover.jpg", c.getImagine());
        assertEquals(0.4, c.getProcent(), 1e-9);
        assertEquals(LocalDate.of(2025, 1, 1), c.getDataStart());
        assertEquals(LocalDate.of(2025, 2, 1), c.getDataFinish());
        assertEquals(4.5, c.getScor(), 1e-9);
        assertEquals("ok", c.getReview());
    }

    @Test
    @DisplayName("Test 3 constructor complet de carte si getteri")
    public void TestCarte3() {
        LocalDate start = LocalDate.of(2025, 1, 10);
        LocalDate finish = LocalDate.of(2025, 1, 20);

        Carte c = new Carte(1, "Test Titlu", "Test Autor", "Fictiune", Format.PAPERBACK, 300, "img.jpg", Status.FINALIZATA, 1.0, start, finish, 4.5, "Foarte buna");

        assertEquals(1, c.getId());
        assertEquals("Test Titlu", c.getTitlu());
        assertEquals("Test Autor", c.getAutor());
        assertEquals("Fictiune", c.getGen());
        assertEquals(Format.PAPERBACK, c.getTip());
        assertEquals(300, c.getNumar_pagini_totale());
        assertEquals("img.jpg", c.getImagine());
        assertEquals(Status.FINALIZATA, c.getStatus());
        assertEquals(1.0, c.getProcent());
        assertEquals(start, c.getDataStart());
        assertEquals(finish, c.getDataFinish());
        assertEquals(4.5, c.getScor());
        assertEquals("Foarte buna", c.getReview());
    }

    @Test
    @DisplayName("Test 4 - toString pentru clasa Carte")
    public void TestCarte4() {
        LocalDate start = LocalDate.of(2025, 1, 10);
        LocalDate finish = LocalDate.of(2025, 1, 20);

        Carte c = new Carte(10, "Ion", "Liviu Rebreanu", "Roman", Format.HARDCOVER, 400, "ion.jpg", Status.FINALIZATA, 1.0, start, finish, 4.75, "O carte foarte buna");

        String s = c.toString();

        assertAll(
                () -> assertTrue(s.contains("10")),
                () -> assertTrue(s.contains("Ion")),
                () -> assertTrue(s.contains("Liviu Rebreanu")),
                () -> assertTrue(s.contains("Roman")),
                () -> assertTrue(s.contains("HARDCOVER")),
                () -> assertTrue(s.contains("400")),
                () -> assertTrue(s.contains("ion.jpg")),
                () -> assertTrue(s.contains("FINALIZATA")),
                () -> assertTrue(s.contains("1")),
                () -> assertTrue(s.contains(start.toString())),
                () -> assertTrue(s.contains(finish.toString())),
                () -> assertTrue(s.contains("4.75") || s.contains("4,75")),
                () -> assertTrue(s.contains("O carte foarte buna"))
        );
    }

    @Test
    @DisplayName("Test 5 - constructor fara parametrii + settere + gettere")
    public void TestCarte5() {
        Carte c = new Carte();

        c.setId(0);
        c.setTitlu("");
        c.setAutor("Autor Test");
        c.setGen(null);
        c.setTip(Format.PAPERBACK);
        c.setNumar_pagini_totale(1);
        c.setImagine(null);
        c.setStatus(Status.IN_CURS_DE_CITIRE);
        c.setProcent(0.0);
        c.setDataStart(LocalDate.of(2025, 1, 1));
        c.setDataFinish(null);
        c.setScor(0.0);
        c.setReview("");

        assertAll(
                () -> assertEquals(0, c.getId()),
                () -> assertEquals("", c.getTitlu()),
                () -> assertEquals("Autor Test", c.getAutor()),
                () -> assertNull(c.getGen()),
                () -> assertEquals(Format.PAPERBACK, c.getTip()),
                () -> assertEquals(1, c.getNumar_pagini_totale()),
                () -> assertNull(c.getImagine()),
                () -> assertEquals(Status.IN_CURS_DE_CITIRE, c.getStatus()),
                () -> assertEquals(0.0, c.getProcent()),
                () -> assertEquals(LocalDate.of(2025, 1, 1), c.getDataStart()),
                () -> assertNull(c.getDataFinish()),
                () -> assertEquals(0.0, c.getScor()),
                () -> assertEquals("", c.getReview())
        );
    }

    @Test
    @DisplayName("Test 6 - to String pentru clasa Carte")
    public void TestCarte6() {
        Carte c = new Carte();

        c.setId(7);
        c.setTitlu("Carte Incompleta");
        c.setAutor("");
        c.setGen(null);
        c.setTip(Format.PAPERBACK);
        c.setNumar_pagini_totale(0);
        c.setImagine(null);
        c.setStatus(Status.NECITITA);
        c.setProcent(0.0);
        c.setDataStart(null);
        c.setDataFinish(null);
        c.setScor(0.0);
        c.setReview(null);

        String s = c.toString();

        assertAll(
                () -> assertNotNull(s),
                () -> assertFalse(s.isEmpty()),
                () -> assertTrue(s.contains("7")),
                () -> assertTrue(s.contains("Carte Incompleta")),
                () -> assertTrue(s.contains("PAPERBACK")),
                () -> assertTrue(s.contains("NECITITA")),
                () -> assertTrue(s.contains("0"))
        );
    }
}
