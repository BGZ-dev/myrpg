package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Elemento;
import java.util.Scanner;

/**
 * Mago: feitiços a longa distância. Pergunta ao jogador qual elemento usar.
 */
public class Mago implements Classe {

    @Override
    public String getNome() { return "Mago"; }

    @Override
    public int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo) {
        int bonus = (int)Math.round(heroi.getInteligencia() * 0.4);
        return danoBase + bonus;
    }

    @Override
    public int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante) { return danoRecebido; }

    @Override
    public void aplicarBuffInicial(Heroi heroi) { }

    @Override
    public void aoFinalDoTurno(Heroi heroi, Inimigo inimigo) { }

    @Override
    public AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        AcaoResultado r = new AcaoResultado();
        System.out.println("Escolha o elemento do feitiço:");
        System.out.println("1.Fogo 2.Água 3.Terra 4.Ar 5.Luz 6.Sombra 7.Raio 8.Gelo");
        int escolha = 1;
        try {
            escolha = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
        }
        Elemento elemento = Elemento.values()[Math.max(0, Math.min(7, escolha-1))];
        int dano = (int)Math.round(heroi.getInteligencia() * 2.0 + heroi.getAtaque() * 0.5);
        r.danoAoInimigo = dano;
        r.mensagem = "🔮 Mago lança feitiço de " + elemento + " causando " + dano + " dano!";
        return r;
    }
}