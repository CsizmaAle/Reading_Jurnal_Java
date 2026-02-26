package UI;

import Services.StatisticiService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;
import static Helpers.HelperStatistici.buildYearRange;

/**
 * Calsa ce realizeaza un calendar cu zilele de lectura pentru ultimii 7 ani - interfata grafica
 */
public class StatisticsPanel2 extends VBox {
    private final GridPane calendar = new GridPane();
    private final ComboBox<Month> monthBox = new ComboBox<>();
    private final ComboBox<Integer> yearBox = new ComboBox<>();

    /**
     * Constructor pentru clasa StatisticsPanel2 - calendar cu zilele de lectura pentru ultimii 7 ani - interfata grafica
     */
    public StatisticsPanel2() {
        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        Label title = new Label("Calendar citire");
        title.getStyleClass().add("card-title");

        monthBox.getItems().setAll(Month.values());
        yearBox.getItems().setAll(buildYearRange());

        LocalDate now = LocalDate.now();
        monthBox.setValue(now.getMonth());
        yearBox.setValue(now.getYear());

        HBox controls = new HBox(10, monthBox, yearBox);
        controls.setAlignment(Pos.CENTER_LEFT);

        calendar.setHgap(6);
        calendar.setVgap(6);
        calendar.setPadding(new Insets(8, 0, 0, 0));

        monthBox.setOnAction(e -> refreshCalendar());
        yearBox.setOnAction(e -> refreshCalendar());

        getChildren().addAll(title, controls, calendar);
        refreshCalendar();
    }

    /**
     * Metoda ce face refresh la calendar dupa seledtarea lunii/anului
     */
    private void refreshCalendar() {
        calendar.getChildren().clear();

        Month month = monthBox.getValue();
        Integer year = yearBox.getValue();
        if (month == null || year == null) return;

        addWeekdayHeaders();

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int daysInMonth = firstDay.lengthOfMonth();
        int startCol = dayOfWeekToColumn(firstDay.getDayOfWeek());
        int row = 1;
        int col = startCol;

        Set<Integer> readingDays = StatisticiService.getReadingDays(year, month.getValue());

        for (int day = 1; day <= daysInMonth; day++) {
            Label cell = new Label(String.valueOf(day));
            cell.setMinSize(28, 28);
            cell.setAlignment(Pos.CENTER);
            cell.setStyle("""
                    -fx-border-color: #eeeeee;
                    -fx-border-radius: 6;
                    -fx-background-radius: 6;
                    """);

            if (readingDays.contains(day)) {
                cell.setStyle("""
                        -fx-background-color: #d9eaff;
                        -fx-border-color: #b7d7ff;
                        -fx-border-radius: 6;
                        -fx-background-radius: 6;
                        """);
            }

            calendar.add(cell, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Metoda  ce adauga la calendar zilele saptamanii
     */
    private void addWeekdayHeaders() {
        for (int i = 0; i < 7; i++) {
            DayOfWeek day = DayOfWeek.of(i == 0 ? 7 : i);
            String label = day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            Label header = new Label(label);
            header.setStyle("-fx-opacity: 0.7; -fx-font-size: 11px;");
            calendar.add(header, i, 0);
        }
    }

    /**
     * Metoda ce mapeaza zilele saptamanii pe coloane
     * @param day DayOfWeek; Ziua saptamanii
     * @return int; ordinea zile in saptamana ( duminica-> 0 .... sambata->6)
     */
    private int dayOfWeekToColumn(DayOfWeek day) {
        int val = day.getValue();
        return val % 7;
    }

}
