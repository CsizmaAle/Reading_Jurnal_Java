package UI;

import Services.StatisticiService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.Map;

/**
 * Clasa ce are ca scop realizarea unui grafic care afiseaza numarul de carti citite pe ani incepand de la prima inregistrare  - interfata grafica
 */
public class StatisticsPanel5 extends VBox {
    private final LineChart<String, Number> chart;

    /**
     * Constructor pentru clasa StatisticsPanel5
     * Clasa ce are ca scop realizarea unui grafic care afiseaza numarul de carti citite pe ani incepand de la prima inregistrare  - interfata grafica
     */
    public StatisticsPanel5() {
        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        Label title = new Label("Carti citite / an");
        title.getStyleClass().add("card-title");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Year");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Books");

        chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setMinHeight(220);

        HBox header = new HBox(10, title);
        header.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(header, chart);
        refreshChart();
    }

    /**
     * Metoda folosita pentru a reincarca graficul in cazul modificarii datelor
     */
    private void refreshChart() {
        Map<Integer, Integer> data = StatisticiService.getFinishedBooksPerYear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        int currentYear = LocalDate.now().getYear();
        int minYear = data.isEmpty() ? currentYear : data.keySet().iterator().next();
        int maxYear = data.isEmpty() ? currentYear : data.keySet().stream().reduce((a, b) -> b).orElse(currentYear);

        for (int year = minYear; year <= maxYear; year++) {
            int value = data.getOrDefault(year, 0);
            series.getData().add(new XYChart.Data<>(String.valueOf(year), value));
        }

        chart.getData().setAll(series);
    }
}
