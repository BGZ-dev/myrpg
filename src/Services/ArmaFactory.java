package Services;

import Dominio.Arma;
import java.util.Scanner;

/**
 * SERVIÇO (Factory)
 * Responsável por criar e fornecer objetos do tipo Arma.
 * Atualizado para usar o novo construtor que inclui Categoria.
 */
public class ArmaFactory {
    public static Arma escolherArma(Scanner scanner) {
        System.out.println("\nEscolha sua arma:");
        System.out.println("1. Espada do Amanhecer (+5 ATQ) — Tipo: FORCA (Crítico ocasional) — Categoria: LAMINA");
        System.out.println("2. Adagas Sombrias (+3 ATQ) — Tipo: DESTREZA (Perfuração) — Categoria: BRANCA_SEM_LAMINA");
        System.out.println("3. Kunais do Silêncio (+4 ATQ) — Tipo: DESTREZA (Atordoar) — Categoria: LONGA_DISTANCIA");
        System.out.println("4. Sabre de Joyboy (+6 ATQ) — Tipo: NEUTRA (Dano Elemental Aleatório) — Categoria: LAMINA");
        System.out.print("Escolha (1-4): ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer do scanner

        return switch (escolha) {
            case 1 -> new Arma("Espada do Amanhecer", 5, "Crítico", Arma.TipoArma.FORCA, 1.2, Arma.Categoria.LAMINA);
            case 2 -> new Arma("Adagas Sombrias", 3, "Perfuração", Arma.TipoArma.DESTREZA, 1.4, Arma.Categoria.BRANCA_SEM_LAMINA);
            case 3 -> new Arma("Kunais do Silêncio", 4, "Atordoar", Arma.TipoArma.DESTREZA, 1.3, Arma.Categoria.LONGA_DISTANCIA);
            default -> new Arma("Sabre de Joyboy", 6, "Dano Elemental Aleatório", Arma.TipoArma.NEUTRA, 1.0, Arma.Categoria.LAMINA);
        };
    }
}