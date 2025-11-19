package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;

import java.util.Scanner;

/**
 * Contrato para classes do jogo.
 * executarAcao: ação ativa que o jogador pode usar durante seu turno.
 */
public interface Classe {
    String getNome();

    int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo);
    int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante);

    void aplicarBuffInicial(Heroi heroi);
    void aoFinalDoTurno(Heroi heroi, Inimigo inimigo);

    /**
     * Executa a ação ativa da classe. Retorna um AcaoResultado que descreve efeitos a aplicar.
     * Pode usar Scanner para escolhas (elemento, confirmar, etc.)
     */
    AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner);
}