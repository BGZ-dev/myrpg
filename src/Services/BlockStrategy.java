package Services;

import Dominio.Personagem;
import Dominio.Elemento;

/**
 * Implementação de bloqueio: chance baseada em constituição, reduz dano, aplica cooldown.
 */
public class BlockStrategy implements ReactionStrategy {

    private static final double BASE_CHANCE = 0.30; // 30%
    private static final double PER_CON = 0.02;     // +2% por ponto de constituição
    private static final double BASE_REDUCTION = 0.30; // 30% redução base quando bloqueia
    private static final double PER_CON_REDUCTION = 0.01; // +1% por constituição
    private static final double MAX_REDUCTION = 0.85;
    private static final int BLOCK_COOLDOWN_TURNS = 2;

    @Override
    public ReactionResult attempt(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        ReactionResult r = new ReactionResult();

        if (defensor.getBlockCooldownRemaining() > 0) {
            r.damageTaken = danoBase;
            r.message = defensor.getNome() + " tentou bloquear, mas está em cooldown.";
            return r;
        }

        double chance = BASE_CHANCE + (defensor.getConstituicao() * PER_CON);
        if (chance > 0.95) chance = 0.95;

        double roll = Math.random();
        if (roll < chance) {
            // bloqueio bem-sucedido
            double reduction = BASE_REDUCTION + defensor.getConstituicao() * PER_CON_REDUCTION;
            if (reduction > MAX_REDUCTION) reduction = MAX_REDUCTION;
            int danoReduzido = (int) Math.round(danoBase * (1.0 - reduction));
            if (danoReduzido < 0) danoReduzido = 0;
            r.blocked = true;
            r.damageTaken = danoReduzido;
            r.message = defensor.getNome() + " bloqueou o ataque!";
            defensor.applyBlockCooldown(BLOCK_COOLDOWN_TURNS);
            return r;
        } else {
            // bloqueio falhou
            r.damageTaken = danoBase;
            r.message = defensor.getNome() + " tentou bloquear e falhou.";
            return r;
        }
    }
}