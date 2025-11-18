package Services;

import Dominio.Personagem;
import Dominio.Elemento;

/**
 * Interface de estratégia: cada implementação tenta uma resposta (ex: esquiva, bloqueio, contra).
 */
public interface ReactionStrategy {
    /**
     * Tenta aplicar a reação. Retorna um ReactionResult com o que aconteceu.
     * - atacante e defensor podem ser usados para ler atributos / aplicar cooldowns.
     */
    ReactionResult attempt(Personagem atacante, Personagem defensor, int danoBase, Elemento elemento);
}