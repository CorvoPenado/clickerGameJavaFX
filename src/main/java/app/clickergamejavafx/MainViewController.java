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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MainViewController{

    private float amountRebirth = 1.0f;
    private float clickPowerPurchaseMultiplier = 1.08f;

    private float accumulateClickPower = 50.0f;
    private float accumulateAutoClickPower = 74.0f;
    private float accumulateSpeedAutoClicker = 56.0f;
    private float accumulateRebirth = 2240.0f;
    private float accumulateAutoClicker = 11200.0f;

    //Variaveis dos botões
    private boolean stateAutoClickerPuchased = false;
    private boolean stateAutoClickerVisible =  true;
    private boolean stateAutoClickerActive = false;

    //Variaveis de classe dos HashMappings - Atributos
    private FloatProperty atrClickPower = new SimpleFloatProperty(1.0f);
    private FloatProperty atrAutoClickerPower = new SimpleFloatProperty(1.0f);
    private FloatProperty atrSpeedAutoClicker = new SimpleFloatProperty(1.0f);
    private FloatProperty atrRebirth = new SimpleFloatProperty(0.0f);
    //Variaveis de classe dos HashMappings - ShoppingList > Upgrades
    private FloatProperty clickPower = new SimpleFloatProperty(accumulateClickPower);
    private FloatProperty autoClickerPower = new SimpleFloatProperty(accumulateAutoClickPower);
    private FloatProperty speedAutoClicker = new SimpleFloatProperty(accumulateSpeedAutoClicker);
    private FloatProperty rebirth = new SimpleFloatProperty(accumulateRebirth);
    private FloatProperty autoClicker = new SimpleFloatProperty(accumulateAutoClicker);

    //Timeline
    private Timeline timeline;
    private int score = 0;
    private FloatProperty money = new SimpleFloatProperty(50000.0f);
    //HashMap pra armazenar a lista de upgrades e seus preços
    private final HashMap<String,FloatProperty> shoppingList = new HashMap<>();
    //HashMap pra armazenar os atributos
    private final HashMap<String,FloatProperty> atributes =  new HashMap<>();

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
    @FXML private ToggleButton btnAutoClicker;



    @FXML
    public void initialize(){
        //Botão Speed Disable
        btnSpeedAutoClickerUpgrade.setDisable(true);

        //Money separado CASO FUTURAMENTE TENHA OUTRAS MOEDAS, ADICIONAR O MONEY EM UM HASHMAP TAMBÉM
        lblMoney.textProperty().bind(money.asString("%.2f"));

        atributes.put("btnClickPowerUpgrade",atrClickPower);
        atributes.put("btnAutoClickerPowerUpgrade", atrAutoClickerPower);
        atributes.put("btnSpeedAutoClickerUpgrade", atrSpeedAutoClicker);
        atributes.put("btnRebirthUpgrade", atrRebirth);

        Map<String, Label> atributesLabels = new HashMap<>();
        atributesLabels.put("btnClickPowerUpgrade",lblClickPower);
        atributesLabels.put("btnAutoClickerPowerUpgrade",lblAutoClickerPower);
        atributesLabels.put("btnSpeedAutoClickerUpgrade",lblSpeedAutoClicker );
        atributesLabels.put("btnRebirthUpgrade",lblRebirth);

        // Inicialização dos preços padrão das binds
        atributesLabels.forEach((key, label) -> {
            FloatProperty priceProperty = atributes.get(key);
            if (priceProperty != null) {
                label.textProperty().bind(priceProperty.asString("%.2f"));
            } else {
                System.out.println("⚠️ Nenhum atributo encontrado no atributesLabels: " + key);
            }
        });

        //Inicializar o HashMap
        shoppingList.put("btnClickPowerUpgrade", clickPower);
        shoppingList.put("btnAutoClickerPowerUpgrade", autoClickerPower);
        shoppingList.put("btnSpeedAutoClickerUpgrade", speedAutoClicker);
        shoppingList.put("btnBuyAutoClicker", autoClicker);
        shoppingList.put("btnRebirthUpgrade", rebirth);

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
        money.set(money.get() + atributes.get("btnClickPowerUpgrade").getValue());
        score++;
        lblMoney.textProperty().bind(money.asString("%.2f"));
        lblScore.setText("Clicks: "+ score);
    }

    public boolean shop(ActionEvent event){
        //Pega o Objeto - Componente que foi interagido
        //por padrão, JavaFX retorna em String o ID dos componentes setados la no FXML
        Button button = (Button) event.getSource();
        String upgradeName = button.getId();

        //verif basica pra ver se achou o id
        if (upgradeName != null) {
            //faz uma busca interna e armazena em item, parecido com as malemolencias de String
            //onde você usa comandos de conversão e tal, e internamente eles ja fazem um LOOP e verificações
            //Então é só usar, pode relacionar isso também com banco de dados, é como se fosse uma busca
            //o filter é como se fosse um 'WHERE' e o findFirst, ele pega o PRIMEIRO ID correspondente
            //caso contrário retorna null.
            Map.Entry<String, FloatProperty> item = shoppingList.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().equals(upgradeName))
                    .findFirst()
                    .orElse(null);

            //Associa a KEY com os atributos
            //parecido com o de cima só que pros atributos
            Map.Entry<String, FloatProperty> atrs = atributes.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().equals(upgradeName))
                    .findFirst()
                    .orElse(null);

            //verif do money comparado ao upgrade
            if (item != null && money.get() >= item.getValue().get()) {
                if(atrs != null){
                    //Aqui eu tive que fazer especifico pra alguns botões, mas o restante que segue o padrão de
                    //aumentar seu valor em 1.12* se mantém, alguns botões precisavam de tratamento extra
                    //porque funcionavam de forma distinta
                    if(atrs.getKey().equals("btnSpeedAutoClickerUpgrade")){
                        //Impede que passe de 0.1f
                        if(atrs.getValue().get() >= 25.0f){return false;}
                        money.set(money.get() - item.getValue().get());
                        item.getValue().set(item.getValue().get() * 1.10f);
                        atrs.getValue().set(atrs.getValue().get() + 0.05f);
                        //Atualize o RATE DA TIMELINE--Como não achei formas SIMPLES DE FAZER o TEMPO
                        //do keyframe ser atualizado, decidi fazer assim, uma pequena mudança
                        timeline.setRate(atrSpeedAutoClicker.get());
                        System.out.println("Speed Alterado com sucesso!");
                        return true;
                    }
                    if(item.getKey().equals("btnRebirthUpgrade")){
                        money.set(money.get() - item.getValue().get());
                        item.getValue().set(item.getValue().get() * 1.10f);
                        atrs.getValue().set(atrs.getValue().get()+1.0f);
                        System.out.println("Rebirth Comprado Com Sucesso!");
                        return true;
                    }
                    //Acima são os botões que usam os atributos mas tem peculiaridades
                    //Abaixo os botões genéricos onde só sobem o value, isso ja bata pra eles.
                    money.set(money.get() - item.getValue().get());
                    item.getValue().set(item.getValue().get() * 1.10f);
                    atrs.getValue().set(atrs.getValue().get() * clickPowerPurchaseMultiplier);
                    System.out.println("Atributo Modificado com sucesso!");
                }
                //Abaixo Botões que não dependem de atributos mas estão dentro do HashMap do Shop
                //Como o botão de autoclicker, ele não tem atributos, ele só precisa ser comprado uma vez
                if(item.getKey().equals("btnBuyAutoClicker")){
                    btnBuyAutoClicker.setDisable(true);
                    btnAutoClicker.setVisible(true);
                    money.set(money.get() - item.getValue().get());
                    System.out.println("Auto Clicker Comprado Com Sucesso!");
                    return true;
                }
                System.out.println("Compra realizada! Novo preço: " + item.getValue().get());
                return true; // Retorna true indicando sucesso
            }else{
                Float faltam = item.getValue().get() - money.getValue();
                System.out.println("Dinheiro insuficiente! faltam: " + faltam);
                return false;
            }
        }
        // Se a compra não foi realizada, cai aqui
        System.out.println("Compra falhou ou item não encontrado.");
        return false;
    }

    public void handleAutoClicker(){
        FloatProperty autoClickPower = atributes.get("btnAutoClickerPowerUpgrade");

        if(btnAutoClicker.isSelected()){
            System.out.println("AutoClicker Ativado");
            btnSpeedAutoClickerUpgrade.setDisable(false);
            btnAutoClicker.setText("DESATIVAR");
            //TIMER------------------------------------------------------------------------------------
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
                money.set(money.getValue() + autoClickPower.floatValue());
                score += 1;
                lblScore.setText("Clicks: " + score);
            });
            //Ainda não sei pra que serve a TimeLine, mas sei que temos que colocar o keyFrame no construtor dela
            //Imagino que TimeLine seja alguma classe que monta uma Thread paralela pra executar as funções
            //e ela recebe o keyFrame como parametro :)
            if (timeline == null){
                timeline = new Timeline(keyFrame);
                // O valor Timeline.INDEFINITE faz ele se repetir infinito. qualquer coisa só alterar pra um numero
                timeline.setCycleCount(Timeline.INDEFINITE);

            }
            btnSpeedAutoClickerUpgrade.setDisable(false);
            timeline.play();

            //FIM-TIMER-------------------------------------------------------------------------------------------
        }else{
            System.out.println("AutoClicker Desativado");
            if(timeline != null){
                btnSpeedAutoClickerUpgrade.setDisable(true);
                btnAutoClicker.setText("ATIVAR");
                timeline.stop();
            }
        }
    }

    public void handleRebirth(){
        if(!(money.get() >= rebirth.get())){
            System.out.println("Sem dinheiro suficiente, FALTAM: " + (rebirth.get() - money.get()));
            return;
        }

        clickPowerPurchaseMultiplier += 0.005;

        money.set(0.0f);
        atrRebirth.set(atrRebirth.get() + amountRebirth);
        //RESET atributos
        atrClickPower.set(1.0f);
        atrAutoClickerPower.set(1.0f);
        atrSpeedAutoClicker.set(1.0f);
        //Incremento Preço Permanente
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

        //Atualiza os botões conforme necessidade
        btnBuyAutoClicker.setDisable(false);
        btnAutoClicker.setVisible(false);
        btnSpeedAutoClickerUpgrade.setDisable(true);
        btnAutoClicker.setSelected(false);
        btnAutoClicker.setText("ATIVAR");

        timeline.stop();
        System.out.println("POWER ATUAL: " + clickPowerPurchaseMultiplier);

    }


}
