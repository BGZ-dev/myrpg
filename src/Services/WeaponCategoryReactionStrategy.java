package Services;

import Dominio.Arma;
import Dominio.Elemento;
import Dominio.Personagem;
import Dominio.Heroi;

/**
 * Strategy composta: decide a sequência de tentativas com base na categoria da arma do defensor.
 * Exemplo:
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
        if (defensor instanceof Heroi) {
            arma = ((Heroi) defensor).getArma();
        } else {
            // Se quiser suportar armas em inimigos, ajuste aqui para verificar Inimigo também
            try {
                // tentativa segura caso Inimigo venha a expor arma mais tarde
                java.lang.reflect.Method m = defensor.getClass().getMethod("getArma");
                Object o = m.invoke(defensor);
                if (o instanceof Arma) arma = (Arma) o;
            } catch (Exception ignored) {}
        }

        Arma.Categoria cat = (arma != null) ? arma.getCategoria() : null;

        // Ordem de tentativas por categoria
        if (cat == Arma.Categoria.LAMINA) {
            result = dodge.attempt(atacante, defensor, danoBase, elemento);
            return result;
        } else if (cat == Arma.Categoria.BRANCA_SEM_LAMINA) {
            result = dodge.attempt(atacante, defensor, danoBase, elemento);
            if (result.dodged) return result;

            result = block.attempt(atacante, defensor, danoBase, elemento);
            if (result.blocked) {
                // se bloqueou, tentar contra
                ReactionResult counterR = counter.attempt(atacante, defensor, danoBase, elemento);
                // combinar resultados: soma counterDamage (se houver)
                result.counterDamage = Math.max(result.counterDamage, counterR.counterDamage);
                // concatena mensagens se houver
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
}