package Dominio.Classes;

import Dominio.Heroi;
import Dominio.Inimigo;
import Dominio.Personagem;
import Dominio.Elemento;

import java.util.Scanner;

/**
 * Bruxo (atualizado):
 * - Usa o atributo 'encantamento' como potência.
 * - Ação ativa: Encantamento Amaldiçoado — escolhe encantar arma ou punhos com elemento (energia amaldiçoada).
 *   A potência (multiplicador/duração) escala com o atributo encantamento.
 * - Mantive a ideia de Pacto (lifesteal) removida daqui; agora Bruxo foca em encantar.
 */
public class Bruxo implements Classe {

    @Override
    public String getNome() { return "Bruxo"; }

    @Override
    public int modificarDanoSaida(Heroi heroi, int danoBase, Object alvo) {
        // sem mod passivo extra; o encantamento ativo já é aplicado no Heroi.calcularDanoBase
        return danoBase;
    }

    @Override
    public int modificarDanoEntrada(Heroi heroi, int danoRecebido, Object atacante) {
        // Sem redução passiva
        return danoRecebido;
    }

    @Override
    public void aplicarBuffInicial(Heroi heroi) {
        // Pode deixar um pequeno bônus de encantamento inicial
        // heroi já recebeu encantamento base via atributo
    }

    @Override
    public void aoFinalDoTurno(Heroi heroi, Inimigo inimigo) {
        // sem efeito por turno adicional por padrão
    }

    @Override
    public AcaoResultado executarAcao(Heroi heroi, Inimigo inimigo, Scanner scanner) {
        // Executa Encantamento Amaldiçoado: pergunta se quer encantar arma ou punhos e qual elemento
        AcaoResultado r = new AcaoResultado();
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
        System.out.println("1.Fogo 2.Água 3.Terra 4.Ar 5.Luz 6.Sombra 7.Raio 8.Gelo");
        int escolha = 1;
        try { escolha = scanner.nextInt(); scanner.nextLine(); } catch (Exception e) { scanner.nextLine(); }
        Elemento elemento = Elemento.values()[Math.max(0, Math.min(7, escolha-1))];

        // Potência e duração calculadas a partir do atributo encantamento
        int ench = Math.max(0, heroi.getEncantamento());
        // multiplicador base: 0.4 + 0.08 * encantamento (ex.: encantamento 5 -> mult = 0.4 + 0.4 = 0.8)
        double multiplicador = 0.4 + ench * 0.08;
        if (multiplicador > 2.0) multiplicador = 2.0; // cap por segurança

        int duracao = 2 + Math.max(1, ench / 3); // 2 + floor(encantamento/3) turnos

        boolean noArma = (alvo == 1);
        heroi.aplicarEncantamento(elemento, noArma, multiplicador, duracao);

        r.mensagem = "🔮 Encantamento Amaldiçoado aplicado (" + elemento + ") no " + (noArma ? "armamento" : "punhos") + " por " + duracao + " turnos.";
        // não causa dano imediato (pode, se desejar, causar pequeno dano)
        return r;
    }
}