package Dominio;

/**
 * Arma com tipo, escala e categoria.
 */
public class Arma {
    public enum TipoArma { FORCA, DESTREZA, NEUTRA }
    public enum Categoria { LAMINA, BRANCA_SEM_LAMINA, LONGA_DISTANCIA }

    private String nome;
    private int bonusAtaque;
    private String especial;
    private TipoArma tipo;
    private double escala;
    private Categoria categoria;

    public Arma(String nome, int bonusAtaque, String especial, TipoArma tipo, double escala, Categoria categoria) {
        this.nome = nome;
        this.bonusAtaque = bonusAtaque;
        this.especial = especial;
        this.tipo = tipo == null ? TipoArma.NEUTRA : tipo;
        this.escala = escala <= 0 ? 1.0 : escala;
        this.categoria = categoria == null ? Categoria.LAMINA : categoria;
    }

    public String getNome() { return nome; }
    public int getBonusAtaque() { return bonusAtaque; }
    public String getEspecial() { return especial; }
    public TipoArma getTipo() { return tipo; }
    public double getEscala() { return escala; }
    public Categoria getCategoria() { return categoria; }

    @Override
    public String toString() {
        return String.format("%s (+%d ATQ, %s, tipo=%s, escala=%.2fx, categoria=%s)",
                nome, bonusAtaque, especial, tipo, escala, categoria);
    }
}