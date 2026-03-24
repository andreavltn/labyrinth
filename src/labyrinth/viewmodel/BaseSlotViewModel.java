package labyrinth.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import labyrinth.model.*;

public class BaseSlotViewModel implements SlotViewModel {

    private final MazeMove mazeMove;
    private final Game game;
    private final SimpleBooleanProperty magnetized = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty forbidden = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty active = new SimpleBooleanProperty(false);

    public BaseSlotViewModel(MazeMove mazeMove, Game game) {
        this.mazeMove = mazeMove;
        this.game = game;

        // le slot devient interdit si son mouvement associé correspond à celui du mouvement interdit
        game.getMaze().forbiddenMoveProperty().addListener((obs, old, newMove) -> {
            forbidden.set(mazeMove.equals(newMove));
        });
    }

    @Override // renvoie le mouvement du jeu associé au slot
    public MazeMove getMazeMove() {
        return mazeMove;
    }

    @Override // renvoie true si le slot est aimanté, false sinon
    public ObservableValue<Boolean> magnetizedProperty() {
        return magnetized;
    }

    @Override // aimante le slot
    public void magnetize() {
        if (active.get() && !forbidden.get()) { // seulement si il est actif et non interdit
            magnetized.set(true);
        }
    }

    @Override // désaimante le slot
    public void demagnetize() {
        magnetized.set(false);
    }

    @Override // renvoie true si le slot est interdit, false sinon
    public ObservableValue<Boolean> forbiddenProperty() {
        return forbidden;
    }

    @Override // joue le coup associé au slot
    public void moveMaze() {
        game.moveMaze(mazeMove); // réalise le coup
        demagnetize(); // désaimante le slot
    }

    @Override // renvoie true si le slot est actif, false sinon
    public ObservableValue<Boolean> activeProperty() {
        return active;
    }

    // rend le slot actif/inactif (la possibilité d'intéragir avec)
    public void setActive(boolean value) {
        active.set(value);
    }
}