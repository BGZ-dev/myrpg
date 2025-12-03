package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;

import java.util.Scanner;

public interface Classe {
    String getNome();

    int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo);
    int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante);

    void aplicarBuffInicial(Heroi heroi);
    void aoFinalDoTurno(Heroi heroi, Inimigo inimigo);

    AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner);
}