package labyrinth.view;

import javafx.scene.layout.GridPane;
import labyrinth.model.MazeMove;
import labyrinth.util.collection.Grid;
import labyrinth.viewmodel.CellViewModel;
import labyrinth.viewmodel.MazeViewModel;
import labyrinth.viewmodel.SlotViewModel;
import labyrinth.viewmodel.TileViewModel;

import java.util.List;

// la vue de la grille des tuiles + les slots
public class BoardView extends GridPane {

    public BoardView(MazeViewModel viewModel) {

        Grid<CellViewModel> cells = viewModel.getCellViewModels();
        TileViewModel extraTile = viewModel.getExtraTileViewModel();
        List<SlotViewModel> slots = viewModel.getSlotViewModels();

        // placement des cellules dans la grille
        for (int x = 0; x < cells.getWidth(); x++) {
            for (int y = 0; y < cells.getHeight(); y++) {
                CellView cellView = new CellView(cells.get(x, y)); // chaque cellule est une cellview
                add(cellView, x+1, y+1); // x+1 et y+1 pour laisser de la place aux slots
            }
        }

        // placement des slots autour des cellules
        for (SlotViewModel slot : slots) {
            MazeMove move = slot.getMazeMove(); // récupère le mouvement associé au slot
            SlotView slotView = new SlotView(slot, extraTile); // le slot

            // en fonction de la direction du mouvement, ajoute le slot au bon endroit
            switch (move.direction()) {

                // direction bas = slot en haut
                case DOWN -> add(slotView, move.index() + 1, 0); // colonne index+1, première ligne

                // direction haut = slot en bas
                case UP -> add(slotView, move.index() + 1, cells.getHeight() + 1); // colonne index+1, dernière ligne

                // direction droite = slot à gauche
                case RIGHT -> add(slotView, 0, move.index() + 1); // première colonne, ligne index+1

                // direction gauche = slot à droite
                case LEFT -> add(slotView, cells.getWidth() + 1, move.index() + 1); // dernière colonne, ligne index+1
            }
        }

        // met la grille au centre
        setMaxWidth(0);
        setMaxHeight(0);
    }
}