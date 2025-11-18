package Dominio;

/**
 * DOMÍNIO
 * Representa uma arma no jogo, com seus atributos.
 * Agora inclui um tipo que determina se a arma escala melhor com FORÇA ou DESTREZA.
 */
public class Arma {
    public enum TipoArma { FORCA, DESTREZA, NEUTRA }

    private String nome;
    private int bonusAtaque;
    private String especial;
    private TipoArma tipo;
    private double escala; // multiplicador opcional aplicado ao atributo relevante

    public Arma(String nome, int bonusAtaque, String especial, TipoArma tipo, double escala) {
        this.nome = nome;
        this.bonusAtaque = bonusAtaque;
        this.especial = especial;
        this.tipo = tipo == null ? TipoArma.NEUTRA : tipo;
        this.escala = escala <= 0 ? 1.0 : escala;
    }

    public String getNome() { return nome; }
    public int getBonusAtaque() { return bonusAtaque; }
    public String getEspecial() { return especial; }
    public TipoArma getTipo() { return tipo; }
    public double getEscala() { return escala; }

    @Override
    public String toString() {
        return String.format("%s (+%d ATQ, %s, tipo=%s, escala=%.2fx)", nome, bonusAtaque, especial, tipo, escala);
    }
}