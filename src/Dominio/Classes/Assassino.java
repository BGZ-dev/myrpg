package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Personagem;

import java.util.Random;
import java.util.Scanner;

public class Assassino implements Classe {
    private final Random rand = new Random();

    @Override
    public String getNome() { return "Assassino"; }

    @Override
    public int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo) {
        // passivo já descrito antes
        double chance = 0.10 + heroi.getDestreza() * 0.02 + heroi.getSorte() * 0.005;
        if (chance > 0.9) chance = 0.9;
        if (rand.nextDouble() < chance) {
            return danoBase + (int)Math.round(danoBase * 0.75);
        }
        return danoBase;
    }

    @Override
    public int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante) { return danoRecebido; }

    @Override
    public void aplicarBuffInicial(Heroi heroi) { }

    @Override
    public void aoFinalDoTurno(Heroi heroi, Inimigo inimigo) { }

    @Override
    public AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        // Ação ativa: Golpe Sombrio — ataque concentrado que tem grande chance de crítico e causa dano extra baseado em Destreza
        AcaoResultado r = new AcaoResultado();
        int base = heroi.calcularDanoBase();
        int extra = (int)Math.round(heroi.getDestreza() * 1.5) + (int)Math.round(heroi.getSorte() * 0.5);
        int dano = base + extra;
        // aumenta chance de crítico na ação manualmente (aplica multiplicador)
        if (rand.nextDouble() < 0.45 + heroi.getSorte()*0.01) {
            dano = (int)Math.round(dano * 1.8);
            r.mensagem = "⚔️ Golpe Sombrio acerta com crítico!";
        } else {
            r.mensagem = "⚔️ Golpe Sombrio executado.";
        }
        r.danoAoInimigo = dano;
        return r;
    }
}