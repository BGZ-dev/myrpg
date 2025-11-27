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
import Dominio.Classes.Feiticeiro;
import Dominio.Classes.Invocador;
import Dominio.Classes.Classe;

import Dominio.Elemento;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
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

        Classe classeEscolhida = null;

        if (escolhaClasse == 4) {
            // Mago: pedir elementos (até 3) — já implementado em outra versão
            List<Elemento> elementosEscolhidos = new ArrayList<>();
            System.out.println("\nVocê escolheu Mago. Pode escolher até 3 elementos para usar nos feitiços.");
            for (int i = 0; i < Elemento.values().length; i++) {
                System.out.println((i + 1) + ". " + Elemento.values()[i]);
            }
            System.out.print("Escolha (1-8) até 3 elementos (ex: 1 3 5) ou ENTER para nenhum: ");
            String linha = scanner.nextLine().trim();
            if (!linha.isEmpty()) {
                String[] tokens = linha.split("\\s+");
                for (String t : tokens) {
                    try {
                        int idx = Integer.parseInt(t);
                        if (idx >= 1 && idx <= Elemento.values().length) {
                            Elemento e = Elemento.values()[idx - 1];
                            if (!elementosEscolhidos.contains(e)) {
                                elementosEscolhidos.add(e);
                                if (elementosEscolhidos.size() >= 3) break;
                            }
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            classeEscolhida = new Mago(elementosEscolhidos);
        } else if (escolhaClasse == 6) {
            List<Elemento> elementosEscolhidos = new ArrayList<>();
            System.out.println("\nVocê escolheu Invocador. Pode escolher até 3 elementos para suas invocações.");
            for (int i = 0; i < Elemento.values().length; i++) {
                System.out.println((i + 1) + ". " + Elemento.values()[i]);
            }
            System.out.print("Escolha (1-8) até 3 elementos (ex: 2 4) ou ENTER para nenhum: ");
            String linha = scanner.nextLine().trim();
            if (!linha.isEmpty()) {
                String[] tokens = linha.split("\\s+");
                for (String t : tokens) {
                    try {
                        int idx = Integer.parseInt(t);
                        if (idx >= 1 && idx <= Elemento.values().length) {
                            Elemento e = Elemento.values()[idx - 1];
                            if (!elementosEscolhidos.contains(e)) {
                                elementosEscolhidos.add(e);
                                if (elementosEscolhidos.size() >= 3) break;
                            }
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            if (elementosEscolhidos.isEmpty()) {
                System.out.println("Elementos selecionados para o Invocador: Nenhum (pode invocar qualquer elemento).");
            } else {
                System.out.println("Elementos selecionados para o Invocador: " + elementosEscolhidos);
            }
            classeEscolhida = new Invocador(elementosEscolhidos);
        } else {
            // outras classes
            switch (escolhaClasse) {
                case 1 -> classeEscolhida = new Assassino();
                case 2 -> classeEscolhida = new Guerreiro();
                case 3 -> classeEscolhida = new Barbaro();
                case 5 -> classeEscolhida = new Feiticeiro();
                default -> classeEscolhida = new Assassino(); // fallback (não deve ocorrer)
            }
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