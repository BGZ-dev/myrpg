package Dominio;

/**
 * DOMÍNIO
 * Representa uma arma no jogo, com seus atributos.
 */
public class Arma {
    private String nome;
    private int bonusAtaque;
    private String especial;

    public Arma(String nome, int bonusAtaque, String especial) {
        this.nome = nome;
        this.bonusAtaque = bonusAtaque;
        this.especial = especial;
    }

    public String getNome() { return nome; }
    public int getBonusAtaque() { return bonusAtaque; }
    public String getEspecial() { return especial; }
}