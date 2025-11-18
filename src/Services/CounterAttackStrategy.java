package Services;

import Dominio.Personagem;
import Dominio.Elemento;

/**
 * Implementação de contra-ataque: só deve ser chamada quando bloqueio for bem-sucedido
 * e a categoria da arma permitir (ex.: BRANCA_SEM_LAMINA).
 */
public class CounterAttackStrategy implements ReactionStrategy {

    @Override
    public ReactionResult attempt(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        ReactionResult r = new ReactionResult();
        // Esqueleto: calcular dano de contra (metade da constituição + metade do ataque do defensor).
        // Devolve r.counterDamage preenchido quando ocorre.
        r.damageTaken = danoBase;
        return r;
    }
}