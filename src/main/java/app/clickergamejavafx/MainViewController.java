package app.clickergamejavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainViewController{
    private int score = 0;
    private FloatProperty money = new SimpleFloatProperty(50.0f);
    private float clickPower = 1.0f;
    private float autoClickerPower = 1.0f;
    private float speedAutoClicker = 1.0f;
    private int rebirth = 0;

    //HashMap
    private final HashMap<String,FloatProperty> shoppingList = new HashMap<>();

    //LABELS
    @FXML private Label lblMoney;
    @FXML private Label lblClickPower;
    @FXML private Label lblAutoClickerPower;
    @FXML private Label lblSpeedAutoClicker;
    @FXML private Label lblRebirth;
    @FXML private Label lblScore;
    //Botões-Labels
    //--------------------------
    @FXML private Button btnClickPowerUpgrade;
    @FXML private Label lblPriceClickPowerUpgrade;
    //--------------------------
    @FXML private Button btnAutoClickerPowerUpgrade;
    @FXML private Label lblPriceAutoClickerPowerUpgrade;
    //--------------------------
    @FXML private Button btnSpeedAutoClickerUpgrade;
    @FXML private Label lblPriceSpeedAutoClickerUpgrade;
    //--------------------------
    @FXML private Button btnBuyAutoClicker;
    @FXML private Label lblPriceAutoClicker;
    //--------------------------
    @FXML private Button btnRebirthUpgrade;
    @FXML private Label lblPriceRebirthUpgrade;
    //--------------------------
    @FXML private Button btnClick;

    @FXML
    public void initialize(){
        lblMoney.textProperty().bind(money.asString("Money: %.2f"));
        lblClickPower.setText(String.valueOf(clickPower));
        lblAutoClickerPower.setText(String.valueOf(autoClickerPower));
        lblSpeedAutoClicker.setText(String.valueOf(speedAutoClicker));
        lblRebirth.setText("Rebirth: " + rebirth);
        lblScore.setText("Clicks: "+ score);

        //Inicializar o HashMap
        shoppingList.put("btnClickPowerUpgrade", new SimpleFloatProperty(50.0f));
        shoppingList.put("btnAutoClickerPowerUpgrade", new SimpleFloatProperty(74.0f));
        shoppingList.put("btnSpeedAutoClickerUpgrade", new SimpleFloatProperty(50.0f));
        shoppingList.put("btnBuyAutoClicker", new SimpleFloatProperty(25000.0f));
        shoppingList.put("btnRebirthUpgrade", new SimpleFloatProperty(1000.0f));

        //Mapeia os preços
        Map<String, Label> priceLabels = new HashMap<>();
        priceLabels.put("btnClickPowerUpgrade", lblPriceClickPowerUpgrade);
        priceLabels.put("btnAutoClickerPowerUpgrade", lblPriceAutoClickerPowerUpgrade);
        priceLabels.put("btnSpeedAutoClickerUpgrade", lblPriceSpeedAutoClickerUpgrade);
        priceLabels.put("btnBuyAutoClicker", lblPriceAutoClicker);
        priceLabels.put("btnRebirthUpgrade", lblPriceRebirthUpgrade);

        // Inicialização dos preços padrão das binds
        priceLabels.forEach((key, label) -> {
            FloatProperty priceProperty = shoppingList.get(key);
            if (priceProperty != null) {
                label.textProperty().bind(priceProperty.asString("Price: %.2f"));
            } else {
                System.out.println("⚠️ Nenhum preço encontrado no shoppingList para a key: " + key);
            }
        });

    }



    public void handleClick(){
        money.set(money.get() + clickPower);
        score++;
        lblMoney.textProperty().bind(money.asString("Money: %.2f"));
        lblScore.setText("Clicks: "+ score);
    }

    public boolean shop(ActionEvent event){
        // 1. Obtém o objeto que disparou o evento
        Button button = (Button) event.getSource();
        String upgradeName = button.getId();

        // 3. Verifica se o nome do upgrade foi encontrado no UserData
        if (upgradeName != null) {
            // 4. Pega o item do shoppingList usando o nome do upgrade como chave
            //    Isso elimina a necessidade do loop for-each
            Map.Entry<String, FloatProperty> item = shoppingList.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().equals(upgradeName))
                    .findFirst()
                    .orElse(null);

            // 5. Verifica se o item existe e se o dinheiro é suficiente
            if (item != null && money.get() >= item.getValue().get()) {
                // A compra é realizada aqui
                money.set(money.get() - item.getValue().get());
                item.getValue().set(item.getValue().get() * 1.12f);
                System.out.println("Compra realizada! Novo preço: " + item.getValue().get());
                return true; // Retorna true indicando sucesso
            }
        }

        // Se a compra não foi realizada, cai aqui
        System.out.println("Compra falhou ou item não encontrado.");
        return false;
    }

}
