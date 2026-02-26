package UI;

import Modele_de_date.Status;
import Services.StatisticiService;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Clasa ce este responsabila cu afisarea unor statistici si date despre biblioteca - intrefata grafica
 */
public class StatisticsPanel3 extends VBox {
    /**
     * Constructor pentru clasa StatisticsPanel3 - rol in afisarea unor statistici si date despre biblioteca - intrefata grafica
     * Date afisate: total carti, nr carti citite, nr carti necitite, procent carti citite, medie pargini per sesiune, medie scor carti citite
     */
    public StatisticsPanel3() {
        setSpacing(8);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        int total = StatisticiService.countBooksBasedOnStatus(null);
        int read = StatisticiService.countBooksBasedOnStatus(Status.FINALIZATA);
        int unread = StatisticiService.countBooksBasedOnStatus(Status.NECITITA);
        double percent = StatisticiService.procentCitite();
        int avgPages = StatisticiService.mediePagini();
        double avgScore = StatisticiService.medieScorCartiCitite();

        Label title = new Label("Rezumat biblioteca");
        title.getStyleClass().add("card-title");

        Label totalLbl = new Label("Total carti: " + total);
        Label readLbl = new Label("Carti citite: " + read);
        Label unreadLbl = new Label("Carti necitite: " + unread);
        Label percentLbl = new Label(String.format("Procent citite: %.1f%%", percent));
        Label avgLbl = new Label("Medie pagini / sesiune: " + avgPages);
        Label avgScoreLbl = new Label(String.format("Medie scor carti citite: %.1f", avgScore));

        getChildren().addAll(title, totalLbl, readLbl, unreadLbl, percentLbl, avgLbl, avgScoreLbl);
    }
}
