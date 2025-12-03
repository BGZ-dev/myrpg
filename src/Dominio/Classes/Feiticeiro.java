package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Personagem;
import Dominio.Elemento;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Feiticeiro implements Classe {

    private final Random rand = new Random();
    private final List<Elemento> elementosPermitidos;

    public Feiticeiro() { this.elementosPermitidos = new ArrayList<>(); }

    public Feiticeiro(List<Elemento> elementosPermitidos) {
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
    public String getNome() { return "Feiticeiro"; }

    @Override
    public int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo) {
        return danoBase;
    }

    @Override
    public int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante) {
        return danoRecebido;

    @Override
    public void aplicarBuffInicial(Heroi heroi) {
    }

    @Override
    public void aoFinalDoTurno(Heroi heroi, Inimigo inimigo) { }

    @Override
    public AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        AcaoResultado r = new AcaoResultado();

        List<Elemento> opcoes;
        if (elementosPermitidos.isEmpty()) {
            opcoes = new ArrayList<>();
            for (Elemento e : Elemento.values()) opcoes.add(e);
        } else {
            opcoes = new ArrayList<>(elementosPermitidos);
        }

        System.out.println("🔮 Encantamento Amaldiçoado — escolha alvo:");
        System.out.println("1. Encantar arma (se possuir)");
        System.out.println("2. Encantar punhos");
        int alvo = 2;
        try { alvo = scanner.nextInt(); scanner.nextLine(); } catch (Exception e) { scanner.nextLine(); }

        if (alvo == 1 && heroi.getArma() == null) {
            System.out.println("Você não tem arma. Encantando punhos no lugar.");
            alvo = 2;
        }

        System.out.println("Escolha elemento do encantamento:");
        for (int i = 0; i < opcoes.size(); i++) {
            System.out.println((i+1) + ". " + opcoes.get(i));
        }
        int escolha = 1;
        try { escolha = scanner.nextInt(); scanner.nextLine(); } catch (Exception e) { scanner.nextLine(); }
        Elemento elemento = opcoes.get(Math.max(0, Math.min(opcoes.size()-1, escolha-1)));

        int ench = Math.max(0, heroi.getEncantamento());
        double multiplicador = 0.4 + ench * 0.08;
        if (multiplicador > 2.0) multiplicador = 2.0;

        int duracao = 2 + Math.max(1, ench / 3);

        boolean noArma = (alvo == 1);
        heroi.aplicarEncantamento(elemento, noArma, multiplicador, duracao);

        r.mensagem = "🔮 Encantamento aplicado: " + elemento + " no " + (noArma ? "armamento" : "punhos") + " por " + duracao + " turnos.";
        return r;
    }
}