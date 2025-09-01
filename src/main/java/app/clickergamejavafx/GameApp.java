package app.clickergamejavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GameApp extends Application {

    private Stage primaryStage;
    private MainViewController gameController; // Guarda a referência do controller do jogo

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        showMenuScene(); // Inicia mostrando o menu
    }

    /**
     * Carrega e exibe a cena do menu principal.
     */
    public void showMenuScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApp.class.getResource("menu.fxml"));
        Parent root = fxmlLoader.load();

        // Passa a lógica de navegação para o MenuController
        MenuController menuController = fxmlLoader.getController();
        menuController.setMainApp(this);

        Scene scene = new Scene(root); // Tamanho definido pelo FXML
        primaryStage.setTitle("Clicker Game - Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Carrega e exibe a cena principal do jogo.
     * @param loadGame Indica se o estado do jogo deve ser carregado de um arquivo.
     */
    public void showGameScene(boolean loadGame) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GameApp.class.getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();

            // Pega a instância do controller do jogo para podermos chamar métodos nele
            gameController = fxmlLoader.getController();
            // Informa ao controller se ele deve carregar os dados ou iniciar um novo jogo
            gameController.initData(loadGame);

            Scene scene = new Scene(root); // Tamanho definido pelo FXML
            primaryStage.setTitle("Clicker Game");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Este método é chamado automaticamente quando a aplicação é fechada.
     * É o lugar ideal para salvar o progresso do jogo.
     */
    @Override
    public void stop() {
        System.out.println("Aplicação fechando, salvando o jogo...");
        if (gameController != null) {
            gameController.saveGame();
        }
    }


    public static void main(String[] args) {
        launch();
    }
}
