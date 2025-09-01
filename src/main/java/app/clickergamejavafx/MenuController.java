package app.clickergamejavafx;

import javafx.fxml.FXML;

import java.io.File;

public class MenuController {

    private GameApp mainApp; // Referência à classe principal da aplicação

    /**
     * É chamado pela GameApp para dar uma referência de volta a si mesma.
     * @param mainApp a instância principal da aplicação
     */
    public void setMainApp(GameApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Chamado quando o usuário clica no botão "New Game".
     * Pede à aplicação principal para mostrar a tela do jogo sem carregar dados.
     */
    @FXML
    private void handleNewGame() {
        // Deleta o save anterior, se existir, para garantir um novo jogo limpo
        File saveFile = new File("save.dat");
        if (saveFile.exists()) {
            saveFile.delete();
        }
        mainApp.showGameScene(false);
    }

    /**
     * Chamado quando o usuário clica no botão "Load Game".
     * Pede à aplicação principal para mostrar a tela do jogo e carregar os dados.
     */
    @FXML
    private void handleLoadGame() {
        mainApp.showGameScene(true);
    }
}

