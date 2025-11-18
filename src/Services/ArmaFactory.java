package Services; // Declara que pertence ao pacote Services

import Dominio.Arma;
import java.util.Scanner;

/**
 * SERVIÇO (Factory)
 * Responsável por criar e fornecer objetos do tipo Arma.
 * Agora cria armas com tipos que favorecem FORÇA ou DESTREZA.
 */
public class ArmaFactory {
    public static Arma escolherArma(Scanner scanner) {
        System.out.println("\nEscolha sua arma:");
        System.out.println("1. Espada do Amanhecer (+5 ATQ) — Tipo: FORÇA (crítico ocasional)");
        System.out.println("2. Adagas Sombrias (+3 ATQ) — Tipo: DESTREZA (alta precisão/velocidade)");
        System.out.println("3. Kunais do Silêncio (+4 ATQ) — Tipo: DESTREZA (chance de atordoar)");
        System.out.println("4. Sabre de Joyboy (+6 ATQ) — Tipo: NEUTRA (dano elemental aleatório)");
        System.out.print("Escolha (1-4): ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer do scanner

        return switch (escolha) {
            case 1 -> new Arma("Espada do Amanhecer", 5, "Crítico", Arma.TipoArma.FORCA, 1.2);
            case 2 -> new Arma("Adagas Sombrias", 3, "Perfuração", Arma.TipoArma.DESTREZA, 1.4);
            case 3 -> new Arma("Kunais do Silêncio", 4, "Atordoar", Arma.TipoArma.DESTREZA, 1.3);
            default -> new Arma("Sabre de Joyboy", 6, "Dano Elemental Aleatório", Arma.TipoArma.NEUTRA, 1.0);
        };
    }
}