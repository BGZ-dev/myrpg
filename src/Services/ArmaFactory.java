package Services; // Declara que pertence ao pacote Services

import Dominio.Arma;
import java.util.Scanner;

/**
 * SERVIÇO (Factory)
 * Responsável por criar e fornecer objetos do tipo Arma.
 */
public class ArmaFactory {
    public static Arma escolherArma(Scanner scanner) {
        System.out.println("\nEscolha sua arma:");
        System.out.println("1. Espada do Amanhecer (+5 ATQ, chance de crítico)");
        System.out.println("2. Adagas Sombrias (+3 ATQ, chance de perfuração)");
        System.out.println("3. Kunais do Silêncio (+4 ATQ, chance de atordoar)");
        System.out.println("4. Sabre de Joyboy (+6 ATQ, chance de dano elemental extra)");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer do scanner

        return switch (escolha) {
            case 1 -> new Arma("Espada do Amanhecer", 5, "Crítico");
            case 2 -> new Arma("Adagas Sombrias", 3, "Perfuração");
            case 3 -> new Arma("Kunais do Silêncio", 4, "Atordoar");
            default -> new Arma("Sabre de Joyboy", 6, "Dano Elemental Aleatório");
        };
    }
}