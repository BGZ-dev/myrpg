package Main; // Declara o pacote

// Importa as classes dos outros pacotes que ele vai usar
import Controller.Batalha;
import Dominio.Arma;
import Dominio.Heroi;
import Dominio.Inimigo;
import Services.ArmaFactory;
import Services.InimigoFactory;

import java.util.Scanner;

/**
 * APLICAÇÃO / PONTO DE ENTRADA
 * Classe principal que inicia o jogo e gerencia o loop principal de aventura.
 * Sua única responsabilidade é dar o "start" e controlar o fluxo macro.
 */
public class RPGGame {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo ao RPG de Terminal!");
        System.out.print("Digite o nome do seu herói: ");
        String nomeHeroi = scanner.nextLine();

        Arma armaEscolhida = ArmaFactory.escolherArma(scanner);
        Heroi heroi = new Heroi(nomeHeroi, armaEscolhida);

        System.out.println("\nOlá, " + heroi.getNome() + "! Sua jornada começa agora...\n");

        // Loop principal do jogo
        while (heroi.estaVivo()) {
            Inimigo inimigo = InimigoFactory.gerarInimigoAleatorio(heroi.getNivel());

            Batalha batalha = new Batalha(heroi, inimigo, scanner);
            boolean heroiVenceu = batalha.iniciar();

            if (!heroiVenceu && !heroi.estaVivo()) {
                // Se o herói não venceu PORQUE morreu (e não por fugir de uma forma que o permita continuar)
                break;
            }
            // Se o herói venceu, o loop continua e um novo inimigo é gerado.
        }

        System.out.println("\n=== FIM DE JOGO ===");
        scanner.close();
    }
}