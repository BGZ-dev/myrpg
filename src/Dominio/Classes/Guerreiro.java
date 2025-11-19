package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import java.util.Scanner;

public class Guerreiro implements Classe {

    @Override
    public String getNome() { return "Guerreiro"; }

    @Override
    public int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo) { return danoBase; }

    @Override
    public int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante) {
        double reducao = 0.15 + heroi.getConstituicao() * 0.01;
        if (reducao > 0.60) reducao = 0.60;
        return (int)Math.round(danoRecebido * (1.0 - reducao));
    }

    @Override
    public void aplicarBuffInicial(Heroi heroi) {
        heroi.setDefesa(heroi.getDefesa() + 2);
    }

    @Override
    public void aoFinalDoTurno(Heroi heroi, Inimigo inimigo) { }

    @Override
    public AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        // Postura Defensiva: aumenta defesa temporariamente (próximo(s) hit(s))
        AcaoResultado r = new AcaoResultado();
        int bonus = Math.max(1, heroi.getConstituicao() / 2);
        r.tempDefBuff = bonus;
        r.tempDefBuffTurnos = 2;
        r.mensagem = "🛡️ Postura Defensiva: +" + bonus + " defesa por 2 turnos.";
        return r;
    }
}