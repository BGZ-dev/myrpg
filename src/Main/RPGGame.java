package Main;

import Controller.Batalha;
import Dominio.Arma;
import Dominio.Heroi;
import Dominio.Inimigo;
import Services.ArmaFactory;
import Services.InimigoFactory;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * APLICAÇÃO / PONTO DE ENTRADA
 * Atualizada para permitir que o jogador distribua até 20 pontos entre Força, Destreza,
 * Constituição, Inteligência e Sorte no momento da criação.
 */
public class RPGGame {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo ao RPG de Terminal!");
        System.out.print("Digite o nome do seu herói: ");
        String nomeHeroi = scanner.nextLine();

        Arma armaEscolhida = ArmaFactory.escolherArma(scanner);

        final int pontosTotais = 20;
        int forcaExtra = 0, destrezaExtra = 0, constituicaoExtra = 0, inteligenciaExtra = 0, sorteExtra = 0;

        System.out.println("\nVocê tem até " + pontosTotais + " pontos para distribuir entre Força, Destreza, Constituição, Inteligência e Sorte.");
        System.out.println("Valores base: Força=10 + bônus da arma, Destreza=8, Constituição=10, Inteligência=8, Sorte=5.");

        while (true) {
            try {
                System.out.print("Pontos para Força (inteiro >= 0): ");
                forcaExtra = scanner.nextInt();
                System.out.print("Pontos para Destreza (inteiro >= 0): ");
                destrezaExtra = scanner.nextInt();
                System.out.print("Pontos para Constituição (inteiro >= 0): ");
                constituicaoExtra = scanner.nextInt();
                System.out.print("Pontos para Inteligência (inteiro >= 0): ");
                inteligenciaExtra = scanner.nextInt();
                System.out.print("Pontos para Sorte (inteiro >= 0): ");
                sorteExtra = scanner.nextInt();
                scanner.nextLine(); // limpa buffer

                int soma = forcaExtra + destrezaExtra + constituicaoExtra + inteligenciaExtra + sorteExtra;
                if (forcaExtra < 0 || destrezaExtra < 0 || constituicaoExtra < 0 || inteligenciaExtra < 0 || sorteExtra < 0) {
                    System.out.println("Não pode usar números negativos. Tente novamente.");
                    continue;
                }
                if (soma > pontosTotais) {
                    System.out.println("A soma dos pontos não pode exceder " + pontosTotais + " (você atribuiu " + soma + "). Tente novamente.");
                    continue;
                }
                break; // válido
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Use números inteiros. Vamos tentar novamente.");
                scanner.nextLine(); // descarta token inválido
            }
        }

        // Base de atributos do herói (compatível com os valores anteriores)
        int baseForca = 10 + armaEscolhida.getBonusAtaque();
        int baseDestreza = 8;
        int baseConstituicao = 10;
        int baseInteligencia = 8;
        int baseSorte = 5;

        int forcaFinal = baseForca + forcaExtra;
        int destrezaFinal = baseDestreza + destrezaExtra;
        int constituicaoFinal = baseConstituicao + constituicaoExtra;
        int inteligenciaFinal = baseInteligencia + inteligenciaExtra;
        int sorteFinal = baseSorte + sorteExtra;

        System.out.println("\nAtributos finais:");
        System.out.println(" Força: " + forcaFinal);
        System.out.println(" Destreza: " + destrezaFinal);
        System.out.println(" Constituição: " + constituicaoFinal);
        System.out.println(" Inteligência: " + inteligenciaFinal);
        System.out.println(" Sorte: " + sorteFinal);

        Heroi heroi = new Heroi(nomeHeroi, armaEscolhida, forcaFinal, destrezaFinal, constituicaoFinal, inteligenciaFinal, sorteFinal);

        System.out.println("\nOlá, " + heroi.getNome() + "! Sua jornada começa agora...\n");

        // Loop principal do jogo
        while (heroi.estaVivo()) {
            Inimigo inimigo = InimigoFactory.gerarInimigoAleatorio(heroi.getNivel());

            Batalha batalha = new Batalha(heroi, inimigo, scanner);
            boolean heroiVenceu = batalha.iniciar();

            if (!heroiVenceu && !heroi.estaVivo()) break;
        }

        System.out.println("\n=== FIM DE JOGO ===");
        scanner.close();
    }
}