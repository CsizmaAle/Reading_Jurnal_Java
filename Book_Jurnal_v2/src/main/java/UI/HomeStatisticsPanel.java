package UI;

import Services.StatisticiService;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.LocalDate;

/**
 * Clasa ce are ca rol gestionarea sectiunii legate de statistici din pagina home din interfata grafica
 */
public class HomeStatisticsPanel extends VBox {
    /**
     * Constructor pentru clasa HomeStatisticsPanel - gestioneaza si creeaza sectiunea de statistici din pagina Home din interfata grafica
     * @param onOpenStats Runnable; callback pentru cazul in care se da click pe sectiune pentru a putea fi deschisa pagina de statistici
     */
    public HomeStatisticsPanel(Runnable onOpenStats) {
        setSpacing(8);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        LocalDate now = LocalDate.now();
        int books = StatisticiService.countFinishedBooksInMonth(now.getYear(), now.getMonthValue());
        int pages = StatisticiService.sumPagesReadInMonth(now.getYear(), now.getMonthValue());

        Label title = new Label("Statistici luna curenta");
        title.getStyleClass().add("card-title");

        Label booksLbl = new Label("Carti citite: " + books);
        Label pagesLbl = new Label("Pagini citite: " + pages);
        Label hint = new Label("Click pentru detalii");
        hint.getStyleClass().add("hint");

        getChildren().addAll(title, booksLbl, pagesLbl, hint);

        setOnMouseClicked(e -> { if (onOpenStats != null) onOpenStats.run();});
    }
}
