package Dominio.Classes;

import Dominio.Invocacao;

/**
 * DTO que descreve o resultado de uma ação de classe.
 * Campos opcionais; Batalha interpreta e aplica o que for preenchido.
 */
public class AcaoResultado {
    public int danoAoInimigo = 0;
    public int curaAoHeroi = 0;
    public int tempAtkBuff = 0;     // incremento de ataque temporário
    public int tempAtkBuffTurnos = 0;
    public int tempDefBuff = 0;     // incremento de defesa temporário
    public int tempDefBuffTurnos = 0;
    public double lifestealBonus = 0.0; // multiplicador adicional de lifesteal temporário
    public int lifestealTurnos = 0;
    public Invocacao invocacao = null;
    public String mensagem = "";

    public AcaoResultado() {}
}