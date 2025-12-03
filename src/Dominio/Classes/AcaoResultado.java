package Dominio.Classes;

import Dominio.Invocacao;


public class AcaoResultado {
    public int danoAoInimigo = 0;
    public int curaAoHeroi = 0;
    public int tempAtkBuff = 0;
    public int tempAtkBuffTurnos = 0;
    public int tempDefBuff = 0;
    public int tempDefBuffTurnos = 0;
    public double lifestealBonus = 0.0;
    public int lifestealTurnos = 0;
    public Invocacao invocacao = null;
    public String mensagem = "";

    public AcaoResultado() {}
}