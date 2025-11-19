package Services;

import Dominio.Arma;
import Dominio.Elemento;
import Dominio.Personagem;
import Dominio.Heroi;

/**
 * Strategy composta: decide a sequência de tentativas com base na categoria da arma do defensor.
 * Versão compatível com JDKs sem switch-expression (usa switch tradicional).
 */
public class WeaponCategoryReactionStrategy {
    private final DodgeStrategy dodge = new DodgeStrategy();
    private final BlockStrategy block = new BlockStrategy();
    private final CounterAttackStrategy counter = new CounterAttackStrategy();

    public ReactionResult attemptReactions(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        ReactionResult result;

        Arma arma = null;
        if (defensor instanceof Heroi) {
            arma = ((Heroi) defensor).getArma();
        } else {
            try {
                java.lang.reflect.Method m = defensor.getClass().getMethod("getArma");
                Object o = m.invoke(defensor);
                if (o instanceof Arma) arma = (Arma) o;
            } catch (Exception ignored) {}
        }

        Arma.Categoria cat = (arma != null) ? arma.getCategoria() : null;

        if (cat == Arma.Categoria.LAMINA) {
            result = dodge.attempt(atacante, defensor, danoBase, elemento);
            return result;
        } else if (cat == Arma.Categoria.BRANCA_SEM_LAMINA) {
            result = dodge.attempt(atacante, defensor, danoBase, elemento);
            if (result.dodged) return result;

            result = block.attempt(atacante, defensor, danoBase, elemento);
            if (result.blocked) {
                ReactionResult counterR = counter.attempt(atacante, defensor, danoBase, elemento);
                result.counterDamage = Math.max(result.counterDamage, counterR.counterDamage);
                if (counterR.message != null && !counterR.message.isEmpty()) {
                    result.message = (result.message == null ? "" : result.message + " ") + counterR.message;
                }
            }
            return result;
        } else if (cat == Arma.Categoria.LONGA_DISTANCIA) {
            result = dodge.attempt(atacante, defensor, danoBase, elemento);
            if (result.dodged) return result;
            return block.attempt(atacante, defensor, danoBase, elemento);
        } else {
            // fallback: tentar esquiva primeiro
            result = dodge.attempt(atacante, defensor, danoBase, elemento);
            if (result.dodged) return result;
            return block.attempt(atacante, defensor, danoBase, elemento);
        }
    }

    /**
     * Tenta apenas a reação pedida. Retorna ReactionResult com o resultado.
     */
    public ReactionResult attemptSpecific(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento, ReactionService.ReactionType type) {
        // Retornamos sempre o resultado da strategy correspondente
        if (type == ReactionService.ReactionType.DODGE) {
            return dodge.attempt(atacante, defensor, danoBase, elemento);
        } else if (type == ReactionService.ReactionType.BLOCK) {
            return block.attempt(atacante, defensor, danoBase, elemento);
        } else if (type == ReactionService.ReactionType.COUNTER) {
            // Para COUNTER, tentamos block primeiro (counter geralmente depende de bloqueio)
            ReactionResult blockR = block.attempt(atacante, defensor, danoBase, elemento);
            if (blockR.blocked) {
                ReactionResult counterR = counter.attempt(atacante, defensor, danoBase, elemento);
                blockR.counterDamage = Math.max(blockR.counterDamage, counterR.counterDamage);
                if (counterR.message != null && !counterR.message.isEmpty()) {
                    blockR.message = (blockR.message == null ? "" : blockR.message + " ") + counterR.message;
                }
            }
            return blockR;
        } else {
            ReactionResult r = new ReactionResult();
            r.damageTaken = Math.max(0, danoBase);
            return r;
        }
    }
}