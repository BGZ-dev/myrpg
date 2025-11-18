package Dominio;

import java.util.Random;

/**
 * DOMÍNIO
 * Classe Chefe — extensão de Inimigo com mecânicas de boss fight:
 * - Fases / Enrage (quando a vida fica baixa)
 * - Ataques especiais (ataque poderoso, golpe avassalador)
 * - Cura ocasional
 * - Escudo temporário que reduz dano recebido
 *
 * Integração:
 * - Batalha já chama calcularDanoBase() e receberDano() em Inimigo, portanto
 *   esta implementação funciona sem mudar Batalha. O Chefe imprime suas ações
 *   diretamente para informar o jogador.
 */
public class Chefe extends Inimigo {
    private final int maxVida;
    private boolean enraged = false;
    private int shieldTurns = 0;
    private final Random rand = new Random();

    public Chefe(String nome, int vida, int ataque, int defesa, Elemento elemento) {
        super(nome, vida, ataque, defesa, elemento);
        this.maxVida = vida;
    }

    /**
     * Decide e executa a ação do chefe no seu turno.
     * Pode curar-se, erguer escudo (reduz dano nos próximos hits),
     * executar ataques especiais ou fazer ataque normal (usando Inimigo.calcularDanoBase()).
     */
    @Override
    public int calcularDanoBase() {
        // Entrar em fúria (enrage) quando abaixo de 35% da vida máxima
        if (!enraged && this.vida <= maxVida * 0.35) {
            enraged = true;
            this.ataque += 8; // buff de ataque permanente no enrage
            System.out.println("\n*** " + nome + " entrou em FÚRIA! Ataque aumentado! ***");
        }

        int roll = rand.nextInt(100);

        if (roll < 10) {
            // Cura: cura uma parte da vida máxima
            int cura = Math.min(maxVida - this.vida, maxVida / 5 + rand.nextInt(20));
            if (cura > 0) {
                this.vida += cura;
                System.out.println(nome + " usa Cura do Chefe e recupera " + cura + " de vida!");
            } else {
                System.out.println(nome + " tentou se curar, mas já está com vida máxima!");
            }
            return 0; // sem dano ao herói neste turno
        } else if (roll < 30) {
            // Ataque poderoso (alto dano)
            int dano = this.ataque * 2 + rand.nextInt(10);
            System.out.println(nome + " executa ATAQUE PODEROSO!");
            return dano;
        } else if (roll < 45) {
            // Escudo rúnico: reduz dano recebido por alguns turnos
            shieldTurns = 2 + rand.nextInt(2); // 2-3 turnos de escudo
            System.out.println(nome + " ergue um Escudo Rúnico (reduz dano recebido por " + shieldTurns + " turnos)!");
            return 0;
        } else if (roll < 55) {
            // Golpe avassalador: dano fixo maior que o normal
            int dano = this.ataque + 8 + rand.nextInt(8);
            System.out.println(nome + " desfere GOLPE AVASSALADOR!");
            return dano;
        } else {
            // Ataque normal (delegado ao Inimigo/Personagem)
            return super.calcularDanoBase();
        }
    }

    /**
     * Ao receber dano, aplica redução se escudo estiver ativo.
     * O escudo consome turnos a cada hit recebido.
     */
    @Override
    public void receberDano(int dano) {
        if (shieldTurns > 0) {
            int danoReduzido = (int) Math.round(dano * 0.6); // 40% de redução
            shieldTurns--;
            System.out.println(nome + " absorve parte do ataque com o escudo! Dano reduzido para " + danoReduzido + ".");
            super.receberDano(danoReduzido);
        } else {
            super.receberDano(dano);
        }
    }

    // Getters auxiliares (úteis caso a UI queira detalhes do chefe)
    public boolean isEnraged() { return enraged; }
    public int getMaxVida() { return maxVida; }
    public int getShieldTurns() { return shieldTurns; }
}