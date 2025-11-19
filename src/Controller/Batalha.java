package Controller;

import Dominio.Elemento;
import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Personagem;
import Dominio.Arma;

import java.util.Random;
import java.util.Scanner;

/**
 * CONTROLADOR/SERVIÇO
 * Orquestra a lógica de um combate entre um herói e um inimigo.
 * Atualizado para exibir os novos atributos no status.
 */
public class Batalha {

    private Heroi heroi;
    private Inimigo inimigo;
    private Scanner scanner;
    private Random rand = new Random();

    public Batalha(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        this.heroi = heroi;
        this.inimigo = inimigo;
        this.scanner = scanner;
    }

    public boolean iniciar() {
        System.out.println("\n🔥 Um inimigo apareceu: " + inimigo.getNome() + " (" + inimigo.getElemento() + ")!");

        while (heroi.estaVivo() && inimigo.estaVivo()) {
            turnoDoHeroi();
            if (!inimigo.estaVivo()) break;
            turnoDoInimigo();
        }

        if (heroi.estaVivo()) {
            System.out.println("\n🏆 Você derrotou " + inimigo.getNome() + "!");
            int xpGanho = 50 + rand.nextInt(50);
            heroi.ganharExperiencia(xpGanho);
            heroi.buffPermanente();
            return true;
        } else {
            System.out.println("\n☠️ " + heroi.getNome() + " foi derrotado...");
            return false;
        }
    }

    private void turnoDoHeroi() {
        System.out.println("\n--- SEU TURNO ---");
        System.out.println("Sua vida: " + heroi.getVida() + " | Vida do inimigo: " + inimigo.getVida());
        System.out.println("1. Atacar | 2. Ataque Especial | 3. Usar Poção | 4. Ver Status | 5. Fugir");
        System.out.print("Escolha uma ação: ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        switch (escolha) {
            case 1:
                System.out.println(heroi.getNome() + " ataca com " + heroi.getArma().getNome() + "!");
                realizarAtaque(heroi, inimigo, heroi.calcularDanoBase(), null);
                break;
            case 2:
                Elemento elementoAtaque = escolherElemento(scanner);
                System.out.println(heroi.getNome() + " usa ATAQUE ESPECIAL (" + elementoAtaque + ")!");
                realizarAtaque(heroi, inimigo, heroi.calcularDanoEspecial(), elementoAtaque);
                break;
            case 3:
                heroi.curar();
                break;
            case 4:
                mostrarStatusHeroi();
                turnoDoHeroi(); // Ver status não gasta o turno
                break;
            case 5:
                System.out.println("Você fugiu da batalha!");
                heroi.receberDano(9999);
                break;
            default:
                System.out.println("Opção inválida! Você perdeu seu turno.");
                break;
        }
    }

    private void turnoDoInimigo() {
        System.out.println("\n--- TURNO DO INIMIGO ---");
        System.out.println(inimigo.getNome() + " (" + inimigo.getElemento() + ") ataca!");
        realizarAtaque(inimigo, heroi, inimigo.calcularDanoBase(), inimigo.getElemento());
    }

    private void realizarAtaque(Personagem atacante, Personagem defensor, int danoBase, Elemento elementoAtaque) {
        double multiplicador = (elementoAtaque != null)
                ? elementoAtaque.efetividadeContra(defensor.getElemento())
                : 1.0;

        double mitigacao = defensor.getDefesa() / 2.0;
        int danoFinal = (int) ((danoBase - mitigacao) * multiplicador);
        if (danoFinal < 0) danoFinal = 0;

        if (elementoAtaque != null) {
            System.out.printf("⚡ Efeito elemental: %.1fx (%s vs %s)%n", multiplicador, elementoAtaque, defensor.getElemento());
        }

        System.out.println(defensor.getNome() + " recebeu " + danoFinal + " de dano!");
        defensor.receberDano(danoFinal);
    }

    private Elemento escolherElemento(Scanner scanner) {
        System.out.println("\nEscolha o elemento do ataque especial:");
        System.out.println("1.Fogo 🔥 2.Água 💧 3.Terra 🌱 4.Ar 🌪️ 5.Luz ☀️ 6.Sombra 🌑 7.Raio ⚡ 8.Gelo ❄️");
        int escolha = scanner.nextInt();
        scanner.nextLine();
        return Elemento.values()[escolha - 1];
    }

    private void mostrarStatusHeroi() {
        System.out.println("\n=== STATUS ===");
        System.out.println(" Nome: " + heroi.getNome());
        System.out.println(" Vida: " + heroi.getVida());
        System.out.println(" Nível: " + heroi.getNivel());
        System.out.println(" Ataque: " + heroi.getAtaque());
        System.out.println(" Defesa: " + heroi.getDefesa());
        System.out.println(" Poções: " + heroi.getPotesDeCura());
        Arma arma = heroi.getArma();
        if (arma != null) {
            System.out.println(" Arma: " + arma.getNome() + " (" + arma.getEspecial() + ")");
            System.out.println(" Tipo de Arma: " + arma.getTipo() + " | Escala: " + arma.getEscala() + "x");
        } else {
            System.out.println(" Arma: Nenhuma");
        }
        System.out.println(" Força: " + heroi.getForca());
        System.out.println(" Destreza: " + heroi.getDestreza());
        System.out.println(" Constituição: " + heroi.getConstituicao());
        System.out.println(" Inteligência: " + heroi.getInteligencia());
        System.out.println(" Sorte: " + heroi.getSorte());
        System.out.println("==============\n");
    }
}