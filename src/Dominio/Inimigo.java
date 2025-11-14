package Dominio;

import java.util.Random;

/**
 * DOMÍNIO
 * Representa um inimigo. Estende Personagem.
 */
public class Inimigo extends Personagem {
    private Random rand = new Random();

    public Inimigo(String nome, int vida, int ataque, int defesa, Elemento elemento) {
        super(nome, vida, ataque, defesa, elemento);
    }

    @Override
    public int calcularDanoBase() {
        return this.ataque + rand.nextInt(8);
    }
}