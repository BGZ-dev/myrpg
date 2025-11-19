package Services;

import Dominio.Personagem;
import Dominio.Elemento;

/**
 * Implementação de esquiva: chance baseada em destreza e aplica cooldown.
 */
public class DodgeStrategy implements ReactionStrategy {

    // parâmetros de balanceamento
    private static final double BASE_CHANCE = 0.20; // 20%
    private static final double PER_DEX = 0.02;     // +2% por ponto de destreza
    private static final int DODGE_COOLDOWN_TURNS = 2;

    @Override
    public ReactionResult attempt(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        ReactionResult r = new ReactionResult();
        // Se defensor tiver cooldown de dodge ativo, não pode esquivar
        if (defensor.getDodgeCooldownRemaining() > 0) {
            r.damageTaken = danoBase;
            r.message = defensor.getNome() + " tentou esquivar, mas está em cooldown.";
            return r;
        }

        double chance = BASE_CHANCE + (defensor.getDestreza() * PER_DEX);
        if (chance > 0.90) chance = 0.90;

        double roll = Math.random();
        if (roll < chance) {
            r.dodged = true;
            r.damageTaken = 0;
            r.message = defensor.getNome() + " realizou uma esquiva perfeita!";
            // aplica cooldown
            defensor.applyDodgeCooldown(DODGE_COOLDOWN_TURNS);
            return r;
        } else {
            // falhou em esquivar
            r.damageTaken = danoBase;
            r.message = defensor.getNome() + " não conseguiu esquivar.";
            return r;
        }
    }
}