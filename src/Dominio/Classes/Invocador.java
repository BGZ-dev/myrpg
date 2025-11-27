package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Invocacao;
import Dominio.Elemento;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Invocador melhorado:
 * - suporta elementos permitidos (até 3)
 * - 3 tipos de invocação: Golem (tank), Feral (alto dano), Sentinel (equilíbrio)
 * - as stats das invocações escalam com: nivel do invocador + pontos em 'encantamento'
 */
public class Invocador implements Classe {
    private final Random rand = new Random();
    private final List<Elemento> elementosPermitidos;

    public Invocador() {
        this.elementosPermitidos = new ArrayList<>();
    }

    public Invocador(List<Elemento> elementosPermitidos) {
        if (elementosPermitidos == null) this.elementosPermitidos = new ArrayList<>();
        else {
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
    public String getNome() { return "Invocador"; }

    @Override
    public int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo) { return danoBase; }

    @Override
    public int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante) { return danoRecebido; }

    @Override
    public void aplicarBuffInicial(Heroi heroi) { }

    @Override
    public void aoFinalDoTurno(Heroi heroi, Inimigo inimigo) { }

    @Override
    public Dominio.Classes.AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        Dominio.Classes.AcaoResultado r = new Dominio.Classes.AcaoResultado();

        // 1) Escolher tipo
        System.out.println("Escolha o tipo de invocação:");
        System.out.println("1. Golem (Tank) — Vida alta, dano baixo");
        System.out.println("2. Feral (Ataque) — Dano alto, vida baixa");
        System.out.println("3. Sentinel (Equilíbrio) — Vida/dano médios");
        System.out.print("Escolha (1-3): ");
        int tipo = 1;
        try { tipo = scanner.nextInt(); scanner.nextLine(); } catch (Exception ex) { scanner.nextLine(); }
        if (tipo < 1 || tipo > 3) tipo = 1;

        // 2) Escolher elemento (entre permitidos se houver)
        List<Elemento> opcoes = new ArrayList<>();
        if (elementosPermitidos.isEmpty()) {
            for (Elemento e : Elemento.values()) opcoes.add(e);
        } else {
            opcoes.addAll(elementosPermitidos);
        }

        System.out.println("Escolha elemento da invocação:");
        for (int i = 0; i < opcoes.size(); i++) {
            System.out.println((i+1) + ". " + opcoes.get(i));
        }
        System.out.print("Escolha (1-" + opcoes.size() + "): ");
        int escolhaElem = 1;
        try { escolhaElem = scanner.nextInt(); scanner.nextLine(); } catch (Exception ex) { scanner.nextLine(); }
        if (escolhaElem < 1) escolhaElem = 1;
        if (escolhaElem > opcoes.size()) escolhaElem = opcoes.size();
        Elemento elementoEscolhido = opcoes.get(escolhaElem - 1);

        // 3) Ficha base por tipo
        int baseVida, baseForca, baseDestreza, baseConstituicao;
        String nomeTipo;
        if (tipo == 1) { // Golem
            nomeTipo = "Golem";
            baseVida = 40; baseForca = 3; baseDestreza = 1; baseConstituicao = 9;
        } else if (tipo == 2) { // Feral
            nomeTipo = "Feral";
            baseVida = 18; baseForca = 9; baseDestreza = 6; baseConstituicao = 2;
        } else { // Sentinel
            nomeTipo = "Sentinel";
            baseVida = 28; baseForca = 5; baseDestreza = 4; baseConstituicao = 5;
        }

        // 4) Escala por "poder do invocador" = nivel + encantamento
        int nivel = Math.max(1, heroi.getNivel());
        int ench = Math.max(0, heroi.getEncantamento());
        int poder = nivel + ench; // pedido: nível + pontos em encantamento

        int vida = baseVida + poder * 4;
        int constituicao = baseConstituicao + poder;
        int forca = baseForca + (poder / 2);
        int destreza = baseDestreza + (poder / 3);

        // pequena variação aleatória
        vida += rand.nextInt(Math.max(1, poder / 2 + 1));
        forca += rand.nextInt(Math.max(1, poder / 4 + 1));
        destreza += rand.nextInt(Math.max(1, poder / 5 + 1));
        constituicao += rand.nextInt(Math.max(1, poder / 6 + 1));

        String nomeInv = nomeTipo + "(" + elementoEscolhido + ")";
        Invocacao pet = new Invocacao(nomeInv, Math.max(1, vida), Math.max(1, forca), Math.max(1, destreza), Math.max(1, constituicao), elementoEscolhido, heroi);

        r.invocacao = pet;
        r.mensagem = "🐾 " + heroi.getNome() + " invocou " + pet.getNome() + " [Vida: " + pet.getVida() + ", ATQ: " + pet.getAtaque() + "]";
        return r;
    }

    public List<Elemento> getElementosPermitidos() { return new ArrayList<>(elementosPermitidos); }
}