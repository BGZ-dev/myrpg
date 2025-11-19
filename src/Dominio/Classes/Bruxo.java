package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import java.util.Scanner;

public class Bruxo implements Classe {

    @Override
    public String getNome() { return "Bruxo"; }

    @Override
    public int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo) { return danoBase; }

    @Override
    public int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante) { return danoRecebido; }

    @Override
    public void aplicarBuffInicial(Heroi heroi) { }

    @Override
    public void aoFinalDoTurno(Heroi heroi, Inimigo inimigo) { }

    @Override
    public AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        // Pacto Sanguíneo: perde um pouco de vida agora para ganhar lifesteal por 3 turnos
        AcaoResultado r = new AcaoResultado();
        int custo = Math.max(1, (int)Math.round(heroi.getVida() * 0.08));
        r.curaAoHeroi = -custo;
        r.lifestealBonus = 0.20; // +20% lifesteal
        r.lifestealTurnos = 3;
        r.mensagem = "🔮 Pacto Sanguíneo: perde " + custo + " vida e ganha +20% lifesteal por 3 turnos.";
        return r;
    }
}