package app.clickergamejavafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MainViewController {

    // Enum para o estado do jogo. Agora é público para ser acessado pela classe GameStateData.
    public enum GameState {
        AUTO_CLICKER_UNPURCHASED,
        AUTO_CLICKER_PURCHASED_INACTIVE,
        AUTO_CLICKER_PURCHASED_ACTIVE
    }

    private GameState currentState;
    private final String SAVE_FILE_NAME = "save.dat"; // Nome do arquivo de save

    // Game balancing and progression variables
    private float amountRebirth = 1.0f;
    private float clickPowerPurchaseMultiplier = 1.08f;

    // Accumulators for item prices after rebirth
    private float accumulateClickPower = 50.0f;
    private float accumulateAutoClickPower = 74.0f;
    private float accumulateSpeedAutoClicker = 56.0f;
    private float accumulateRebirth = 2240.0f;
    private float accumulateAutoClicker = 11200.0f;


    // --- Player Attributes ---
    private final FloatProperty atrClickPower = new SimpleFloatProperty(1.0f);
    private final FloatProperty atrAutoClickerPower = new SimpleFloatProperty(1.0f);
    private final FloatProperty atrSpeedAutoClicker = new SimpleFloatProperty(1.0f);
    private final FloatProperty atrRebirth = new SimpleFloatProperty(0.0f);

    // --- Shop Item Prices ---
    private final FloatProperty clickPower = new SimpleFloatProperty(accumulateClickPower);
    private final FloatProperty autoClickerPower = new SimpleFloatProperty(accumulateAutoClickPower);
    private final FloatProperty speedAutoClicker = new SimpleFloatProperty(accumulateSpeedAutoClicker);
    private final FloatProperty rebirth = new SimpleFloatProperty(accumulateRebirth);
    private final FloatProperty autoClicker = new SimpleFloatProperty(accumulateAutoClicker);

    // Game variables
    private Timeline timeline;
    private int score = 0;
    private final FloatProperty money = new SimpleFloatProperty(0.0f); // Inicia com 0 para um novo jogo

    // Data Structures for managing upgrades and attributes
    private final HashMap<String, FloatProperty> shoppingList = new HashMap<>();
    private final HashMap<String, FloatProperty> attributes = new HashMap<>();

    // --- FXML UI Components ---
    @FXML private Label lblMoney;
    @FXML private Label lblClickPower;
    @FXML private Label lblAutoClickerPower;
    @FXML private Label lblSpeedAutoClicker;
    @FXML private Label lblRebirth;
    @FXML private Label lblScore;
    @FXML private Button btnClickPowerUpgrade;
    @FXML private Label lblPriceClickPowerUpgrade;
    @FXML private Button btnAutoClickerPowerUpgrade;
    @FXML private Label lblPriceAutoClickerPowerUpgrade;
    @FXML private Button btnSpeedAutoClickerUpgrade;
    @FXML private Label lblPriceSpeedAutoClickerUpgrade;
    @FXML private Button btnBuyAutoClicker;
    @FXML private Label lblPriceAutoClicker;
    @FXML private Button btnRebirthUpgrade;
    @FXML private Label lblPriceRebirthUpgrade;
    @FXML private Button btnClick;
    @FXML private ToggleButton btnAutoClicker;

    @FXML
    public void initialize() {
        setupDataMaps();
        bindLabels();
        // A lógica de estado inicial agora é controlada pelo initData, que é chamado pelo GameApp.
    }

    /**
     * Ponto de entrada para o controlador após a cena ser carregada.
     * Decide se inicia um novo jogo ou carrega um existente.
     * @param shouldLoadGame true para carregar, false para novo jogo.
     */
    public void initData(boolean shouldLoadGame) {
        if (shouldLoadGame) {
            loadGame();
        } else {
            // Configurações para um novo jogo
            money.set(0.0f);
            score = 0;
            lblScore.setText("Clicks: 0");
            // Define o estado inicial para um novo jogo
            setState(GameState.AUTO_CLICKER_UNPURCHASED);
        }
    }

    private void setState(GameState newState) {
        this.currentState = newState;
        System.out.println("STATE CHANGED TO: " + newState);

        switch (currentState) {
            case AUTO_CLICKER_UNPURCHASED:
                btnBuyAutoClicker.setDisable(false);
                btnAutoClicker.setVisible(false);
                btnSpeedAutoClickerUpgrade.setDisable(true);
                btnAutoClicker.setSelected(false);
                btnAutoClicker.setText("ATIVAR");
                if (timeline != null) {
                    timeline.stop();
                }
                break;

            case AUTO_CLICKER_PURCHASED_INACTIVE:
                btnBuyAutoClicker.setDisable(true);
                btnAutoClicker.setVisible(true);
                btnSpeedAutoClickerUpgrade.setDisable(true);
                btnAutoClicker.setText("ATIVAR");
                btnAutoClicker.setSelected(false);
                if (timeline != null) {
                    timeline.stop();
                }
                break;

            case AUTO_CLICKER_PURCHASED_ACTIVE:
                btnBuyAutoClicker.setDisable(true);
                btnAutoClicker.setVisible(true);
                btnSpeedAutoClickerUpgrade.setDisable(false);
                btnAutoClicker.setText("DESATIVAR");
                btnAutoClicker.setSelected(true);
                startTimeline();
                break;
        }
    }

    /**
     * Salva o estado atual do jogo em um arquivo.
     */
    public void saveGame() {
        GameStateData data = new GameStateData();

        // Coleta todos os dados para o objeto de salvamento
        data.score = this.score;
        data.money = this.money.get();
        data.currentState = this.currentState;
        data.clickPowerPurchaseMultiplier = this.clickPowerPurchaseMultiplier;

        data.atrClickPower = this.atrClickPower.get();
        data.atrAutoClickerPower = this.atrAutoClickerPower.get();
        data.atrSpeedAutoClicker = this.atrSpeedAutoClicker.get();
        data.atrRebirth = this.atrRebirth.get();

        data.priceClickPower = this.clickPower.get();
        data.priceAutoClickerPower = this.autoClickerPower.get();
        data.priceSpeedAutoClicker = this.speedAutoClicker.get();
        data.priceRebirth = this.rebirth.get();
        data.priceAutoClicker = this.autoClicker.get();

        data.accumulateClickPower = this.accumulateClickPower;
        data.accumulateAutoClickPower = this.accumulateAutoClickPower;
        data.accumulateSpeedAutoClicker = this.accumulateSpeedAutoClicker;
        data.accumulateRebirth = this.accumulateRebirth;
        data.accumulateAutoClicker = this.accumulateAutoClicker;

        // Escreve o objeto no arquivo
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_NAME))) {
            oos.writeObject(data);
            System.out.println("Jogo salvo com sucesso em " + SAVE_FILE_NAME);
        } catch (IOException e) {
            System.out.println("Erro ao salvar o jogo: " + e.getMessage());
        }
    }

    /**
     * Carrega o estado do jogo de um arquivo.
     */
    public void loadGame() {
        File saveFile = new File(SAVE_FILE_NAME);
        if (!saveFile.exists()) {
            System.out.println("Nenhum jogo salvo encontrado. Iniciando novo jogo.");
            initData(false); // Inicia um novo jogo se não houver save
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE_NAME))) {
            GameStateData data = (GameStateData) ois.readObject();

            // Restaura todos os dados do objeto para as variáveis do jogo
            this.score = data.score;
            this.money.set(data.money);
            this.clickPowerPurchaseMultiplier = data.clickPowerPurchaseMultiplier;

            this.atrClickPower.set(data.atrClickPower);
            this.atrAutoClickerPower.set(data.atrAutoClickerPower);
            this.atrSpeedAutoClicker.set(data.atrSpeedAutoClicker);
            this.atrRebirth.set(data.atrRebirth);

            this.clickPower.set(data.priceClickPower);
            this.autoClickerPower.set(data.priceAutoClickerPower);
            this.speedAutoClicker.set(data.priceSpeedAutoClicker);
            this.rebirth.set(data.priceRebirth);
            this.autoClicker.set(data.priceAutoClicker);

            this.accumulateClickPower = data.accumulateClickPower;
            this.accumulateAutoClickPower = data.accumulateAutoClickPower;
            this.accumulateSpeedAutoClicker = data.accumulateSpeedAutoClicker;
            this.accumulateRebirth = data.accumulateRebirth;
            this.accumulateAutoClicker = data.accumulateAutoClicker;

            lblScore.setText("Clicks: " + this.score);

            // Atualiza a UI para refletir o estado carregado
            setState(data.currentState);

            System.out.println("Jogo carregado com sucesso!");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar o jogo: " + e.getMessage());
            initData(false); // Se o save estiver corrompido, inicia um novo jogo
        }
    }

    public void handleClick() {
        money.set(money.get() + attributes.get("btnClickPowerUpgrade").getValue());
        score++;
        lblScore.setText("Clicks: " + score);
    }

    public boolean shop(ActionEvent event) {
        Button button = (Button) event.getSource();
        String upgradeName = button.getId();

        if (upgradeName == null) {
            System.out.println("Compra falhou ou item não encontrado.");
            return false;
        }

        FloatProperty itemPriceProp = shoppingList.get(upgradeName);
        FloatProperty attributeProp = attributes.get(upgradeName);

        if (itemPriceProp != null && money.get() >= itemPriceProp.get()) {
            money.set(money.get() - itemPriceProp.get());

            if ("btnBuyAutoClicker".equals(upgradeName)) {
                System.out.println("Auto Clicker Comprado Com Sucesso!");
                setState(GameState.AUTO_CLICKER_PURCHASED_INACTIVE);
                return true;
            }

            if ("btnRebirthUpgrade".equals(upgradeName) && attributeProp != null) {
                itemPriceProp.set(itemPriceProp.get() * 1.10f);
                attributeProp.set(attributeProp.get() + 1.0f);
                System.out.println("Rebirth Comprado Com Sucesso!");
                return true;
            }

            if ("btnSpeedAutoClickerUpgrade".equals(upgradeName) && attributeProp != null) {
                if (attributeProp.get() >= 25.0f) {
                    money.set(money.get() + itemPriceProp.get());
                    return false;
                }
                itemPriceProp.set(itemPriceProp.get() * 1.10f);
                attributeProp.set(attributeProp.get() + 0.05f);
                if (timeline != null) {
                    timeline.setRate(atrSpeedAutoClicker.get());
                }
                System.out.println("Speed Alterado com sucesso!");
                return true;
            }

            if (attributeProp != null) {
                itemPriceProp.set(itemPriceProp.get() * 1.10f);
                attributeProp.set(attributeProp.get() * clickPowerPurchaseMultiplier);
                System.out.println("Atributo Modificado com sucesso!");
                return true;
            }

            System.out.println("Compra realizada! Novo preço: " + itemPriceProp.get());
            return true;
        } else {
            if (itemPriceProp != null) {
                float faltam = itemPriceProp.get() - money.getValue();
                System.out.println("Dinheiro insuficiente! faltam: " + faltam);
            }
            return false;
        }
    }


    public void handleAutoClicker() {
        if (btnAutoClicker.isSelected() && currentState == GameState.AUTO_CLICKER_PURCHASED_INACTIVE) {
            setState(GameState.AUTO_CLICKER_PURCHASED_ACTIVE);
        } else if (!btnAutoClicker.isSelected() && currentState == GameState.AUTO_CLICKER_PURCHASED_ACTIVE) {
            setState(GameState.AUTO_CLICKER_PURCHASED_INACTIVE);
        }
    }

    private void startTimeline() {
        FloatProperty autoClickPower = attributes.get("btnAutoClickerPowerUpgrade");

        if (timeline == null) {
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
                money.set(money.getValue() + autoClickPower.floatValue());
                score += 1;
                lblScore.setText("Clicks: " + score);
            });
            timeline = new Timeline(keyFrame);
            timeline.setCycleCount(Timeline.INDEFINITE);
        }
        timeline.setRate(atrSpeedAutoClicker.get());
        timeline.play();
    }


    public void handleRebirth() {
        if (money.get() < rebirth.get()) {
            System.out.println("Sem dinheiro suficiente, FALTAM: " + (rebirth.get() - money.get()));
            return;
        }

        clickPowerPurchaseMultiplier += 0.005;
        money.set(0.0f);
        atrRebirth.set(atrRebirth.get() + amountRebirth);

        atrClickPower.set(1.0f);
        atrAutoClickerPower.set(1.0f);
        atrSpeedAutoClicker.set(1.0f);

        accumulateClickPower *= 1.05f;
        clickPower.set(accumulateClickPower);
        accumulateSpeedAutoClicker *= 1.05f;
        speedAutoClicker.set(accumulateSpeedAutoClicker);
        accumulateRebirth *= 1.05f;
        rebirth.set(accumulateRebirth);
        accumulateAutoClickPower *= 1.05f;
        autoClickerPower.set(accumulateAutoClickPower);
        accumulateAutoClicker *= 1.05f;
        autoClicker.set(accumulateAutoClicker);

        setState(GameState.AUTO_CLICKER_UNPURCHASED);

        System.out.println("REBIRTH COMPLETE! New Power Multiplier: " + clickPowerPurchaseMultiplier);
    }

    private void setupDataMaps() {
        attributes.put("btnClickPowerUpgrade", atrClickPower);
        attributes.put("btnAutoClickerPowerUpgrade", atrAutoClickerPower);
        attributes.put("btnSpeedAutoClickerUpgrade", atrSpeedAutoClicker);
        attributes.put("btnRebirthUpgrade", atrRebirth);

        shoppingList.put("btnClickPowerUpgrade", clickPower);
        shoppingList.put("btnAutoClickerPowerUpgrade", autoClickerPower);
        shoppingList.put("btnSpeedAutoClickerUpgrade", speedAutoClicker);
        shoppingList.put("btnBuyAutoClicker", autoClicker);
        shoppingList.put("btnRebirthUpgrade", rebirth);
    }

    private void bindLabels() {
        lblMoney.textProperty().bind(money.asString("%.2f"));

        Map<String, Label> attributeLabels = new HashMap<>();
        attributeLabels.put("btnClickPowerUpgrade", lblClickPower);
        attributeLabels.put("btnAutoClickerPowerUpgrade", lblAutoClickerPower);
        attributeLabels.put("btnSpeedAutoClickerUpgrade", lblSpeedAutoClicker);
        attributeLabels.put("btnRebirthUpgrade", lblRebirth);

        attributeLabels.forEach((key, label) -> {
            FloatProperty priceProperty = attributes.get(key);
            if (priceProperty != null) {
                label.textProperty().bind(priceProperty.asString("%.2f"));
            }
        });

        Map<String, Label> priceLabels = new HashMap<>();
        priceLabels.put("btnClickPowerUpgrade", lblPriceClickPowerUpgrade);
        priceLabels.put("btnAutoClickerPowerUpgrade", lblPriceAutoClickerPowerUpgrade);
        priceLabels.put("btnSpeedAutoClickerUpgrade", lblPriceSpeedAutoClickerUpgrade);
        priceLabels.put("btnBuyAutoClicker", lblPriceAutoClicker);
        priceLabels.put("btnRebirthUpgrade", lblPriceRebirthUpgrade);

        priceLabels.forEach((key, label) -> {
            FloatProperty priceProperty = shoppingList.get(key);
            if (priceProperty != null) {
                label.textProperty().bind(priceProperty.asString("Price: %.2f"));
            }
        });
    }
}

