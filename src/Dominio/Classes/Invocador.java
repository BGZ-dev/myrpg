package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Invocacao;
import Dominio.Elemento;

import java.util.Random;
import java.util.Scanner;

public class Invocador implements Classe {
    private final Random rand = new Random();

    @Override
    public String getNome() { return "Invocador"; }

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
        AcaoResultado r = new AcaoResultado();
        // Escolhe elemento aleatório para a invocação ou pergunta ao jogador
        System.out.println("Escolha elemento da invocação (1-Fogo ... 8-Gelo) ou 0 para aleatório:");
        int escolha = 0;
        try {
            escolha = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
        }
        Elemento elemento;
        if (escolha >= 1 && escolha <= 8) elemento = Elemento.values()[escolha-1];
        else elemento = Elemento.values()[rand.nextInt(Elemento.values().length)];

        // cria invocação baseada na inteligência do herói
        int vida = Math.max(5, heroi.getInteligencia() * 5 + 10);
        int forca = Math.max(1, heroi.getInteligencia() / 2 + 2);
        int destreza = Math.max(1, heroi.getDestreza() / 2);
        int constituicao = Math.max(1, heroi.getConstituicao() / 2);
        Invocacao pet = new Invocacao("Servitor(" + elemento + ")", vida, forca, destreza, constituicao, elemento, heroi);
        r.invocacao = pet;
        r.mensagem = "🐾 Invocador conjura " + pet.getNome() + " com " + pet.getVida() + " vida.";
        return r;
    }
}