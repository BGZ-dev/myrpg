package Dominio;

import java.util.Random;

/**
 * DOMÍNIO
 * Representa o jogador. Estende Personagem e adiciona lógicas
 * específicas como nível, experiência e poções.
 */
public class Heroi extends Personagem {
    private int nivel;
    private int experiencia;
    private int potesDeCura;
    private Arma arma;
    private Random rand = new Random();

    public Heroi(String nome, Arma arma) {
        super(nome, 100, 20 + arma.getBonusAtaque(), 10, null); // Herói não tem elemento fixo
        this.arma = arma;
        this.nivel = 1;
        this.experiencia = 0;
        this.potesDeCura = 3;
    }

    @Override
    public int calcularDanoBase() {
        // Lógica de dano do ataque normal
        return this.ataque + rand.nextInt(10);
    }

    public int calcularDanoEspecial() {
        // Lógica de dano do ataque especial
        return this.ataque * 2 + rand.nextInt(15);
    }

    public void curar() {
        if (potesDeCura > 0) {
            int cura = 30;
            vida += cura;
            int vidaMaxima = 100 + (nivel - 1) * 20;
            if (vida > vidaMaxima) vida = vidaMaxima;
            potesDeCura--;
            System.out.println(nome + " usou uma poção e curou " + cura + " de vida!");
        } else {
            System.out.println("Você não tem mais poções!");
        }
    }

    public void ganharExperiencia(int xp) {
        experiencia += xp;
        System.out.println(nome + " ganhou " + xp + " de experiência!");
        if (experiencia >= 100 * nivel) {
            nivel++;
            experiencia = 0;
            ataque += 5;
            defesa += 3;
            vida = 100 + (nivel - 1) * 20;
            System.out.println("*** " + nome + " subiu para o nível " + nivel + "! ***");
        }
    }

    public void buffPermanente() {
        ataque += 1;
        defesa += 1;
        System.out.println("✨ " + nome + " ficou mais forte! (+1 ATQ, +1 DEF permanentemente)");
    }

    // Getters para a camada de View (IU)
    public int getNivel() { return nivel; }
    public int getPotesDeCura() { return potesDeCura; }
    public Arma getArma() { return arma; }
}