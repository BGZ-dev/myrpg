package Main;

import Controller.Batalha;
import Dominio.Arma;
import Dominio.Heroi;
import Dominio.Inimigo;
import Services.ArmaFactory;
import Services.InimigoFactory;
import Dominio.Classes.Assassino;
import Dominio.Classes.Guerreiro;
import Dominio.Classes.Barbaro;
import Dominio.Classes.Mago;
import Dominio.Classes.Bruxo;
import Dominio.Classes.Invocador;
import Dominio.Classes.Classe;

import java.util.InputMismatchException;
import java.util.Scanner;

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

        // Escolha de classe
        System.out.println("\nEscolha sua classe:");
        System.out.println("1. Assassino | 2. Guerreiro | 3. Bárbaro | 4. Mago | 5. Bruxo | 6. Invocador");
        int escolhaClasse = 0;
        while (true) {
            try {
                System.out.print("Escolha (1-6): ");
                escolhaClasse = scanner.nextInt();
                scanner.nextLine();
                if (escolhaClasse < 1 || escolhaClasse > 6) {
                    System.out.println("Escolha inválida. Tente novamente.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Use um número.");
                scanner.nextLine();
            }
        }

        Classe classeEscolhida;
        switch (escolhaClasse) {
            case 1 -> classeEscolhida = new Assassino();
            case 2 -> classeEscolhida = new Guerreiro();
            case 3 -> classeEscolhida = new Barbaro();
            case 4 -> classeEscolhida = new Mago();
            case 5 -> classeEscolhida = new Bruxo();
            default -> classeEscolhida = new Invocador();
        }

        Heroi heroi = new Heroi(nomeHeroi, armaEscolhida, forcaFinal, destrezaFinal, constituicaoFinal, inteligenciaFinal, sorteFinal, classeEscolhida);

        System.out.println("\nOlá, " + heroi.getNome() + "! Sua jornada começa agora...\n");

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