package UI;

import Helpers.HelperBookCard;
import Modele_de_date.Carte;
import Services.BookSelectService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import Modele_de_date.Status;
import javafx.scene.layout.TilePane;
import java.util.List;
import java.util.function.Consumer;

/**
 * Clasa ce are ca rol gestionarea si afisarea cartilor care se lectureaza in pagina Home
 */
public class ReadingBooksPanel extends VBox {
    private final Consumer<Carte> onBookSelected;
    private final TilePane grid = new TilePane();

    /**
     * Constructor pentru clasa ReadingBooksPanel - rol gestionarea si afisarea cartilor care se lectureaza in pagina Home
     * @param onBookSelected {@code Consumer<Carte>} - callback pentru transmiterea informatiilor legate de o anumita carte la pagina urmatoare
     */
    public ReadingBooksPanel(Consumer<Carte> onBookSelected) {
        this.onBookSelected = onBookSelected;
        setSpacing(12);
        setPadding(new Insets(12));

        grid.setHgap(16);
        grid.setVgap(16);
        grid.setPrefColumns(3);
        grid.setTileAlignment(Pos.TOP_LEFT);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        getChildren().add(scroll);
        loadBooks();
    }

    /**
     * Metoda ce permite reincarcarea cartilor
     */
    public void refresh() {
        loadBooks();
    }

    /**
     * Metoda ce incarca cartile care sunt in curs de citire
     */
    private void loadBooks() {
        List<Carte> books = BookSelectService.BookFilter(Status.IN_CURS_DE_CITIRE);
        grid.getChildren().clear();
        for (Carte book : books) {
            grid.getChildren().add(HelperBookCard.createBookCard(book, onBookSelected));
        }
    }

}
