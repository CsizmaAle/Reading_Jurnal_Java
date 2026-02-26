package Logica;

import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Clasa de test pentru metodele din LogicaAplicatiei
 */
public class LogicaAplicatieiTest {

    @Test
    @DisplayName("Test 1 - start reading - seteaza status IN_CURS_DE_CITIRE si dataStart=azi")
    void test1() {
        Carte c = new Carte(1, "T", "A", "G", Format.PAPERBACK, 100, "", Status.NECITITA, 0.0, null, null, 0.0, "");
        LogicaAplicatiei.startReading(c);
        assertAll(
                () -> assertEquals(Status.IN_CURS_DE_CITIRE, c.getStatus()),
                () -> assertEquals(LocalDate.now(), c.getDataStart())
        );
    }


    @Test
    @DisplayName("Test 2 - start reading - suprascrie statusul si dataStart existente")
    void test2() {
        Carte c = new Carte(2, "T2", "A2", "G2", Format.PAPERBACK, 120, "", Status.ABANDONATA, 0.0, LocalDate.of(2024, 1, 1), null, 0.0, "");
        LogicaAplicatiei.startReading(c);
        assertAll(
                () -> assertEquals(Status.IN_CURS_DE_CITIRE, c.getStatus()),
                () -> assertEquals(LocalDate.now(), c.getDataStart())
        );
    }

    @Test
    @DisplayName("Test 3 - start reading - nu modifica alte campuri")
    void test3() {
        Carte c = new Carte(3, "Titlu", "Autor", "Gen", Format.PAPERBACK, 220, "img.png", Status.NECITITA, 0.25, null, LocalDate.of(2024, 6, 1), 3.5, "ok");
        LogicaAplicatiei.startReading(c);
        assertAll(
                () -> assertEquals("Titlu", c.getTitlu()),
                () -> assertEquals("Autor", c.getAutor()),
                () -> assertEquals("Gen", c.getGen()),
                () -> assertEquals(Format.PAPERBACK, c.getTip()),
                () -> assertEquals(220, c.getNumar_pagini_totale()),
                () -> assertEquals("img.png", c.getImagine()),
                () -> assertEquals(0.25, c.getProcent(), 1e-9),
                () -> assertEquals(LocalDate.of(2024, 6, 1), c.getDataFinish()),
                () -> assertEquals(3.5, c.getScor(), 1e-9),
                () -> assertEquals("ok", c.getReview())
        );
    }

    @Test
    @DisplayName("Test 4 - finish reading - seteaza status FINALIZATA, procent=1 si dataFinish=azi")
    void test4() {
        Carte c = new Carte(4, "T4", "A4", "G4", Format.EBOOK, 80, "", Status.IN_CURS_DE_CITIRE, 0.6, LocalDate.of(2024, 2, 10), null, 2.0, "old");
        LogicaAplicatiei.finishReading(c, 4.5, "bun");
        assertAll(
                () -> assertEquals(Status.FINALIZATA, c.getStatus()),
                () -> assertEquals(1.0, c.getProcent(), 1e-9),
                () -> assertEquals(LocalDate.now(), c.getDataFinish()),
                () -> assertEquals(4.5, c.getScor(), 1e-9),
                () -> assertEquals("bun", c.getReview())
        );
    }

    @Test
    @DisplayName("Test 5 - finish reading - nu suprascrie scor/review cand sunt null")
    void test5() {
        Carte c = new Carte(5, "T5", "A5", "G5", Format.HARDCOVER, 300, "", Status.IN_CURS_DE_CITIRE, 0.9, LocalDate.of(2024, 5, 5), null, 3.25, "ok");
        LogicaAplicatiei.finishReading(c, null, null);
        assertAll(
                () -> assertEquals(Status.FINALIZATA, c.getStatus()),
                () -> assertEquals(1.0, c.getProcent(), 1e-9),
                () -> assertEquals(LocalDate.now(), c.getDataFinish()),
                () -> assertEquals(3.25, c.getScor(), 1e-9),
                () -> assertEquals("ok", c.getReview())
        );
    }

    @Test
    @DisplayName("Test 6 - finish reading - nu modifica alte campuri")
    void test6() {
        Carte c = new Carte(6, "Titlu6", "Autor6", "Gen6", Format.PAPERBACK, 250, "img6.png", Status.IN_CURS_DE_CITIRE, 0.4, LocalDate.of(2024, 3, 3), null, 0.0, "");
        LogicaAplicatiei.finishReading(c, 4.0, "ok");
        assertAll(
                () -> assertEquals("Titlu6", c.getTitlu()),
                () -> assertEquals("Autor6", c.getAutor()),
                () -> assertEquals("Gen6", c.getGen()),
                () -> assertEquals(Format.PAPERBACK, c.getTip()),
                () -> assertEquals(250, c.getNumar_pagini_totale()),
                () -> assertEquals("img6.png", c.getImagine()),
                () -> assertEquals(LocalDate.of(2024, 3, 3), c.getDataStart())
        );
    }

}
