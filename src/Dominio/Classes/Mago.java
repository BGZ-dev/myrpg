package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Personagem;
import Dominio.Elemento;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Mago com elementos permitidos (até 3).
 *
 * Comportamento:
 * - Pode ser construído vazio (sem restrição) ou com uma lista de elementos (1..3).
 * - executarAcao permite escolher apenas entre os elementos permitidos (se houver).
 * - Ao usar um elemento permitido o feitiço ganha maior chance de crítico.
 * - modificarDanoEntrada aumenta o dano recebido se o atacante usar um elemento eficaz
 *   contra qualquer um dos elementos escolhidos pelo mago (fraqueza).
 */
public class Mago implements Classe {

    private final Random rand = new Random();
    private final List<Elemento> elementosPermitidos;

    public Mago() {
        this.elementosPermitidos = new ArrayList<>(); // vazio = sem restrição (pode usar qualquer elemento)
    }

    public Mago(List<Elemento> elementosPermitidos) {
        if (elementosPermitidos == null) this.elementosPermitidos = new ArrayList<>();
        else {
            // aceita no máximo 3 (defensivo)
            this.elementosPermitidos = new ArrayList<>();
            for (Elemento e : elementosPermitidos) {
                if (e != null && !this.elementosPermitidos.contains(e)) {
                    this.elementosPermitidos.add(e);
                    if (this.elementosPermitidos.size() >= 3) break;
                }
            }
        }
    }

    @Override
    public String getNome() { return "Mago"; }

    @Override
    public int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo) {
        // Passivo geral: pequenos bônus por inteligência já aplicados em executarAcao.
        int bonus = (int)Math.round(heroi.getInteligencia() * 0.4);
        return danoBase + bonus;
    }

    /**
     * Quando o mago é atacado, se o atacante usa um elemento que é eficaz (>1.0)
     * contra qualquer elemento permitido do mago, o dano recebido é aumentado.
     */
    @Override
    public int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante) {
        if (elementosPermitidos.isEmpty()) return danoRecebido; // sem elementos escolhidos => sem fraqueza adicional

        Elemento elementoAtacante = null;
        if (atacante instanceof Personagem) {
            elementoAtacante = ((Personagem) atacante).getElemento();
        } else {
            try {
                java.lang.reflect.Method m = atacante.getClass().getMethod("getElemento");
                Object o = m.invoke(atacante);
                if (o instanceof Elemento) elementoAtacante = (Elemento) o;
            } catch (Exception ignored) {}
        }

        if (elementoAtacante == null) return danoRecebido;

        for (Elemento e : elementosPermitidos) {
            if (elementoAtacante.efetividadeContra(e) > 1.0) {
                // penalidade de fraqueza — +25% de dano recebido (ajustável)
                return (int) Math.round(danoRecebido * 1.25);
            }
        }
        return danoRecebido;
    }

    @Override
    public void aplicarBuffInicial(Heroi heroi) { /* sem buff inicial */ }

    @Override
    public void aoFinalDoTurno(Heroi heroi, Inimigo inimigo) { /* sem efeito por turno */ }

    /**
     * Executa um feitiço: permite escolher um elemento dentre os permitidos (ou qualquer um se lista vazia).
     * Retorna AcaoResultado com danoAoInimigo já calculado (inclui crítico e interação elemental).
     */
    @Override
    public AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        AcaoResultado r = new AcaoResultado();

        // monta opções
        List<Elemento> opcoes = new ArrayList<>();
        if (elementosPermitidos.isEmpty()) {
            for (Elemento e : Elemento.values()) opcoes.add(e);
        } else {
            opcoes.addAll(elementosPermitidos);
        }

        System.out.println("🔮 Escolha o elemento do feitiço:");
        for (int i = 0; i < opcoes.size(); i++) {
            System.out.println((i + 1) + ". " + opcoes.get(i));
        }
        System.out.print("Escolha (1-" + opcoes.size() + "): ");
        int escolha = 1;
        try {
            escolha = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception ex) {
            scanner.nextLine();
        }
        if (escolha < 1) escolha = 1;
        if (escolha > opcoes.size()) escolha = opcoes.size();
        Elemento elemento = opcoes.get(escolha - 1);

        // cálculo de dano base do feitiço
        int dano = (int) Math.round(heroi.getInteligencia() * 2.0 + heroi.getAtaque() * 0.4);

        // chance de crítico: base + inteligência + bônus se elemento pertence aos permitidos
        double critChance = 0.12 + (heroi.getInteligencia() * 0.01);
        if (!elementosPermitidos.isEmpty() && elementosPermitidos.contains(elemento)) {
            critChance += 0.20; // bônus por usar "elemento do mago"
        }
        if (critChance > 0.90) critChance = 0.90;

        boolean critou = rand.nextDouble() < critChance;
        if (critou) {
            dano = (int) Math.round(dano * 1.8);
            r.mensagem = "🔮 Feitiço de " + elemento + " atingiu com CRÍTICO! (" + dano + " dano)";
        } else {
            r.mensagem = "🔮 Mago lança feitiço de " + elemento + " causando " + dano + " dano!";
        }

        // aplicar interação elemental contra o inimigo (se existir elemento do inimigo)
        double mult = elemento.efetividadeContra(inimigo.getElemento());
        int danoFinal = (int) Math.round(dano * mult);
        if (mult != 1.0) {
            r.mensagem += String.format(" (Efeito elemental: %.1fx vs %s)", mult, inimigo.getElemento());
        }
        r.danoAoInimigo = Math.max(0, danoFinal);

        return r;
    }

    // utilitário para UI/debug
    public List<Elemento> getElementosPermitidos() {
        return new ArrayList<>(elementosPermitidos);
    }
}