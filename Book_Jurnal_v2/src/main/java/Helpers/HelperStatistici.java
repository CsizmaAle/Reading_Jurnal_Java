package Helpers;

import java.time.LocalDate;

public class HelperStatistici {

    /**
     * Metoda ce creeaza un array cu ani pentru selectarea anilor (se iau ultimii 7 ani)
     * @return Integer[] - array cu anii
     */
    public static Integer[] buildYearRange() {
        int current = LocalDate.now().getYear();
        Integer[] years = new Integer[7];
        for (int i = 0; i < years.length; i++) {
            years[i] = current - i ;
        }
        return years;
    }


}
