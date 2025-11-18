package Services;

import Dominio.Personagem;
import Dominio.Arma;
import Dominio.Elemento;

/**
 * Strategy composta: decide a sequência de tentativas com base na categoria da arma do defensor.
 * Exemplos:
 *  - LAMINA -> apenas Dodge
 *  - BRANCA_SEM_LAMINA -> Dodge then Block (and possibly Counter)
 *  - LONGA_DISTANCIA -> Dodge then Block
 */
public class WeaponCategoryReactionStrategy {
    private final DodgeStrategy dodge = new DodgeStrategy();
    private final BlockStrategy block = new BlockStrategy();
    private final CounterAttackStrategy counter = new CounterAttackStrategy();

    public ReactionResult attemptReactions(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        ReactionResult result;

        // tenta identificar arma/categoria do defensor (se tiver)
        Arma arma = null;
        if (defensor instanceof Dominio.Heroi) {
            arma = ((Dominio.Heroi) defensor).getArma();
        }

        Arma.Categoria cat = (arma != null) ? arma.getCategoria() : null;

        // Lógica de ordem: exemplo simples
        if (cat == Arma.Categoria.LAMINA) {
            result = dodge.attempt(atacante, defensor, danoBase, elemento);
            return result;
        } else if (cat == Arma.Categoria.BRANCA_SEM_LAMINA) {
            result = dodge.attempt(atacante, defensor, danoBase, elemento);
            if (result.dodged) return result;
            result = block.attempt(atacante, defensor, danoBase, element);
            if (result.blocked) {
                // se bloqueou, tentar contra
                ReactionResult counterR = counter.attempt(atacante, defensor, danoBase, element);
                // combinar resultados (ex.: soma counterDamage)
                result.counterDamage = counterR.counterDamage;
            }
            return result;
        } else if (cat == Arma.Categoria.LONGA_DISTANCIA) {
            result = dodge.attempt(atacante, defensor, danoBase, element);
            if (result.dodged) return result;
            return block.attempt(atacante, defensor, danoBase, element);
        } else {
            // fallback: apenas dodge attempt
            return dodge.attempt(atacante, defensor, danoBase, element);
        }
    }
}