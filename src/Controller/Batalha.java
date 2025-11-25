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
 * Batalha — atualizado:
 * - delega reações manuais ao ReactionService.attemptSpecificReaction(...)
 * - aplica modificarDanoEntrada da classe do defensor (quando houver) antes de aplicar dano
 * - imprime mensagens retornadas pelas strategies (sucesso/falha)
 * - decrementa cooldowns e tick de encantamento a cada rodada
 *
 * Compatível com JDKs que não suportam switch-expression (usa switch tradicional).
 */
public class Batalha {

    private Heroi heroi;
    private Inimigo inimigo;
    private Scanner scanner;
    private Random rand = new Random();
    private final List<Invocacao> invocacoes = new ArrayList<>();

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

            if (heroi.getClasse() != null) heroi.getClasse().aoFinalDoTurno(heroi, inimigo);

            // tick encantamento e cooldowns
            heroi.tickEncantamento();
            heroi.decrementarCooldowns();
            inimigo.decrementarCooldowns();

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
            case 1: {
                System.out.println(heroi.getNome() + " ataca com " + (heroi.getArma() != null ? heroi.getArma().getNome() : "punhos") + "!");
                realizarAtaque(heroi, inimigo, heroi.calcularDanoBase(), null);
                invocarAtacar();
                break;
            }
            case 2: {
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
                    invocarAtacar();
                }
                break;
            }
            case 3: {
                heroi.curar();
                break;
            }
            case 4: {
                mostrarStatusHeroi();
                // não consome turno
                turnoDoHeroi();
                break;
            }
            case 5: {
                System.out.println("Você fugiu da batalha!");
                heroi.receberDano(9999);
                break;
            }
            default: {
                System.out.println("Opção inválida! Você perdeu seu turno.");
                break;
            }
        }
    }

    private void invocarAtacar() {
        for (Invocacao pet : new ArrayList<>(invocacoes)) {
            if (!pet.estaVivo()) continue;
            int danoPet = pet.calcularDanoBase();
            System.out.println(pet.getNome() + " ataca e causa " + danoPet + " ao inimigo!");
            // aplicar modificador de classe do inimigo (se houver) antes de aplicar dano
            int danoAjustado = aplicarModificadorClasseAoDanoRecebido(inimigo, danoPet, pet);
            inimigo.receberDano(danoAjustado);
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
     * O dano final aplicado ao defensor passa pelo modificador de classe (se defender for Heroi com classe).
     */
    private void realizarAtaque(Personagem atacante, Personagem defensor, int danoBase, Elemento elementoAtaque) {
        double multiplicador = (elementoAtaque != null)
                ? elementoAtaque.efetividadeContra(defensor.getElemento())
                : 1.0;

        double mitigacao = defensor.getDefesa() / 2.0;
        int danoCalculado = (int) Math.round((danoBase - mitigacao) * multiplicador);
        if (danoCalculado < 0) danoCalculado = 0;

        if (elementoAtaque != null) {
            System.out.printf("⚡ Efeito elemental: %.1fx (%s vs %s)%n", multiplicador, elementoAtaque, defensor.getElemento());
        }

        // Se defensor for o herói (jogador), ofereça a escolha de reagir manualmente
        if (defensor instanceof Heroi && atacante instanceof Inimigo) {
            boolean handled = promptHeroReaction((Heroi) defensor, atacante, danoCalculado, elementoAtaque);
            if (handled) return;
        }

        // fallback automático via ReactionService
        ReactionResult reaction = null;
        try {
            reaction = ReactionService.resolveReaction(atacante, defensor, danoCalculado, elementoAtaque);
        } catch (Throwable t) {
            System.out.println("⚠️ Erro ao resolver reação automática: " + t.getMessage() + " — aplicando dano padrão.");
        }

        if (reaction == null) {
            int danoParaAplicar = aplicarModificadorClasseAoDanoRecebido(defensor, danoCalculado, atacante);
            System.out.println(defensor.getNome() + " recebeu " + danoParaAplicar + " de dano!");
            defensor.receberDano(danoParaAplicar);
            return;
        }

        // Mostrar mensagem retornada pela strategy (explica sucesso/falha)
        if (reaction.message != null && !reaction.message.isEmpty()) {
            System.out.println(reaction.message);
        }

        if (reaction.dodged) {
            return; // esquiva, nada a aplicar
        }

        if (reaction.blocked) {
            int danoParaAplicar = aplicarModificadorClasseAoDanoRecebido(defensor, Math.max(0, reaction.damageTaken), atacante);
            System.out.println(defensor.getNome() + " recebeu " + danoParaAplicar + " de dano após o bloqueio!");
            defensor.receberDano(danoParaAplicar);

            if (reaction.counterDamage > 0) {
                int counterAdj = aplicarModificadorClasseAoDanoRecebido(atacante, reaction.counterDamage, defensor);
                System.out.println(defensor.getNome() + " contra-ataca causando " + counterAdj + " a " + atacante.getNome() + "!");
                atacante.receberDano(counterAdj);
            }
            return;
        }

        // sem reação efetiva: aplicar damageTaken
        int danoFinal = Math.max(0, reaction.damageTaken);
        int danoParaAplicar = aplicarModificadorClasseAoDanoRecebido(defensor, danoFinal, atacante);
        System.out.println(defensor.getNome() + " recebeu " + danoParaAplicar + " de dano!");
        defensor.receberDano(danoParaAplicar);

        if (reaction.counterDamage > 0) {
            int counterAdj = aplicarModificadorClasseAoDanoRecebido(atacante, reaction.counterDamage, defensor);
            System.out.println(defensor.getNome() + " causa " + counterAdj + " de contra-ataque a " + atacante.getNome() + "!");
            atacante.receberDano(counterAdj);
        }
    }

    /**
     * Prompt interativo que delega a tentativa de reação ao ReactionService.
     * Retorna true se a escolha foi válida e a reação aplicada; false se entrada inválida
     * (para então cair no fallback automático).
     */
    private boolean promptHeroReaction(Heroi heroiDef, Personagem atacante, int danoCalculado, Elemento elementoAtaque) {
        Arma arma = heroiDef.getArma();
        Arma.Categoria cat = arma != null ? arma.getCategoria() : null;

        boolean podeEsquivar = true;
        boolean podeBloquear = true;
        boolean podeContra = false;
        if (cat != null) {
            if (cat == Arma.Categoria.LAMINA) { podeEsquivar = true; podeBloquear = false; }
            else if (cat == Arma.Categoria.BRANCA_SEM_LAMINA) { podeEsquivar = true; podeBloquear = true; podeContra = true; }
            else if (cat == Arma.Categoria.LONGA_DISTANCIA) { podeEsquivar = true; podeBloquear = true; }
        }

        // debug opcional
        System.out.println("DEBUG: arma = " + (arma != null ? arma.getNome() + " (" + cat + ")" : "Nenhuma") +
                " | HP: " + heroiDef.getVida() + " | ATQ calculado: " + danoCalculado);

        // Loop até escolha válida ou fallback (pressione 0 para fallback)
        while (true) {
            System.out.println("\nVocê está sendo atacado! Escolha sua reação:");
            System.out.println("1. Esquivar" + (podeEsquivar ? "" : " (não disponível)"));
            System.out.println("2. Bloquear" + (podeBloquear ? "" : " (não disponível)"));
            System.out.println("3. Contra-Atacar" + (podeContra ? "" : " (não disponível)"));
            System.out.println("0. Não reagir / Fallback automático");

            System.out.print("Escolha (número): ");
            int escolha = -1;
            try {
                escolha = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Entrada inválida — escolha novamente ou digite 0 para não reagir.");
                continue;
            }

            if (escolha == 0) {
                System.out.println("Você optou por não reagir.");
                int danoParaAplicar = aplicarModificadorClasseAoDanoRecebido(heroiDef, danoCalculado, atacante);
                System.out.println(heroiDef.getNome() + " recebeu " + danoParaAplicar + " de dano!");
                heroiDef.receberDano(danoParaAplicar);
                return true;
            }

            ReactionService.ReactionType type = null;
            if (escolha == 1) {
                if (!podeEsquivar) { System.out.println("Esquiva não disponível. Escolha outra opção."); continue; }
                type = ReactionService.ReactionType.DODGE;
            } else if (escolha == 2) {
                if (!podeBloquear) { System.out.println("Bloqueio não disponível. Escolha outra opção."); continue; }
                type = ReactionService.ReactionType.BLOCK;
            } else if (escolha == 3) {
                if (!podeContra) { System.out.println("Contra-ataque não disponível. Escolha outra opção."); continue; }
                type = ReactionService.ReactionType.COUNTER;
            } else {
                System.out.println("Escolha inválida. Tente novamente.");
                continue;
            }

            ReactionResult rr;
            try {
                rr = ReactionService.attemptSpecificReaction(atacante, heroiDef, danoCalculado, elementoAtaque, type);
            } catch (Throwable t) {
                System.out.println("⚠️ Erro ao processar reação: " + t.getMessage() + " — aplicando fallback.");
                return false; // fallback automático
            }

            if (rr == null) {
                System.out.println("Erro no processamento da reação — dano aplicado.");
                int danoParaAplicar = aplicarModificadorClasseAoDanoRecebido(heroiDef, danoCalculado, atacante);
                heroiDef.receberDano(danoParaAplicar);
                return true;
            }

            // Sempre exibir a mensagem retornada pela strategy (sucesso ou falha)
            if (rr.message != null && !rr.message.isEmpty()) {
                System.out.println(rr.message);
            }

            if (rr.dodged) {
                return true;
            }
            if (rr.blocked) {
                int danoParaAplicar = aplicarModificadorClasseAoDanoRecebido(heroiDef, Math.max(0, rr.damageTaken), atacante);
                heroiDef.receberDano(danoParaAplicar);
                if (rr.counterDamage > 0) {
                    int counterAdj = aplicarModificadorClasseAoDanoRecebido(atacante, rr.counterDamage, heroiDef);
                    System.out.println("Contra-ataque causa " + counterAdj + " ao atacante.");
                    atacante.receberDano(counterAdj);
                }
                return true;
            }

            // sem reação efetiva
            int danoParaAplicar = aplicarModificadorClasseAoDanoRecebido(heroiDef, Math.max(0, rr.damageTaken), atacante);
            heroiDef.receberDano(danoParaAplicar);
            if (rr.counterDamage > 0) {
                int counterAdj = aplicarModificadorClasseAoDanoRecebido(atacante, rr.counterDamage, heroiDef);
                atacante.receberDano(counterAdj);
            }
            return true;
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

    private void aplicarAcaoResultado(AcaoResultado res) {
        if (res == null) return;
        if (res.mensagem != null && !res.mensagem.isEmpty()) System.out.println(res.mensagem);

        if (res.curaAoHeroi != 0) {
            if (res.curaAoHeroi > 0) {
                heroi.curarPor(res.curaAoHeroi);
                System.out.println(heroi.getNome() + " recuperou " + res.curaAoHeroi + " de vida.");
            } else {
                int perda = -res.curaAoHeroi;
                int perdaAdj = aplicarModificadorClasseAoDanoRecebido(heroi, perda, inimigo);
                heroi.receberDano(perdaAdj);
                System.out.println(heroi.getNome() + " perdeu " + perdaAdj + " de vida.");
            }
        }

        if (res.danoAoInimigo > 0) {
            System.out.println("Ação causa " + res.danoAoInimigo + " dano ao inimigo.");
            int danoAdj = aplicarModificadorClasseAoDanoRecebido(inimigo, res.danoAoInimigo, heroi);
            inimigo.receberDano(danoAdj);
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

    /**
     * Aplica o ajuste de dano de entrada via classe do defensor (se houver e for Heroi).
     * Retorna o dano ajustado.
     */
    private int aplicarModificadorClasseAoDanoRecebido(Personagem defensor, int dano, Personagem atacante) {
        if (dano <= 0) return 0;
        if (defensor instanceof Heroi) {
            Heroi h = (Heroi) defensor;
            if (h.getClasse() != null) {
                try {
                    dano = h.getClasse().modificarDanoEntrada(h, dano, atacante);
                } catch (Throwable ignored) {}
            }
        }
        return Math.max(0, dano);
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