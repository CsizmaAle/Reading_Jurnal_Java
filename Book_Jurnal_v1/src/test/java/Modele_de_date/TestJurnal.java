package Modele_de_date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clasa de test pentru clasa Jurnal
 */
public class TestJurnal {

    @Test
    @DisplayName("Test 1 - test pentru constructor fara parametrii, getteri si setteri")
    public void test1(){
        Jurnal j = new Jurnal();

        j.setId(0);
        j.setIdCarte(10);
        j.setData(null);
        j.setPaginaCurenta(2);
        j.setPaginiCitite(1);

        assertAll(
                () -> assertEquals(0, j.getId()),
                () -> assertEquals(10, j.getIdCarte()),
                () -> assertNull(j.getData()),
                () -> assertEquals(2, j.getPaginaCurenta()),
                () -> assertEquals(1, j.getPaginiCitite())
        );
    }

    @Test
    @DisplayName("Test 2 - test pentru constructor fara parametrii, getteri si setteri")
    public void test2(){
        Jurnal j = new Jurnal();

        j.setId(34);
        j.setIdCarte(150);
        j.setData(LocalDate.now());
        j.setPaginaCurenta(2);
        j.setPaginiCitite(1);

        assertAll(
                () -> assertEquals(34, j.getId()),
                () -> assertEquals(150, j.getIdCarte()),
                () -> assertEquals(LocalDate.now(), j.getData()),
                () -> assertEquals(2, j.getPaginaCurenta()),
                () -> assertEquals(1, j.getPaginiCitite())
        );
    }

    @Test
    @DisplayName(" Test 3 - test Constructor complet")
    void test3() {
        LocalDate d = LocalDate.of(2025, 3, 10);
        Jurnal j = new Jurnal(5, 101, d, 120);

        assertAll(
                () -> assertEquals(5, j.getId()),
                () -> assertEquals(101, j.getIdCarte()),
                () -> assertEquals(d, j.getData()),
                () -> assertEquals(120, j.getPaginaCurenta())
        );
    }

    @Test
    @DisplayName(" Test 4 - test Constructor complet")
    void test4() {
        LocalDate d = LocalDate.of(2021, 6, 20);
        Jurnal j = new Jurnal(7, 1051, d, 130);

        assertAll(
                () -> assertEquals(7, j.getId()),
                () -> assertEquals(1051, j.getIdCarte()),
                () -> assertEquals(d, j.getData()),
                () -> assertEquals(130, j.getPaginaCurenta())
        );
    }

    @Test
    @DisplayName("Test 5 - toString")
    void test5() {
        LocalDate d = LocalDate.of(2025, 4, 1);
        Jurnal j = new Jurnal(8, 202, d, 75);
        String s = j.toString();

        assertAll(
                () -> assertNotNull(s),
                () -> assertFalse(s.isEmpty()),
                () -> assertTrue(s.contains("8")),
                () -> assertTrue(s.contains("202")),
                () -> assertTrue(s.contains(d.toString())),
                () -> assertTrue(s.contains("75")),
                () -> assertTrue(s.contains("25"))
        );
    }


}
