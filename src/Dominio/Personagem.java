package Dominio;

/**
 * DOMÍNIO
 * Classe base abstrata para todas as entidades vivas do jogo.
 * Agora inclui o atributo 'encantamento' (antes chamado sorte).
 * Mantém getSorte() por compatibilidade.
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

    // Encantamento (substitui/consolida o papel de 'sorte')
    protected int encantamento;

    /**
     * Construtor legado (compatível com uso existente).
     * Mantém comportamento anterior e deriva atributos primários.
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
        this.inteligencia = Math.max(0, ataque / 4);
        // antes sorte = 1; agora encantamento derivado de forma conservadora
        this.encantamento = Math.max(1, ataque / 4);
    }

    /**
     * Novo construtor que recebe atributos primários diretamente.
     */
    public Personagem(String nome, int vida, int forca, int destreza, int constituicao, int inteligencia, int encantamento, Elemento elemento) {
        this.nome = nome;
        this.vida = vida;
        this.forca = Math.max(1, forca);
        this.destreza = Math.max(1, destreza);
        this.constituicao = Math.max(1, constituicao);
        this.inteligencia = Math.max(0, inteligencia);
        this.encantamento = Math.max(0, encantamento);
        this.elemento = elemento;

        // derivação
        this.ataque = this.forca * 2 + this.destreza;
        this.defesa = this.constituicao * 2;
    }

    public boolean estaVivo() { return vida > 0; }
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public Elemento getElemento() { return elemento; }
    public int getDefesa() { return defesa; }
    public int getAtaque() { return ataque; }

    // Getters dos atributos primários
    public int getForca() { return forca; }
    public int getDestreza() { return destreza; }
    public int getConstituicao() { return constituicao; }
    public int getInteligencia() { return inteligencia; }

    // Novo getter: Encantamento
    public int getEncantamento() { return encantamento; }

    // Compatibilidade: mantém getSorte() retornando encantamento
    public int getSorte() { return encantamento; }

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