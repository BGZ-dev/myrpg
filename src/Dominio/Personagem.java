package Dominio;

/**
 * DOMÍNIO
 * Classe base abstrata para todas as entidades vivas do jogo.
 */
public abstract class Personagem {
    protected String nome;
    protected int vida;
    protected int ataque;
    protected int defesa;
    protected Elemento elemento;

    protected int forca;
    protected int destreza;
    protected int constituicao;
    protected int inteligencia;
    protected int sorte;

    public Personagem(String nome, int vida, int ataque, int defesa, Elemento elemento) {
        this.nome = nome;
        this.vida = vida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.elemento = elemento;

        this.forca = Math.max(1, ataque / 2);
        this.destreza = Math.max(1, ataque / 4);
        this.constituicao = Math.max(1, defesa / 2);
        this.inteligencia = Math.max(0, ataque / 4);
        this.sorte = 1;
    }

    public Personagem(String nome, int vida, int forca, int destreza, int constituicao, int inteligencia, int sorte, Elemento elemento) {
        this.nome = nome;
        this.vida = vida;
        this.forca = Math.max(1, forca);
        this.destreza = Math.max(1, destreza);
        this.constituicao = Math.max(1, constituicao);
        this.inteligencia = Math.max(0, inteligencia);
        this.sorte = Math.max(0, sorte);
        this.elemento = elemento;

        this.ataque = this.forca * 2 + this.destreza;
        this.defesa = this.constituicao * 2;
    }

    public boolean estaVivo() { return vida > 0; }
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public Elemento getElemento() { return elemento; }
    public int getDefesa() { return defesa; }
    public int getAtaque() { return ataque; }

    public int getForca() { return forca; }
    public int getDestreza() { return destreza; }
    public int getConstituicao() { return constituicao; }
    public int getInteligencia() { return inteligencia; }
    public int getSorte() { return sorte; }

    public void receberDano(int dano) {
        this.vida -= dano;
        if (this.vida < 0) this.vida = 0;
    }

    // Cura por efeitos (usada por Bruxo / efeitos)
    public void curarPor(int amount) {
        this.vida += amount;
    }

    public abstract int calcularDanoBase();
}