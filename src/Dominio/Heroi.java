package Dominio;

import Dominio.Classes.Classe;
import java.util.Random;

/**
 * Herói atualizado para suportar Encantamento (antes Sorte) e encantamentos temporários
 * aplicáveis à arma ou aos punhos (energia amaldiçoada).
 */
public class Heroi extends Personagem {
    private int nivel;
    private int experiencia;
    private int potesDeCura;
    private Arma arma;
    private Random rand = new Random();
    private Classe classe; // nova

    // Campos para encantamento ativo (aplicados por Bruxo)
    private boolean encantamentoAtivo = false;
    private Elemento encantamentoElemento = null;
    private boolean encantamentoNoArma = true; // se false => punhos
    private double encantamentoMultiplicador = 0.0;
    private int encantamentoTurnos = 0;

    private static final double MULT_FORCA_DANO = 1.5;
    private static final double MULT_DEX_DANO = 1.4;
    private static final double CRIT_CHANCE_BASE = 0.03;
    private static final double CRIT_BONUS = 1.5;

    // Construtor legado (mantido)
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
    public Heroi(String nome, Arma arma, int forcaInicial, int destrezaInicial, int constituicaoInicial, int inteligenciaInicial, int encantamentoInicial, Classe classe) {
        super(nome, 100 + constituicaoInicial * 2, forcaInicial, destrezaInicial, constituicaoInicial, inteligenciaInicial, encantamentoInicial, null);
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
                double critChance = CRIT_CHANCE_BASE + (this.destreza * 0.01) + (this.getSorte() * 0.005);
                boolean crit = rand.nextDouble() < critChance;
                dano += rand.nextInt(Math.max(1, this.destreza));
                if (crit) {
                    dano = Math.round((float)(dano * CRIT_BONUS));
                    System.out.println("✨ Acerto crítico! (Destreza/Encantamento) ✨");
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

        // Aplicar encantamento ativo: aumenta dano conforme encantamento e tipo (arma/punhos)
        if (encantamentoAtivo) {
            // Potência base do encantamento dependente do atributo encantamento (antes "sorte")
            int ench = Math.max(0, this.getEncantamento());
            int bonus = (int) Math.round(ench * this.encantamentoMultiplicador);
            danoFinal += bonus;
            // mensagem curta
            System.out.println("🔰 Encantamento ativo (" + encantamentoElemento + "): +" + bonus + " dano.");
        }

        return danoFinal;
    }

    public int calcularDanoEspecial() {
        // mantemos o especial antigo como fallback/uso interno — classes podem delegar a ele se desejarem
        double dano = this.ataque * 2;
        Arma.TipoArma tipo = arma != null ? arma.getTipo() : Arma.TipoArma.NEUTRA;
        double escala = arma != null ? arma.getEscala() : 1.0;

        dano += this.inteligencia * 1.2 * escala;

        if (tipo == Arma.TipoArma.FORCA) {
            dano += this.forca * (MULT_FORCA_DANO + 0.8) * escala;
            dano += rand.nextInt(15);
        } else if (tipo == Arma.TipoArma.DESTREZA) {
            dano += this.destreza * (MULT_DEX_DANO + 0.8) * escala;
            double critChance = CRIT_CHANCE_BASE + (this.destreza * 0.015) + (this.getSorte() * 0.005);
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
            danoFinal = classe.modificarDanoSaida(this, danoFinal, null);
        }
        // encantamento também pode influenciar especiais (se ativo)
        if (encantamentoAtivo) {
            int ench = Math.max(0, this.getEncantamento());
            int bonus = (int) Math.round(ench * this.encantamentoMultiplicador);
            danoFinal += bonus;
            System.out.println("🔰 Encantamento (especial): +" + bonus + " dano.");
        }
        return danoFinal;
    }

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

    public void ganharExperiencia(int xp) {
        experiencia += xp;
        System.out.println(nome + " ganhou " + xp + " de experiência!");
        if (experiencia >= 100 * nivel) {
            experiencia -= 100 * nivel;
            nivel++;
            forca += 2;
            destreza += 1;
            constituicao += 2;
            inteligencia += 1;
            encantamento += 1; // crescimento do encantamento ao subir de nível

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
        inteligencia += 1;
        encantamento += 1;
        ataque = forca * 2 + destreza;
        defesa = constituicao * 2;
        System.out.println("✨ " + nome + " ficou mais forte! (+1 FOR, +1 DES, +1 CON, +1 INT, +1 ENC permanentemente)");
    }

    // Métodos para gerir encantamento aplicado pelo Bruxo
    public void aplicarEncantamento(Elemento elemento, boolean noArma, double multiplicador, int turnos) {
        this.encantamentoAtivo = true;
        this.encantamentoElemento = elemento;
        this.encantamentoNoArma = noArma;
        this.encantamentoMultiplicador = multiplicador;
        this.encantamentoTurnos = Math.max(1, turnos);
        System.out.println("🔰 Encantamento aplicado: " + (noArma ? "arma" : "punhos") + " | elemento: " + elemento + " | +" + multiplicador + " por " + this.encantamentoTurnos + " turnos.");
    }

    // Decrementa duração do encantamento; chamar ao fim de rodada
    public void tickEncantamento() {
        if (!encantamentoAtivo) return;
        encantamentoTurnos--;
        if (encantamentoTurnos <= 0) {
            encantamentoAtivo = false;
            encantamentoElemento = null;
            encantamentoMultiplicador = 0.0;
            encantamentoTurnos = 0;
            System.out.println("🔰 Encantamento expirou.");
        }
    }

    public boolean isEncantamentoAtivo() { return encantamentoAtivo; }
    public Elemento getEncantamentoElemento() { return encantamentoElemento; }
    public boolean isEncantamentoNoArma() { return encantamentoNoArma; }

    // Quando curar por efeitos (Bruxo), respeita vida máxima do herói
    @Override
    public void curarPor(int amount) {
        int vidaMax = 100 + (nivel - 1) * 20 + constituicao * 2;
        this.vida += amount;
        if (this.vida > vidaMax) this.vida = vidaMax;
    }

    public int getVidaMaximaEstimada() {
        return 100 + (nivel - 1) * 20 + constituicao * 2;
    }

    // getters e setters
    public int getNivel() { return nivel; }
    public int getPotesDeCura() { return potesDeCura; }
    public Arma getArma() { return arma; }
    public Classe getClasse() { return classe; }

    public void setAtaque(int a) { this.ataque = a; }
    public void setDefesa(int d) { this.defesa = d; }
}