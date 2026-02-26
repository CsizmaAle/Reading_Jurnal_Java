package UI;

import Helpers.HelperStatistici;
import Services.StatisticiService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

/**
 * Clasa ce are ca rol realizarea de grafic - pagini citite pe zi - in pagina Statistici
 */
public class StatisticsPanel1 extends VBox {
    private final ComboBox<Month> monthBox = new ComboBox<>();
    private final ComboBox<Integer> yearBox = new ComboBox<>();
    private final LineChart<String, Number> chart;

    /**
     * Constructor pentru clasa StatisticsPanel1 - rol realizarea de grafic - pagini citite pe zi - in pagina Statistici
     */
    public StatisticsPanel1() {
        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        Label title = new Label("Pagini citite / zi");
        title.getStyleClass().add("card-title");

        monthBox.getItems().setAll(Month.values());
        yearBox.getItems().setAll(HelperStatistici.buildYearRange());

        LocalDate now = LocalDate.now();
        monthBox.setValue(now.getMonth());
        yearBox.setValue(now.getYear());

        monthBox.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Month item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            }
        });
        monthBox.setButtonCell(monthBox.getCellFactory().call(null));

        HBox controls = new HBox(10, monthBox, yearBox);
        controls.setAlignment(Pos.CENTER_LEFT);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Day");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Pages");

        chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setMinHeight(220);

        monthBox.setOnAction(e -> refreshChart());
        yearBox.setOnAction(e -> refreshChart());

        getChildren().addAll(title, controls, chart);
        refreshChart();
    }

    /**
     * Metoda ce reincarca graficul pentru anul si luna selectate
     */
    private void refreshChart() {
        Month month = monthBox.getValue();
        Integer year = yearBox.getValue();
        if (month == null || year == null) return;

        Map<Integer, Integer> data = StatisticiService.getPagesPerDay(year, month.getValue());
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        int daysInMonth = month.length(LocalDate.of(year, month, 1).isLeapYear());
        for (int day = 1; day <= daysInMonth; day++) {
            Integer value = data.getOrDefault(day, 0);
            series.getData().add(new XYChart.Data<>(String.valueOf(day), value));
        }
        chart.getData().setAll(series);
    }
}
