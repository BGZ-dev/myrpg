package Dominio;

import java.util.Random;

/**
 * DOMÍNIO
 * Representa um inimigo. Estende Personagem.
 * Agora com um tipo de ataque (AttackType) que descreve o estilo do golpe: MELEE, RANGED, MAGIC, SPECIAL.
 */
public class Inimigo extends Personagem {
    private Random rand = new Random();

    public enum AttackType { MELEE, RANGED, MAGIC, SPECIAL }

    private AttackType attackType = AttackType.MELEE;

    // Construtor legado (mantém compatibilidade) — assume MELEE por padrão
    public Inimigo(String nome, int vida, int ataque, int defesa, Elemento elemento) {
        super(nome, vida, ataque, defesa, elemento);
        this.attackType = AttackType.MELEE;
    }

    // Novo construtor que aceita tipo de ataque
    public Inimigo(String nome, int vida, int ataque, int defesa, Elemento elemento, AttackType attackType) {
        super(nome, vida, ataque, defesa, elemento);
        this.attackType = attackType == null ? AttackType.MELEE : attackType;
    }

    @Override
    public int calcularDanoBase() {
        return this.ataque + rand.nextInt(8);
    }

    public AttackType getAttackType() {
        return attackType;
    }

    /**
     * Label legível/localizada do tipo de ataque (usada pela UI/console).
     */
    public String getAttackTypeLabel() {
        switch (attackType) {
            case MELEE: return "Corpo-a-corpo";
            case RANGED: return "À distância";
            case MAGIC: return "Mágico";
            case SPECIAL: return "Especial";
            default: return attackType.name();
        }
    }
}