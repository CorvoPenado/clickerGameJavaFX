package app.clickergamejavafx;

import java.io.Serializable;

/**
 * Classe para armazenar todos os dados do jogo que precisam ser salvos.
 * Implementar Serializable permite que objetos desta classe sejam facilmente escritos em um arquivo.
 */
public class GameStateData implements Serializable {
    // Adicione uma versão para garantir compatibilidade se a classe mudar no futuro.
    private static final long serialVersionUID = 1L;

    // Variáveis do estado do jogo
    int score;
    float money;
    MainViewController.GameState currentState;

    // Variáveis dos atributos do jogador
    float atrClickPower;
    float atrAutoClickerPower;
    float atrSpeedAutoClicker;
    float atrRebirth;

    // Variáveis dos preços da loja
    float priceClickPower;
    float priceAutoClickerPower;
    float priceSpeedAutoClicker;
    float priceRebirth;
    float priceAutoClicker;

    // Acumuladores para o Rebirth
    float accumulateClickPower;
    float accumulateAutoClickPower;
    float accumulateSpeedAutoClicker;
    float accumulateRebirth;
    float accumulateAutoClicker;

    // Multiplicador de compra
    float clickPowerPurchaseMultiplier;
}
