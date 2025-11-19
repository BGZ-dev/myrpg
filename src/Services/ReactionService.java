package Services;

import Dominio.Personagem;
import Dominio.Elemento;

/**
 * Orquestrador de reações: expõe resolveReaction(atacante, defensor, dano, elemento)
 * e attemptSpecificReaction(...) para tentar uma reação puntual (DODGE/BLOCK/COUNTER).
 */
public class ReactionService {

    private static final WeaponCategoryReactionStrategy categoryStrategy = new WeaponCategoryReactionStrategy();

    public enum ReactionType { DODGE, BLOCK, COUNTER }

    public static ReactionResult resolveReaction(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        // Delega para a strategy que decide a sequência de reações
        return categoryStrategy.attemptReactions(atacante, defensor, danoBase, elemento);
    }

    public static ReactionResult attemptSpecificReaction(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento, ReactionType type) {
        return categoryStrategy.attemptSpecific(atacante, defensor, danoBase, elemento, type);
    }
}