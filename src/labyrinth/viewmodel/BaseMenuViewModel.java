package labyrinth.viewmodel;

public class BaseMenuViewModel implements MenuViewModel {

    private final BaseGameViewModel gameViewModel;

    public BaseMenuViewModel(BaseGameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }

    @Override
    public void newGame() {
        // à implémenter plus tard
    }

    @Override
    public void exit() {
        System.exit(0); // quitte le jeu
    }

    @Override
    public void about() {
        // à implémenter plus tard
    }
}