package Services;

import Dominio.Personagem;
import Dominio.Elemento;

/**
 * Implementação de esquiva: chance baseada em destreza e aplica cooldown.
 */
public class DodgeStrategy implements ReactionStrategy {

    @Override
    public ReactionResult attempt(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento) {
        ReactionResult r = new ReactionResult();
        // Lógica de chance baseada em destreza (ex.: 20% + 2% por ponto), usa getters do Personagem
        // Deve checar e aplicar defensor.getDodgeCooldownRemaining() e set cooldown se bem-sucedido.
        // Implementação concreta: usar métodos públicos do Personagem para manipular cooldowns (applyDodgeCooldown).
        // Aqui apenas esqueleto - implemente as fórmulas que preferir.
        r.damageTaken = danoBase; // default: sem esquiva
        return r;
    }
}