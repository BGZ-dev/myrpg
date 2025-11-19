package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import java.util.Scanner;

public class Barbaro implements Classe {

    @Override
    public String getNome() { return "Bárbaro"; }

    @Override
    public int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo) {
        int vidaMax = heroi.getVidaMaximaEstimada();
        double ferimentoPercent = 1.0 - ((double) heroi.getVida() / Math.max(1, vidaMax));
        double multiplicador = 1.0 + ferimentoPercent * 0.5;
        return (int)Math.round(danoBase * multiplicador);
    }

    @Override
    public int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante) { return danoRecebido; }

    @Override
    public void aplicarBuffInicial(Heroi heroi) {
        heroi.setAtaque(heroi.getAtaque() + 1);
    }

    @Override
    public void aoFinalDoTurno(Heroi heroi, Inimigo inimigo) { }

    @Override
    public AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        // Fúria Bárbara: sacrifica parte da vida atual para causar dano massivo no inimigo
        AcaoResultado r = new AcaoResultado();
        int sacrificar = Math.max(1, (int)Math.round(heroi.getVida() * 0.12));
        r.curaAoHeroi = -sacrificar; // sinal negativo para indicar perda de vida (Batalha aplica)
        int dano = heroi.calcularDanoBase() + sacrificar * 2;
        r.danoAoInimigo = dano;
        r.mensagem = "😡 Fúria Bárbara: perdeu " + sacrificar + " vida e causou " + dano + " dano!";
        return r;
    }
}