package UI;

import Helpers.HelperBookCard;
import Modele_de_date.Carte;
import Modele_de_date.Status;
import Services.BookSelectService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.function.Consumer;

/**
 * Clasa responsabila pentru afisarea cartilor in biblioteca
 */
public class BookListPanel extends VBox {
    private final TilePane grid = new TilePane();
    private final Consumer<Carte> onBookSelected;
    private Status filterStatus;

    /**
     * Constructor pentru creareea spatiului de afisare si afisarea cartilor in biblioteca
     * @param onBookSelected {@code Consumer<Carte>} - callback pentru a transmite informatiile legate de carti
     */
    public BookListPanel(Consumer<Carte> onBookSelected) {
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
     * Metoda ce este responsabila de afisarea cartilor
     */
    private void loadBooks() {
        List<Carte> books = filterStatus == null
                ? BookSelectService.fetchBooksFromDB()
                : BookSelectService.BookFilter(filterStatus);
        grid.getChildren().clear();
        for (Carte book : books) {
            grid.getChildren().add(HelperBookCard.createBookCard(book, onBookSelected));
        }
    }

    /**
     * Metoda folosita pentru a reincarca cartile din  biblioteca
     */
    public void refresh() {
        loadBooks();
    }

    /**
     * Metoda ce seteaza statusul la filtru
     * @param status Status; statusul care urmeaza a fi setat la filtru
     */
    public void setFilterStatus(Status status) {
        this.filterStatus = status;
    }


}
