package Dominio;

import Dominio.Classes.Classe;
import java.util.Random;

/**
 * Classe Heroi (implementação compatível com Batalha).
 * Contém:
 * - public void ganharExperiencia(int xp)
 * - public void buffPermanente()
 * - public int calcularDanoEspecial()
 * - public void curar()
 *
 * Mantém também construtor legado e novo construtor que aceita Classe.
 */
public class Heroi extends Personagem {
    private int nivel;
    private int experiencia;
    private int potesDeCura;
    private Arma arma;
    private Random rand = new Random();
    private Classe classe; // opcional

    private static final double MULT_FORCA_DANO = 1.5;
    private static final double MULT_DEX_DANO = 1.4;
    private static final double CRIT_CHANCE_BASE = 0.03;
    private static final double CRIT_BONUS = 1.5;

    // Construtor legado (mantido para compatibilidade)
    public Heroi(String nome, Arma arma) {
        super(nome, 100 + 10 * 2, 10 + arma.getBonusAtaque(), 8, 10, 8, 5, null);
        this.arma = arma;
        this.nivel = 1;
        this.experiencia = 0;
        this.potesDeCura = 3;
        this.classe = null;
        this.ataque = this.forca * 2 + this.destreza;
        this.defesa = this.constituicao * 2;
        this.vida = 100 + (nivel - 1) * 20 + this.constituicao * 2;
    }

    // Novo construtor que aceita Classe e atributos primários
    public Heroi(String nome, Arma arma, int forcaInicial, int destrezaInicial, int constituicaoInicial, int inteligenciaInicial, int sorteInicial, Classe classe) {
        super(nome, 100 + constituicaoInicial * 2, forcaInicial, destrezaInicial, constituicaoInicial, inteligenciaInicial, sorteInicial, null);
        this.arma = arma;
        this.nivel = 1;
        this.experiencia = 0;
        this.potesDeCura = 3;
        this.classe = classe;
        this.ataque = this.forca * 2 + this.destreza;
        this.defesa = this.constituicao * 2;
        this.vida = 100 + (nivel - 1) * 20 + this.constituicao * 2;
        if (this.classe != null) {
            this.classe.aplicarBuffInicial(this);
            System.out.println("🔰 Classe escolhida: " + this.classe.getNome());
        }
    }

    @Override
    public int calcularDanoBase() {
        double dano = this.ataque;
        Arma.TipoArma tipo = arma != null ? arma.getTipo() : Arma.TipoArma.NEUTRA;
        double escala = arma != null ? arma.getEscala() : 1.0;

        switch (tipo) {
            case FORCA -> {
                dano += this.forca * MULT_FORCA_DANO * escala;
                dano += rand.nextInt(Math.max(1, this.forca / 2));
            }
            case DESTREZA -> {
                dano += this.destreza * MULT_DEX_DANO * escala;
                double critChance = CRIT_CHANCE_BASE + (this.destreza * 0.01) + (this.sorte * 0.005);
                boolean crit = rand.nextDouble() < critChance;
                dano += rand.nextInt(Math.max(1, this.destreza));
                if (crit) {
                    dano = Math.round((float)(dano * CRIT_BONUS));
                    System.out.println("✨ Acerto crítico! (Destreza/Sorte) ✨");
                }
            }
            default -> {
                dano += rand.nextInt(Math.max(1, this.destreza));
            }
        }

        int danoFinal = Math.max(0, (int) Math.round(dano));

        // Aplicar modificação da classe (se houver)
        if (classe != null) {
            danoFinal = classe.modificarDanoSaida(this, danoFinal, null);
        }
        return danoFinal;
    }

    // método chamado por Batalha para ataques especiais
    public int calcularDanoEspecial() {
        double dano = this.ataque * 2;
        Arma.TipoArma tipo = arma != null ? arma.getTipo() : Arma.TipoArma.NEUTRA;
        double escala = arma != null ? arma.getEscala() : 1.0;

        // Inteligência contribui para o especial
        dano += this.inteligencia * 1.2 * escala;

        if (tipo == Arma.TipoArma.FORCA) {
            dano += this.forca * (MULT_FORCA_DANO + 0.8) * escala;
            dano += rand.nextInt(15);
        } else if (tipo == Arma.TipoArma.DESTREZA) {
            dano += this.destreza * (MULT_DEX_DANO + 0.8) * escala;
            double critChance = CRIT_CHANCE_BASE + (this.destreza * 0.015) + (this.sorte * 0.005);
            if (rand.nextDouble() < critChance) {
                dano *= CRIT_BONUS;
                System.out.println("💥 Crítico no ataque especial!");
            }
            dano += rand.nextInt(20);
        } else {
            dano += rand.nextInt(25);
        }

        int danoFinal = Math.max(0, (int) Math.round(dano));
        if (classe != null) {
            // permite que a classe modifique o especial se desejar (usa modificarDanoSaida)
            danoFinal = classe.modificarDanoSaida(this, danoFinal, null);
        }
        return danoFinal;
    }

    // método chamado por Batalha quando jogador escolhe "Usar Poção"
    public void curar() {
        if (potesDeCura > 0) {
            int cura = 30 + this.constituicao / 2;
            int vidaMaxima = 100 + (nivel - 1) * 20 + this.constituicao * 2;
            vida += cura;
            if (vida > vidaMaxima) vida = vidaMaxima;
            potesDeCura--;
            System.out.println(nome + " usou uma poção e curou " + cura + " de vida!");
        } else {
            System.out.println("Você não tem mais poções!");
        }
    }

    // método chamado por Batalha ao final de uma vitória para adicionar XP
    public void ganharExperiencia(int xp) {
        experiencia += xp;
        System.out.println(nome + " ganhou " + xp + " de experiência!");
        if (experiencia >= 100 * nivel) {
            experiencia -= 100 * nivel; // mantem excesso
            nivel++;
            // bônus ao subir de nível
            forca += 2;
            destreza += 1;
            constituicao += 2;
            inteligencia += 1;
            sorte += 1;

            ataque = forca * 2 + destreza;
            defesa = constituicao * 2;
            vida = 100 + (nivel - 1) * 20 + constituicao * 2;
            System.out.println("*** " + nome + " subiu para o nível " + nivel + "! ***");
        }
    }

    // chamado por Batalha após vitória para "dar um pequeno buff permanente"
    public void buffPermanente() {
        forca += 1;
        destreza += 1;
        constituicao += 1;
        inteligencia += 1;
        sorte += 1;
        ataque = forca * 2 + destreza;
        defesa = constituicao * 2;
        System.out.println("✨ " + nome + " ficou mais forte! (+1 FOR, +1 DES, +1 CON, +1 INT, +1 SORTE permanentemente)");
    }

    // cura aplicada por efeitos (ex.: Bruxo lifesteal)
    @Override
    public void curarPor(int amount) {
        int vidaMax = 100 + (nivel - 1) * 20 + constituicao * 2;
        this.vida += amount;
        if (this.vida > vidaMax) this.vida = vidaMax;
    }

    // helper para outras classes estimarem vida máxima
    public int getVidaMaximaEstimada() {
        return 100 + (nivel - 1) * 20 + constituicao * 2;
    }

    // getters e setters usados pelo restante do código
    public int getNivel() { return nivel; }
    public int getPotesDeCura() { return potesDeCura; }
    public Arma getArma() { return arma; }
    public Classe getClasse() { return classe; }

    public void setAtaque(int a) { this.ataque = a; }
    public void setDefesa(int d) { this.defesa = d; }
}