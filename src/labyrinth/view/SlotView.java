package labyrinth.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import labyrinth.viewmodel.SlotViewModel;
import labyrinth.viewmodel.TileViewModel;

// la vue d'un slot au bord du plateau
public class SlotView extends TileView {

    public SlotView(SlotViewModel slotViewModel, TileViewModel extraTile) {
        super(extraTile);

        // transparent par défaut
        setOpacity(0);

        // quand magnetized change, met à jour la transparence
        slotViewModel.magnetizedProperty().addListener((obs, old, magnetized) -> {
            setOpacity(magnetized ? 1 : 0); // visible si aimanté, transparent sinon
        });

        // image des chaines par-dessus la tuile (slot interdit)
        ImageView forbiddenImage = new ImageView();
        forbiddenImage.setFitWidth(60); // taille 60x60
        forbiddenImage.setFitHeight(60);
        forbiddenImage.setVisible(false); // invisible par défaut
        getChildren().add(forbiddenImage); // ajouté par dessus

        // en fonction de forbidden
        slotViewModel.forbiddenProperty().addListener((obs, old, forbidden) -> {
            if (forbidden) {
                forbiddenImage.setImage(new Image("/img/forbidden.png")); // image des chaines
                forbiddenImage.setVisible(true); // chaines visible
                setOpacity(0.5); // le slot devient semi-transparent
                setMouseTransparent(true); // ne capte plus la souris (slot inactif)
            } else {
                forbiddenImage.setVisible(false); // chaines invisibles
                setOpacity(0); // le slot devient invisible lui aussi
                setMouseTransparent(false); // capte la souris (slot utilisable)
            }
        });

        // la souris entre dans le slot : on aimante
        setOnMouseEntered(e -> slotViewModel.magnetize());

        // la souris quitte le slot : désaimante
        setOnMouseExited(e -> slotViewModel.demagnetize());

        // clic sur le slot : joue le coup (seulement si le slot est actif)
        setOnMouseClicked(e -> {
            if (slotViewModel.activeProperty().getValue()) {
                slotViewModel.moveMaze();
            }
        });
    }
}