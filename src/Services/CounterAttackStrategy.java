package Services;

import Dominio.Personagem;
import Dominio.Elemento;

/**
 * Implementação de contra-ataque: só deve ser chamada quando bloqueio for bem-sucedido
 * e a categoria da arma permitir (ex.: BRANCA_SEM_LAMINA).
 */
public class CounterAttackStrategy implements ReactionStrategy {

    private static final double PER_CONTRA_PER_CON = 0.02; // 2% por constituição (probabilidade extra quando usado isoladamente)
    private static final double MAX_PROB = 0.50;

    @Override
    public ReactionResult attempt(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        ReactionResult r = new ReactionResult();
        // chance de contra baseada na constituição do defensor
        double chance = Math.min(MAX_PROB, defensor.getConstituicao() * PER_CONTRA_PER_CON);
        double roll = Math.random();
        r.damageTaken = danoBase; // por padrão o defensor recebe o dano que chegou (bloqueio já teria reduzido)
        if (roll < chance) {
            // calcula dano de contra: metade da constituição + metade do ataque do defensor
            int contra = (int) Math.round(defensor.getConstituicao() * 0.5 + defensor.getAtaque() * 0.5);
            r.counterDamage = Math.max(0, contra);
            r.message = defensor.getNome() + " executou um contra-ataque!";
        } else {
            r.counterDamage = 0;
            r.message = defensor.getNome() + " tentou contra-ataque e falhou.";
        }
        return r;
    }
}