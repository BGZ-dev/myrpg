package Dominio;


/**
 * DOMÍNIO
 * Classe base abstrata para todas as entidades vivas do jogo.
 * Centraliza os atributos e ações comuns, como vida, ataque e defesa.
 */
public abstract class Personagem {
    protected String nome;
    protected int vida;
    protected int ataque;
    protected int defesa;
    protected Elemento elemento;

    public Personagem(String nome, int vida, int ataque, int defesa, Elemento elemento) {
        this.nome = nome;
        this.vida = vida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.elemento = elemento;
    }

    public boolean estaVivo() { return vida > 0; }
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public Elemento getElemento() { return elemento; }
    public int getDefesa() { return defesa; }

    // ADICIONE ESTA LINHA PARA CORRIGIR O ERRO
    public int getAtaque() { return ataque; }

    public void receberDano(int dano) {
        this.vida -= dano;
        if (this.vida < 0) this.vida = 0;
    }

    public abstract int calcularDanoBase();
}