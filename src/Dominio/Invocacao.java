package Dominio;

/**
 * Entidade simples que representa uma invocação (pet/aliado temporário).
 * Ataca o inimigo a cada turno do herói. Quando vida chega a 0, some.
 */
public class Invocacao extends Personagem {
    private final Heroi owner;
    private final Elemento elemento;

    public Invocacao(String nome, int vida, int forca, int destreza, int constituicao, Elemento elemento, Heroi owner) {
        super(nome, vida, forca, destreza, constituicao, 0, 0, elemento);
        this.owner = owner;
        this.elemento = elemento;
    }

    @Override
    public int calcularDanoBase() {
        // dano simples derivado de força/destreza
        int dano = this.ataque + (this.forca / 2) + (this.destreza / 3);
        return Math.max(1, dano);
    }

    public Heroi getOwner() { return owner; }
}