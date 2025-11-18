package Services;

import Dominio.Personagem;
import Dominio.Elemento;

/**
 * Orquestrador de reações: expõe resolveReaction(atacante, defensor, dano, elemento)
 * e delega para uma strategy baseada na arma/categoria do defensor.
 */
public class ReactionService {

    private static final WeaponCategoryReactionStrategy categoryStrategy = new WeaponCategoryReactionStrategy();

    public static ReactionResult resolveReaction(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        // Delega para a strategy que decide a sequência de reações
        return categoryStrategy.attemptReactions(atacante, defensor, danoBase, elemento);
    }
}