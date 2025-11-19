package Controller;

import Dominio.Elemento;
import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Personagem;
import Dominio.Arma;
import Dominio.Invocacao;
import Dominio.Classes.Classe;
import Dominio.Classes.AcaoResultado;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Batalha adaptada: a opção 2 (ATAQUE ESPECIAL) passa a executar a AÇÃO DA CLASSE.
 * - A opção 1 continua sendo ataque normal
 * - A opção 2 chama classe.executarAcao(...) (se houver classe)
 * - As invocações atacam após a ação do herói (tanto ataque normal quanto ação de classe)
 */
public class Batalha {

    private Heroi heroi;
    private Inimigo inimigo;
    private Scanner scanner;
    private Random rand = new Random();
    private final List<Invocacao> invocacoes = new ArrayList<>(); // invocações ativas

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

            // Efeitos por turno da classe (ex.: invocador)
            if (heroi.getClasse() != null) heroi.getClasse().aoFinalDoTurno(heroi, inimigo);

            // Limpar invocações mortas
            invocacoes.removeIf(inv -> !inv.estaVivo());
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
        System.out.println("1. Atacar | 2. Ação da Classe (antes: Ataque Especial) | 3. Usar Poção | 4. Ver Status | 5. Fugir");
        System.out.print("Escolha uma ação: ");
        int escolha = -1;
        try {
            escolha = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Entrada inválida.");
            return;
        }

        switch (escolha) {
            case 1 -> {
                System.out.println(heroi.getNome() + " ataca com " + heroi.getArma().getNome() + "!");
                realizarAtaque(heroi, inimigo, heroi.calcularDanoBase(), null);
                // invocações atacam após ataque do herói
                invocarAtacar();
            }
            case 2 -> {
                // AÇÃO DA CLASSE em lugar do antigo "ataque especial"
                Classe cls = heroi.getClasse();
                if (cls == null) {
                    // Se não tem classe, como fallback, executa o cálculo especial antigo
                    System.out.println("Nenhuma classe atribuída — executando ataque especial padrão.");
                    Elemento elementoAtaque = escolherElemento(scanner);
                    System.out.println(heroi.getNome() + " usa ATAQUE ESPECIAL (" + elementoAtaque + ")!");
                    realizarAtaque(heroi, inimigo, heroi.calcularDanoEspecial(), elementoAtaque);
                    invocarAtacar();
                } else {
                    AcaoResultado res = cls.executarAcao(heroi, inimigo, scanner);
                    aplicarAcaoResultado(res);
                    // invocações atacam após ação de classe (se houver)
                    invocarAtacar();
                }
            }
            case 3 -> heroi.curar();
            case 4 -> {
                mostrarStatusHeroi();
                // não gasta turno
                turnoDoHeroi();
            }
            case 5 -> {
                System.out.println("Você fugiu da batalha!");
                heroi.receberDano(9999);
            }
            default -> System.out.println("Opção inválida! Você perdeu seu turno.");
        }
    }

    private void invocarAtacar() {
        for (Invocacao pet : new ArrayList<>(invocacoes)) {
            if (!pet.estaVivo()) continue;
            int danoPet = pet.calcularDanoBase();
            System.out.println(pet.getNome() + " ataca e causa " + danoPet + " ao inimigo!");
            inimigo.receberDano(danoPet);
            if (!inimigo.estaVivo()) {
                System.out.println(inimigo.getNome() + " foi derrotado pela invocação!");
                break;
            }
        }
    }

    private void turnoDoInimigo() {
        System.out.println("\n--- TURNO DO INIMIGO ---");
        System.out.println(inimigo.getNome() + " (" + inimigo.getElemento() + ") ataca!");
        realizarAtaque(inimigo, heroi, inimigo.calcularDanoBase(), inimigo.getElemento());
    }

    private void aplicarAcaoResultado(AcaoResultado res) {
        if (res == null) return;
        if (res.mensagem != null && !res.mensagem.isEmpty()) System.out.println(res.mensagem);

        if (res.curaAoHeroi != 0) {
            if (res.curaAoHeroi > 0) {
                heroi.curarPor(res.curaAoHeroi);
                System.out.println(heroi.getNome() + " recuperou " + res.curaAoHeroi + " de vida.");
            } else {
                // perda de vida (ex.: barbaro sacrifica vida)
                heroi.receberDano(-res.curaAoHeroi);
                System.out.println(heroi.getNome() + " perdeu " + (-res.curaAoHeroi) + " de vida.");
            }
        }
        if (res.danoAoInimigo > 0) {
            System.out.println("Ação causa " + res.danoAoInimigo + " dano ao inimigo.");
            inimigo.receberDano(res.danoAoInimigo);
        }
        if (res.tempAtkBuff > 0 && res.tempAtkBuffTurnos > 0) {
            heroi.setAtaque(heroi.getAtaque() + res.tempAtkBuff);
            System.out.println("Ataque temporário aplicado: +" + res.tempAtkBuff + " por " + res.tempAtkBuffTurnos + " turnos.");
            // Nota: para expiração automática dos buffs, implemente tracking em Heroi (opcional).
        }
        if (res.tempDefBuff > 0 && res.tempDefBuffTurnos > 0) {
            heroi.setDefesa(heroi.getDefesa() + res.tempDefBuff);
            System.out.println("Defesa temporária aplicada: +" + res.tempDefBuff + " por " + res.tempDefBuffTurnos + " turnos.");
        }
        if (res.lifestealBonus > 0 && res.lifestealTurnos > 0) {
            System.out.println("Lifesteal aumentado em " + (int)(res.lifestealBonus*100) + "% por " + res.lifestealTurnos + " turnos.");
            // Para funcionamento real, armazene o multiplicador temporário em Heroi e use ao curar por lifesteal.
        }
        if (res.invocacao != null) {
            invocacoes.add(res.invocacao);
            System.out.println(res.invocacao.getNome() + " foi invocado com " + res.invocacao.getVida() + " vida.");
        }
    }

    private void realizarAtaque(Personagem atacante, Personagem defensor, int danoBase, Elemento elementoAtaque) {
        double multiplicador = (elementoAtaque != null)
                ? elementoAtaque.efetividadeContra(defensor.getElemento())
                : 1.0;

        double mitigacao = defensor.getDefesa() / 2.0;
        int danoFinal = (int) Math.round((danoBase - mitigacao) * multiplicador);
        if (danoFinal < 0) danoFinal = 0;

        if (elementoAtaque != null) {
            System.out.printf("⚡ Efeito elemental: %.1fx (%s vs %s)%n", multiplicador, elementoAtaque, defensor.getElemento());
        }

        // Aplicar possibilidade de reação (se tiver ReactionService integrado)
        System.out.println(defensor.getNome() + " recebeu " + danoFinal + " de dano!");
        defensor.receberDano(danoFinal);

        // Lifesteal do Bruxo: se atacante é Heroi com Bruxo, curar parte do dano
        if (atacante instanceof Heroi) {
            Heroi hAt = (Heroi) atacante;
            if (hAt.getClasse() != null && hAt.getClasse().getNome().equals("Bruxo")) {
                int heal = Math.max(1, danoFinal / 4);
                hAt.curarPor(heal);
                System.out.println("🔮 Bruxo drena vida e recupera " + heal + " de vida!");
            }
        }
    }

    private Elemento escolherElemento(Scanner scanner) {
        System.out.println("\nEscolha o elemento do ataque especial:");
        System.out.println("1.Fogo 🔥 2.Água 💧 3.Terra 🌱 4.Ar 🌪️ 5.Luz ☀️ 6.Sombra 🌑 7.Raio ⚡ 8.Gelo ❄️");
        int escolha = 1;
        try {
            escolha = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
        }
        return Elemento.values()[Math.max(0, Math.min(7, escolha-1))];
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
        System.out.println(" Classe: " + (heroi.getClasse() != null ? heroi.getClasse().getNome() : "Nenhuma"));
        System.out.println(" Força: " + heroi.getForca());
        System.out.println(" Destreza: " + heroi.getDestreza());
        System.out.println(" Constituição: " + heroi.getConstituicao());
        System.out.println(" Inteligência: " + heroi.getInteligencia());
        System.out.println(" Sorte: " + heroi.getSorte());
        if (!invocacoes.isEmpty()) {
            System.out.println(" Invocações ativas:");
            for (Invocacao p : invocacoes) {
                System.out.println("  - " + p.getNome() + " (Vida: " + p.getVida() + ")");
            }
        }
        System.out.println("==============\n");
    }
}