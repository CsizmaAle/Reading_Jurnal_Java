package UI;

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
import static Helpers.HelperStatistici.buildYearRange;

/**
 * Clasa responsabila de realizarea uni grafic cu numarul de carti citite pe luna in functie de an
 */
public class StatisticsPanel4 extends VBox {
    private final ComboBox<Integer> yearBox = new ComboBox<>();
    private final LineChart<String, Number> chart;

    /**
     * Constructor pentru StatisticsPanel4 - clasa responsabila de realizarea uni grafic cu numarul de carti citite pe luna in functie de an
     */
    public StatisticsPanel4() {
        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        Label title = new Label("Carti citite / luna");
        title.getStyleClass().add("card-title");

        yearBox.getItems().setAll(buildYearRange());
        yearBox.setValue(LocalDate.now().getYear());

        HBox controls = new HBox(10, new Label("Year:"), yearBox);
        controls.setAlignment(Pos.CENTER_LEFT);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Books");

        chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setMinHeight(220);

        yearBox.setOnAction(e -> refreshChart());

        getChildren().addAll(title, controls, chart);
        refreshChart();
    }

    /**
     * Metoda ce reincarca graficul in cazul in care utilizatorul schimba anul
     */
    private void refreshChart() {
        Integer year = yearBox.getValue();
        if (year == null) return;
        Map<Integer, Integer> data = StatisticiService.getFinishedBooksPerMonth(year);
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (int m = 1; m <= 12; m++) {
            Month month = Month.of(m);
            String label = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            int value = data.getOrDefault(m, 0);
            series.getData().add(new XYChart.Data<>(label, value));
        }

        chart.getData().setAll(series);
    }
}
