package labyrinth.view;

import labyrinth.viewmodel.CellViewModel;

// la vue d'une cellule
public class CellView extends TileView {

    public CellView(CellViewModel viewModel) {
        super(viewModel.getTileViewModel());

        // au clic
        setOnMouseClicked(e -> {
            if (viewModel.activeProperty().getValue()) { // si la cellule est active
                viewModel.movePlayer(); // déplace le joueur
            }
        });

        // au survol
        setOnMouseEntered(e -> {
            if (viewModel.activeProperty().getValue()) { // si la cellule est active
                // effet de surbrillance couleur or
                setStyle("-fx-effect: dropshadow(gaussian, gold, 10, 0.5, 0, 0);");
                toFront(); // met la cellule au premier plan
            }
        });

        setOnMouseExited(e -> {
            setStyle(""); // retire le style quand on quitte le survol
            toBack(); // repasse la cellule en arrière plan
        });
    }
}