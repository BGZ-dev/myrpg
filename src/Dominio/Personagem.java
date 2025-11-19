package Dominio;

import Dominio.Elemento;

/**
 * DOMÍNIO
 * Classe base abstrata para todas as entidades vivas do jogo.
 * Agora com suporte a cooldowns de reação (esquiva / bloqueio).
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
    protected int encantamento;

    // cooldowns de reação (em turnos)
    protected int dodgeCooldownRemaining = 0;
    protected int blockCooldownRemaining = 0;

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
        this.encantamento = Math.max(1, ataque / 4);
    }

    public Personagem(String nome, int vida, int forca, int destreza, int constituicao, int inteligencia, int encantamento, Elemento elemento) {
        this.nome = nome;
        this.vida = vida;
        this.forca = Math.max(1, forca);
        this.destreza = Math.max(1, destreza);
        this.constituicao = Math.max(1, constituicao);
        this.inteligencia = Math.max(0, inteligencia);
        this.encantamento = Math.max(0, encantamento);
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
    public int getEncantamento() { return encantamento; }
    public int getSorte() { return encantamento; } // compatibilidade

    public void receberDano(int dano) {
        this.vida -= dano;
        if (this.vida < 0) this.vida = 0;
    }

    public void curarPor(int amount) {
        this.vida += amount;
    }

    // cooldowns: getters / aplicadores / decrementador
    public int getDodgeCooldownRemaining() { return dodgeCooldownRemaining; }
    public int getBlockCooldownRemaining() { return blockCooldownRemaining; }

    public void applyDodgeCooldown(int turns) {
        this.dodgeCooldownRemaining = Math.max(this.dodgeCooldownRemaining, turns);
    }

    public void applyBlockCooldown(int turns) {
        this.blockCooldownRemaining = Math.max(this.blockCooldownRemaining, turns);
    }

    /**
     * Decrementa todos os cooldowns em 1 turno (chamar ao fim de cada rodada)
     */
    public void decrementarCooldowns() {
        if (dodgeCooldownRemaining > 0) dodgeCooldownRemaining--;
        if (blockCooldownRemaining > 0) blockCooldownRemaining--;
    }

    public abstract int calcularDanoBase();
}