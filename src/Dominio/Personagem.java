package Dominio;

/**
 * DOMÍNIO
 * Classe base abstrata para todas as entidades vivas do jogo.
 * Centraliza os atributos e ações comuns, como vida, ataque, defesa e agora
 * atributos primários: força, destreza, constituição, inteligência e sorte.
 */
public abstract class Personagem {
    protected String nome;
    protected int vida;
    protected int ataque;
    protected int defesa;
    protected Elemento elemento;

    // Novos atributos primários
    protected int forca;
    protected int destreza;
    protected int constituicao;
    protected int inteligencia;
    protected int sorte;

    /**
     * Construtor legado (compatível com uso existente).
     * Mantém comportamento anterior para callers que ainda fornecem ataque/defesa diretamente.
     * Deriva atributos primários a partir de ataque/defesa para compatibilidade.
     */
    public Personagem(String nome, int vida, int ataque, int defesa, Elemento elemento) {
        this.nome = nome;
        this.vida = vida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.elemento = elemento;

        // Deriva atributos básicos para retrocompatibilidade
        this.forca = Math.max(1, ataque / 2);
        this.destreza = Math.max(1, ataque / 4);
        this.constituicao = Math.max(1, defesa / 2);
        this.inteligencia = Math.max(1, ataque / 4);
        this.sorte = 1;
    }

    /**
     * Novo construtor que recebe atributos primários diretamente.
     * Calcula ataque e defesa a partir desses atributos.
     */
    public Personagem(String nome, int vida, int forca, int destreza, int constituicao, int inteligencia, int sorte, Elemento elemento) {
        this.nome = nome;
        this.vida = vida;
        this.forca = Math.max(1, forca);
        this.destreza = Math.max(1, destreza);
        this.constituicao = Math.max(1, constituicao);
        this.inteligencia = Math.max(0, inteligencia);
        this.sorte = Math.max(0, sorte);
        this.elemento = elemento;

        // Fórmula simples para derivar ataque/defesa a partir dos atributos:
        this.ataque = this.forca * 2 + this.destreza;       // Força ponderada para dano, destreza acrescenta precisão/variação
        this.defesa = this.constituicao * 2;                // Constituição determina resistência
    }

    public boolean estaVivo() { return vida > 0; }
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public Elemento getElemento() { return elemento; }
    public int getDefesa() { return defesa; }

    // Mantém getter de ataque (usado em status/IA)
    public int getAtaque() { return ataque; }

    // Getters dos atributos primários
    public int getForca() { return forca; }
    public int getDestreza() { return destreza; }
    public int getConstituicao() { return constituicao; }
    public int getInteligencia() { return inteligencia; }
    public int getSorte() { return sorte; }

    public void receberDano(int dano) {
        this.vida -= dano;
        if (this.vida < 0) this.vida = 0;
    }

    public abstract int calcularDanoBase();
}