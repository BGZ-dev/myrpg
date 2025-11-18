package Dominio;

import java.util.Random;

/**
 * DOMÍNIO
 * Representa o jogador. Estende Personagem e adiciona lógicas
 * específicas como nível, experiência e poções.
 *
 * Suporta:
 * - Construtor legado (nome, arma) para compatibilidade.
 * - Novo construtor que recebe atributos primários (força, destreza, constituição).
 */
public class Heroi extends Personagem {
    private int nivel;
    private int experiencia;
    private int potesDeCura;
    private Arma arma;
    private Random rand = new Random();

    // Fatores de balanceamento (ajuste conforme necessário)
    private static final double MULT_FORCA_DANO = 1.5;
    private static final double MULT_DEX_DANO = 1.4;
    private static final double CRIT_CHANCE_BASE = 0.05; // 5% base
    private static final double CRIT_BONUS = 1.5; // 50% a mais no dano crítico

    /**
     * Construtor legado (mantido para compatibilidade).
     * Usa valores base e a arma adiciona um bônus simples à força.
     */
    public Heroi(String nome, Arma arma) {
        super(
                nome,
                100 + 10 * 2,                      // vida temporária, será recalculada abaixo
                10 + arma.getBonusAtaque(),        // força base recebe bônus da arma (modelagem simples)
                8,                                 // destreza base
                10,                                // constituição base
                null
        );
        this.arma = arma;
        this.nivel = 1;
        this.experiencia = 0;
        this.potesDeCura = 3;

        // Recalcula vida/ataque/defesa derivados corretamente
        this.ataque = this.forca * 2 + this.destreza;
        this.defesa = this.constituicao * 2;
        this.vida = 100 + (nivel - 1) * 20 + this.constituicao * 2;
    }

    /**
     * Novo construtor que permite criar um herói com atributos primários explícitos.
     * Usar quando o jogador distribuir pontos (forca/destreza/constituicao).
     */
    public Heroi(String nome, Arma arma, int forcaInicial, int destrezaInicial, int constituicaoInicial) {
        super(
                nome,
                // Vida base inicial já considera constituição
                100 + constituicaoInicial * 2,
                forcaInicial,
                destrezaInicial,
                constituicaoInicial,
                null
        );
        this.arma = arma;
        this.nivel = 1;
        this.experiencia = 0;
        this.potesDeCura = 3;

        // Atualiza stats derivados para coerência
        this.ataque = this.forca * 2 + this.destreza;
        this.defesa = this.constituicao * 2;
        // Define vida inicial como vida máxima do nível 1
        this.vida = 100 + (nivel - 1) * 20 + this.constituicao * 2;
    }

    @Override
    public int calcularDanoBase() {
        // Calcula dano base considerando tipo da arma:
        double dano = this.ataque; // valor base derivado de atributos

        Arma.TipoArma tipo = arma != null ? arma.getTipo() : Arma.TipoArma.NEUTRA;
        double escala = arma != null ? arma.getEscala() : 1.0;

        switch (tipo) {
            case FORCA -> {
                dano += this.forca * MULT_FORCA_DANO * escala;
                // pequena variação aleatória baseada na força
                dano += rand.nextInt(Math.max(1, this.forca / 2));
            }
            case DESTREZA -> {
                dano += this.destreza * MULT_DEX_DANO * escala;
                // dado crítico influenciado pela destreza
                double critChance = CRIT_CHANCE_BASE + (this.destreza * 0.01); // +1% por ponto de destreza
                boolean crit = rand.nextDouble() < critChance;
                dano += rand.nextInt(Math.max(1, this.destreza));
                if (crit) {
                    dano = Math.round((float)(dano * CRIT_BONUS));
                    System.out.println("✨ Acerto crítico! (Destreza) ✨");
                }
            }
            default -> {
                // neutra: usa apenas ataque + variação pela destreza
                dano += rand.nextInt(Math.max(1, this.destreza));
            }
        }

        // garante inteiro não-negativo
        int danoFinal = Math.max(0, (int) Math.round(dano));
        return danoFinal;
    }

    public int calcularDanoEspecial() {
        // Ataque especial combina força/destreza com multiplicadores maiores
        double dano = this.ataque * 2;
        Arma.TipoArma tipo = arma != null ? arma.getTipo() : Arma.TipoArma.NEUTRA;
        double escala = arma != null ? arma.getEscala() : 1.0;

        if (tipo == Arma.TipoArma.FORCA) {
            dano += this.forca * (MULT_FORCA_DANO + 0.8) * escala;
            dano += rand.nextInt(15);
        } else if (tipo == Arma.TipoArma.DESTREZA) {
            dano += this.destreza * (MULT_DEX_DANO + 0.8) * escala;
            // maior chance de crítico no especial
            double critChance = CRIT_CHANCE_BASE + (this.destreza * 0.015);
            if (rand.nextDouble() < critChance) {
                dano *= CRIT_BONUS;
                System.out.println("💥 Crítico no ataque especial!");
            }
            dano += rand.nextInt(20);
        } else {
            dano += rand.nextInt(25);
        }

        return Math.max(0, (int) Math.round(dano));
    }

    public void curar() {
        if (potesDeCura > 0) {
            int cura = 30 + this.constituicao / 2; // constituição dá bônus de cura
            int vidaMaxima = 100 + (nivel - 1) * 20 + this.constituicao * 2;
            vida += cura;
            if (vida > vidaMaxima) vida = vidaMaxima;
            potesDeCura--;
            System.out.println(nome + " usou uma poção e curou " + cura + " de vida!");
        } else {
            System.out.println("Você não tem mais poções!");
        }
    }

    public void ganharExperiencia(int xp) {
        experiencia += xp;
        System.out.println(nome + " ganhou " + xp + " de experiência!");
        if (experiencia >= 100 * nivel) {
            experiencia -= 100 * nivel; // mantém excesso de XP
            nivel++;
            // Ao subir de nível, distribui bônus aos atributos
            forca += 2;
            destreza += 1;
            constituicao += 2;

            // atualiza stats derivados e vida
            ataque = forca * 2 + destreza;
            defesa = constituicao * 2;
            vida = 100 + (nivel - 1) * 20 + constituicao * 2;
            System.out.println("*** " + nome + " subiu para o nível " + nivel + "! ***");
        }
    }

    public void buffPermanente() {
        forca += 1;
        destreza += 1;
        constituicao += 1;
        // atualiza stats derivados
        ataque = forca * 2 + destreza;
        defesa = constituicao * 2;
        System.out.println("✨ " + nome + " ficou mais forte! (+1 FOR, +1 DES, +1 CON permanentemente)");
    }

    // Getters para a camada de View (IU)
    public int getNivel() { return nivel; }
    public int getPotesDeCura() { return potesDeCura; }
    public Arma getArma() { return arma; }
}