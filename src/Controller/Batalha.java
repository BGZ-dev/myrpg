package Controller;

import Dominio.Elemento;
import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Personagem;
import Dominio.Arma;
import Dominio.Invocacao;
import Dominio.Classes.Classe;
import Dominio.Classes.AcaoResultado;
import Dominio.Classes.Invocador;

import Services.ReactionService;
import Services.ReactionResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Batalha — versão com controle de limite de invocações por nível.
 *
 * Principais mudanças:
 * - Invocações permanecem ativas até que sua vida chegue a zero (cleanupInvocacoes).
 * - Ao tentar adicionar uma invocação (ação de classe retorna res.invocacao),
 *   checamos o limite permitido para o nível do herói e só adicionamos se houver espaço.
 * - Limites por nível:
 *     nível 1..3  => 1
 *     nível 4..5  => 2
 *     nível 6..7  => 3
 *     nível 8..10 => 4
 *     nível >= 11 => 5
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

            // tick encantamento e decremento de cooldowns
            heroi.tickEncantamento();
            heroi.decrementarCooldowns();
            inimigo.decrementarCooldowns();

            // remove invocações com vida zerada somente
            cleanupInvocacoes();
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

        // Menu base
        System.out.println("1. Atacar | 2. Ação da Classe (antes: Ataque Especial) | 3. Usar Poção | 4. Ver Status | 5. Fugir");

        // Se o herói é Invocador, mostrar opção para ver invocações ativas
        boolean ehInvocador = (heroi.getClasse() instanceof Invocador);
        if (ehInvocador) {
            System.out.println("6. Ver Invocações Ativas");
        }

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

        // Processa escolha especial do invocador
        if (escolha == 6 && ehInvocador) {
            mostrarInvocacoesAtivas();
            // permitir escolher novamente
            turnoDoHeroi();
            return;
        }

        switch (escolha) {
            case 1: {
                System.out.println(heroi.getNome() + " ataca com " + (heroi.getArma() != null ? heroi.getArma().getNome() : "punhos") + "!");
                realizarAtaque(heroi, inimigo, heroi.calcularDanoBase(), null);
                // invocações atacam após ataque do herói
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
                    // invocações atacam após ação de classe
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
                // não consome turno: permite ver status e escolher novamente
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
        // invocações atacam inimigo; invocações removidas somente quando vida <= 0 (cleanupInvocacoes)
        for (Invocacao pet : new ArrayList<>(invocacoes)) {
            if (!pet.estaVivo()) continue;
            int danoPet = pet.calcularDanoBase();
            System.out.println(pet.getNome() + " ataca e causa " + danoPet + " ao inimigo!");
            int danoAdj = aplicarModificadorClasseAoDanoRecebido(inimigo, danoPet, pet);
            inimigo.receberDano(danoAdj);
            if (!inimigo.estaVivo()) {
                System.out.println(inimigo.getNome() + " foi derrotado pela invocação!");
                break;
            }
        }
        // não removemos invocações aqui; limpeza centralizada
    }

    private void turnoDoInimigo() {
        System.out.println("\n--- TURNO DO INIMIGO ---");
        String tipoLabel = "";
        try {
            tipoLabel = inimigo.getAttackTypeLabel();
        } catch (Throwable ignored) { tipoLabel = ""; }
        if (!tipoLabel.isEmpty()) {
            System.out.println(inimigo.getNome() + " [" + tipoLabel + "] (" + inimigo.getElemento() + ") ataca!");
        } else {
            System.out.println(inimigo.getNome() + " (" + inimigo.getElemento() + ") ataca!");
        }

        // atacar herói
        realizarAtaque(inimigo, heroi, inimigo.calcularDanoBase(), inimigo.getElemento());

        // limpeza de invocações com vida zerada após possíveis efeitos
        cleanupInvocacoes();
    }

    /**
     * Realiza um ataque aplicando efeito elemental, tentando reação do defensor (se for herói oferece prompt),
     * e aplicando o resultado (incluindo modificador da classe do defensor).
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

        // Se defensor é o herói e atacante é Inimigo, permite reação manual
        if (defensor instanceof Heroi && atacante instanceof Inimigo) {
            boolean handled = promptHeroReaction((Heroi) defensor, atacante, danoCalculado, elementoAtaque);
            if (handled) return; // já tratado dentro do prompt (inclui aplicação de dano)
            // se retornou false, prosseguir para fallback automático
        }

        // fallback automático usando ReactionService
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

        if (reaction.message != null && !reaction.message.isEmpty()) {
            System.out.println(reaction.message);
        }

        if (reaction.dodged) {
            return;
        }

        if (reaction.blocked) {
            int danoAReceber = Math.max(0, reaction.damageTaken);
            int danoParaAplicar = aplicarModificadorClasseAoDanoRecebido(defensor, danoAReceber, atacante);
            System.out.println(defensor.getNome() + " recebeu " + danoParaAplicar + " de dano após o bloqueio!");
            defensor.receberDano(danoParaAplicar);

            if (reaction.counterDamage > 0) {
                int counterAdj = aplicarModificadorClasseAoDanoRecebido(atacante, reaction.counterDamage, defensor);
                System.out.println(defensor.getNome() + " contra-ataca causando " + counterAdj + " a " + atacante.getNome() + "!");
                atacante.receberDano(counterAdj);
            }
            return;
        }

        // sem reação efetiva
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
     * Prompt interativo que delega ao ReactionService.attemptSpecificReaction(...)
     * Retorna true se a reação foi tratada (válida) e aplicada; false para fallback automático.
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
                return false;
            }

            if (rr == null) {
                System.out.println("Erro no processamento da reação — aplicando fallback.");
                return false;
            }

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
        System.out.println("\nEscolha o elemento do ataque/efeito:");
        System.out.println("1.Fogo 2.Água 3.Terra 4.Ar 5.Luz 6.Sombra 7.Raio 8.Gelo");
        int escolha = 1;
        try {
            escolha = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
        }
        return Elemento.values()[Math.max(0, Math.min(7, escolha - 1))];
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
            System.out.println("Lifesteal aumentado em " + (int) (res.lifestealBonus * 100) + "% por " + res.lifestealTurnos + " turnos.");
        }

        // Aqui controlamos o limite de invocações por nível antes de adicionar
        if (res.invocacao != null) {
            int limite = maxInvocacoesPermitidas(heroi.getNivel());
            int ativas = invocacoes.size();
            if (ativas >= limite) {
                System.out.println("❌ Você não pode invocar mais. Limite de invocações para seu nível (" + heroi.getNivel() + ") é: " + limite + " (ativas: " + ativas + ").");
            } else {
                invocacoes.add(res.invocacao);
                System.out.println(res.invocacao.getNome() + " foi invocado com " + res.invocacao.getVida() + " vida.");
                System.out.println("Invocações ativas: " + (invocacoes.size()) + "/" + limite);
            }
        }
    }

    /**
     * Remove somente invocações cuja vida esteja zerada (<= 0).
     * Imprime uma mensagem de desaparecimento para cada uma removida.
     */
    private void cleanupInvocacoes() {
        if (invocacoes.isEmpty()) return;
        Iterator<Invocacao> it = invocacoes.iterator();
        while (it.hasNext()) {
            Invocacao pet = it.next();
            if (!pet.estaVivo()) {
                System.out.println("💀 " + pet.getNome() + " desapareceu (vida zerada).");
                it.remove();
            }
        }
    }

    private int maxInvocacoesPermitidas(int nivel) {
        if (nivel <= 0) return 1;
        if (nivel <= 3) return 1;
        if (nivel <= 5) return 2;
        if (nivel <= 7) return 3;
        if (nivel <= 10) return 4;
        return 5;
    }

    private int aplicarModificadorClasseAoDanoRecebido(Personagem defensor, int dano, Personagem atacante) {
        if (dano <= 0) return 0;
        if (defensor instanceof Heroi) {
            Heroi h = (Heroi) defensor;
            Classe cls = h.getClasse();
            if (cls != null) {
                try {
                    dano = cls.modificarDanoEntrada(h, dano, atacante);
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
    private void mostrarInvocacoesAtivas() {
        System.out.println("\n=== INVOCAÇÕES ATIVAS ===");
        if (invocacoes.isEmpty()) {
            System.out.println("Nenhuma invocação ativa no momento.");
        } else {
            int i = 1;
            for (Invocacao p : invocacoes) {
                System.out.println(i + ". " + p.getNome() + " | Elemento: " + p.getElemento() + " | Vida: " + p.getVida() + " | Ataque: " + p.getAtaque());
                i++;
            }
            int limite = maxInvocacoesPermitidas(heroi.getNivel());
            System.out.println("Total: " + invocacoes.size() + " / Limite para nível " + heroi.getNivel() + " = " + limite);
        }
        System.out.println("=========================\n");
    }
}