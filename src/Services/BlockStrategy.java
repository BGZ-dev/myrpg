package Services;

import Dominio.Personagem;
import Dominio.Elemento;

/**
 * Implementação de bloqueio: chance baseada em constituição, reduz dano, aplica cooldown.
 */
public class BlockStrategy implements ReactionStrategy {

    @Override
    public ReactionResult attempt(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        ReactionResult r = new ReactionResult();
        // Esqueleto: calcular chance de bloqueio, se sucesso reduzir dano e aplicar cooldown (defensor.applyBlockCooldown(...))
        r.damageTaken = danoBase; // default
        return r;
    }
}