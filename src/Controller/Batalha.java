package Controller;

import Dominio.Elemento;
import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Personagem;
import Dominio.Arma;
import Dominio.Invocacao;
import Dominio.Classes.Classe;
import Dominio.Classes.AcaoResultado;

import Services.ReactionService;
import Services.ReactionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Batalha adaptada:
 * - Opção 2 = Ação da Classe
 * - Quando o defensor for o Heroi e for atacado, o jogador é convidado a reagir manualmente
 *   (esquiva / bloqueio / contra-ataque) com base na categoria da arma e atributos.
 * - Se o jogador não reagir (entrada inválida) ou se ReactionService falhar, usamos resolução automática.
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

            // Decrementa duração de encantamentos aplicados ao herói
            heroi.tickEncantamento();

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
                System.out.println(heroi.getNome() + " ataca com " + (heroi.getArma() != null ? heroi.getArma().getNome() : "punhos") + "!");
                realizarAtaque(heroi, inimigo, heroi.calcularDanoBase(), null);
                // invocações atacam após ataque do herói
                invocarAtacar();
            }
            case 2 -> {
                Classe cls = heroi.getClasse();
                if (cls == null) {
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
                // não consome turno
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

    /**
     * Realiza um ataque aplicando efeitos elementais e delegando a reação.
     * Se o defensor for o herói e for jogador (ou seja, defender é humano), chama promptHeroReaction
     * para permitir escolha de esquiva / bloqueio / contra; caso contrário tenta ReactionService.
     */
    private void realizarAtaque(Personagem atacante, Personagem defensor, int danoBase, Elemento elementoAtaque) {
        // aplica modificador elemental
        double multiplicador = (elementoAtaque != null)
                ? elementoAtaque.efetividadeContra(defensor.getElemento())
                : 1.0;

        // mitigação por defesa
        double mitigacao = defensor.getDefesa() / 2.0;
        int danoCalculado = (int) Math.round((danoBase - mitigacao) * multiplicador);
        if (danoCalculado < 0) danoCalculado = 0;

        if (elementoAtaque != null) {
            System.out.printf("⚡ Efeito elemental: %.1fx (%s vs %s)%n", multiplicador, elementoAtaque, defensor.getElemento());
        }

        // Se defensor for o herói (jogador), ofereça a escolha de reagir manualmente
        if (defensor instanceof Heroi && atacante instanceof Inimigo) {
            boolean choiceHandled = promptHeroReaction((Heroi) defensor, atacante, danoCalculado, elementoAtaque);
            if (choiceHandled) return; // reação do jogador lidou com tudo
            // se não foi tratada (entrada inválida), cairá para resolução automática abaixo
        }

        // fallback: tentar ReactionService (se disponível) — útil para NPCs e se o jogador não reagiu
        ReactionResult reaction = null;
        try {
            reaction = ReactionService.resolveReaction(atacante, defensor, danoCalculado, elementoAtaque);
        } catch (Throwable t) {
            System.out.println("⚠️ Erro ao resolver reação automática: " + t.getMessage() + " — aplicando dano padrão.");
        }

        if (reaction == null) {
            System.out.println(defensor.getNome() + " recebeu " + danoCalculado + " de dano!");
            defensor.receberDano(danoCalculado);
            return;
        }

        // interpreta o ReactionResult
        if (reaction.dodged) {
            System.out.println(reaction.message != null && !reaction.message.isEmpty() ? reaction.message : defensor.getNome() + " esquivou-se!");
            return;
        }
        if (reaction.blocked) {
            System.out.println(reaction.message != null && !reaction.message.isEmpty() ? reaction.message : defensor.getNome() + " bloqueou o ataque!");
            int danoAReceber = Math.max(0, reaction.damageTaken);
            System.out.println(defensor.getNome() + " recebeu " + danoAReceber + " de dano após o bloqueio!");
            defensor.receberDano(danoAReceber);
            if (reaction.counterDamage > 0) {
                System.out.println(defensor.getNome() + " contra-ataca causando " + reaction.counterDamage + " a " + atacante.getNome() + "!");
                atacante.receberDano(reaction.counterDamage);
            }
            return;
        }

        // sem reação efetiva
        int danoFinal = Math.max(0, reaction.damageTaken);
        System.out.println(defensor.getNome() + " recebeu " + danoFinal + " de dano!");
        defensor.receberDano(danoFinal);
        if (reaction.counterDamage > 0) {
            System.out.println(defensor.getNome() + " causa " + reaction.counterDamage + " de contra-ataque a " + atacante.getNome() + "!");
            atacante.receberDano(reaction.counterDamage);
        }
    }

    /**
     * Prompt interativo para o jogador (Heroi) escolher reação quando for atacado por um inimigo.
     * Retorna true se a escolha foi válida e a reação aplicada; false se entrada inválida (para fallback).
     */
    private boolean promptHeroReaction(Heroi heroiDef, Personagem atacante, int danoCalculado, Elemento elementoAtaque) {
        Arma arma = heroiDef.getArma();
        Arma.Categoria cat = arma != null ? arma.getCategoria() : null;

        // determinar opções disponíveis
        boolean podeEsquivar = true;
        boolean podeBloquear = true;
        boolean podeContra = false;
        if (cat != null) {
            switch (cat) {
                case LAMINA -> { podeEsquivar = true; podeBloquear = false; }
                case BRANCA_SEM_LAMINA -> { podeEsquivar = true; podeBloquear = true; podeContra = true; }
                case LONGA_DISTANCIA -> { podeEsquivar = true; podeBloquear = true; podeContra = false; }
            }
        } else {
            // sem arma: permitir esquiva e bloqueio básicos
            podeEsquivar = true; podeBloquear = true;
        }

        // Exibir menu de reação
        System.out.println("\nVocê está sendo atacado! Escolha sua reação:");
        int optionIndex = 1;
        int optEsquiva = -1, optBloqueio = -1, optContra = -1, optNada = -1;
        if (podeEsquivar) { optEsquiva = optionIndex++; System.out.println(optEsquiva + ". Esquivar"); }
        if (podeBloquear) { optBloqueio = optionIndex++; System.out.println(optBloqueio + ". Bloquear"); }
        if (podeContra)   { optContra = optionIndex++; System.out.println(optContra + ". Contra-Atacar (requer bloqueio bem-sucedido)"); }
        optNada = optionIndex++; System.out.println(optNada + ". Não reagir / Fazer nada");

        System.out.print("Escolha (número): ");
        int escolha = -1;
        try {
            escolha = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Entrada inválida — reação ignorada.");
            return false;
        }

        // Se jogador escolheu não reagir
        if (escolha == optNada) {
            System.out.println("Você optou por não reagir.");
            System.out.println(heroiDef.getNome() + " recebeu " + danoCalculado + " de dano!");
            heroiDef.receberDano(danoCalculado);
            return true;
        }

        // ESQUIVA
        if (escolha == optEsquiva) {
            double chance = 0.20 + (heroiDef.getDestreza() * 0.02);
            if (chance > 0.90) chance = 0.90;
            double roll = rand.nextDouble();
            if (roll < chance) {
                System.out.println("✨ Você esquivou com sucesso!");
                // opcional: aplicar cooldown aqui se desejar
                return true;
            } else {
                System.out.println("Você tentou esquivar, mas falhou.");
                // recebe dano normalmente (sem outras reações)
                System.out.println(heroiDef.getNome() + " recebeu " + danoCalculado + " de dano!");
                heroiDef.receberDano(danoCalculado);
                return true;
            }
        }

        // BLOQUEIO (e possivelmente CONTRA)
        if (escolha == optBloqueio || escolha == optContra) {
            double chance = 0.30 + (heroiDef.getConstituicao() * 0.02);
            if (chance > 0.95) chance = 0.95;
            double roll = rand.nextDouble();
            if (roll < chance) {
                double reducao = 0.30 + (heroiDef.getConstituicao() * 0.01);
                if (reducao > 0.85) reducao = 0.85;
                int danoReduzido = (int) Math.round(danoCalculado * (1.0 - reducao));
                if (danoReduzido < 0) danoReduzido = 0;
                System.out.println("🛡️ Bloqueio bem-sucedido! Dano reduzido para " + danoReduzido + ".");
                heroiDef.receberDano(danoReduzido);

                // se escolheu contra e categoria permite, rolar chance de contra
                if (escolha == optContra && podeContra) {
                    double probContra = Math.min(0.5, heroiDef.getConstituicao() * 0.02);
                    if (rand.nextDouble() < probContra) {
                        int contra = (int) Math.round(heroiDef.getConstituicao() / 2.0 + heroiDef.calcularDanoBase() * 0.5);
                        System.out.println("⚡ Contra-ataque! Você causa " + contra + " de dano ao atacante!");
                        atacante.receberDano(Math.max(0, contra));
                    } else {
                        System.out.println("Tentativa de contra-ataque falhou.");
                    }
                }
                return true;
            } else {
                System.out.println("Bloqueio falhou. Você recebeu " + danoCalculado + " de dano!");
                heroiDef.receberDano(danoCalculado);
                return true;
            }
        }

        // escolha inválida -> fallback
        System.out.println("Escolha inválida — reação ignorada.");
        return false;
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

    private void aplicarAcaoResultado(AcaoResultado res) {
        if (res == null) return;
        if (res.mensagem != null && !res.mensagem.isEmpty()) System.out.println(res.mensagem);

        if (res.curaAoHeroi != 0) {
            if (res.curaAoHeroi > 0) {
                heroi.curarPor(res.curaAoHeroi);
                System.out.println(heroi.getNome() + " recuperou " + res.curaAoHeroi + " de vida.");
            } else {
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
        }

        if (res.tempDefBuff > 0 && res.tempDefBuffTurnos > 0) {
            heroi.setDefesa(heroi.getDefesa() + res.tempDefBuff);
            System.out.println("Defesa temporária aplicada: +" + res.tempDefBuff + " por " + res.tempDefBuffTurnos + " turnos.");
        }

        if (res.lifestealBonus > 0 && res.lifestealTurnos > 0) {
            System.out.println("Lifesteal aumentado em " + (int)(res.lifestealBonus*100) + "% por " + res.lifestealTurnos + " turnos.");
        }

        if (res.invocacao != null) {
            invocacoes.add(res.invocacao);
            System.out.println(res.invocacao.getNome() + " foi invocado com " + res.invocacao.getVida() + " vida.");
        }
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
        System.out.println(" Encantamento: " + heroi.getEncantamento());
        if (heroi.isEncantamentoAtivo()) {
            System.out.println(" Encantamento ativo: " + heroi.getEncantamentoElemento() + " | Alvo: " + (heroi.isEncantamentoNoArma() ? "Arma" : "Punhos"));
        }
        if (!invocacoes.isEmpty()) {
            System.out.println(" Invocações ativas:");
            for (Invocacao p : invocacoes) {
                System.out.println("  - " + p.getNome() + " (Vida: " + p.getVida() + ")");
            }
        }
        System.out.println("==============\n");
    }
}