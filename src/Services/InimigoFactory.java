package Services;

import Dominio.Elemento;
import Dominio.Inimigo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SERVIÇO (Factory) - Versão estendida
 * Gera inimigos variados com nome, elemento, stats escalados por nível do herói e raridade.
 * Agora cada template inclui um AttackType (MELEE / RANGED / MAGIC / SPECIAL).
 */
public class InimigoFactory {
    private static final Random rand = new Random();

    private enum Raridade { COMUM, RARO, ELITE, CHEFE }

    private static class Template {
        final String nomeBase;
        final int vidaBase;
        final int ataqueBase;
        final int defesaBase;
        final Elemento elemento;
        final Raridade raridade;
        final double raridadeMultiplier;
        final Inimigo.AttackType attackType;

        Template(String nomeBase, int vidaBase, int ataqueBase, int defesaBase, Elemento elemento, Raridade raridade, double raridadeMultiplier, Inimigo.AttackType attackType) {
            this.nomeBase = nomeBase;
            this.vidaBase = vidaBase;
            this.ataqueBase = ataqueBase;
            this.defesaBase = defesaBase;
            this.elemento = elemento;
            this.raridade = raridade;
            this.raridadeMultiplier = raridadeMultiplier;
            this.attackType = attackType == null ? Inimigo.AttackType.MELEE : attackType;
        }
    }

    private static final List<Template> TEMPLATES = new ArrayList<>();
    static {
        // COMUNS
        TEMPLATES.add(new Template("Goblin", 40, 8, 4, Elemento.TERRA, Raridade.COMUM, 1.0, Inimigo.AttackType.MELEE));
        TEMPLATES.add(new Template("Slime", 35, 6, 2, Elemento.AGUA, Raridade.COMUM, 1.0, Inimigo.AttackType.MAGIC));
        TEMPLATES.add(new Template("Bandido", 45, 10, 5, Elemento.AR, Raridade.COMUM, 1.0, Inimigo.AttackType.RANGED));
        TEMPLATES.add(new Template("Lobo Selvagem", 50, 11, 3, Elemento.TERRA, Raridade.COMUM, 1.0, Inimigo.AttackType.MELEE));

        // RAROS
        TEMPLATES.add(new Template("Harpia", 70, 14, 6, Elemento.AR, Raridade.RARO, 1.25, Inimigo.AttackType.RANGED));
        TEMPLATES.add(new Template("Necromante Aprendiz", 65, 16, 5, Elemento.SOMBRA, Raridade.RARO, 1.25, Inimigo.AttackType.MAGIC));
        TEMPLATES.add(new Template("Escudeiro Orc", 80, 18, 8, Elemento.TERRA, Raridade.RARO, 1.25, Inimigo.AttackType.MELEE));

        // ELITE
        TEMPLATES.add(new Template("Cavaleiro Sombrio", 120, 22, 12, Elemento.SOMBRA, Raridade.ELITE, 1.6, Inimigo.AttackType.MELEE));
        TEMPLATES.add(new Template("Elemento de Gelo", 110, 20, 10, Elemento.GELO, Raridade.ELITE, 1.6, Inimigo.AttackType.MAGIC));
        TEMPLATES.add(new Template("Mago do Trovão", 105, 24, 8, Elemento.RAIO, Raridade.ELITE, 1.6, Inimigo.AttackType.MAGIC));

        // CHEFE
        TEMPLATES.add(new Template("Dragão Ancião", 250, 35, 20, Elemento.FOGO, Raridade.CHEFE, 2.4, Inimigo.AttackType.SPECIAL));
        TEMPLATES.add(new Template("Lich Supremo", 220, 38, 15, Elemento.SOMBRA, Raridade.CHEFE, 2.4, Inimigo.AttackType.SPECIAL));
    }

    /**
     * Gera um inimigo aleatório baseado no nível do herói.
     * A função escolhe um template com ponderação implícita (mais probabilidade para comuns).
     */
    public static Inimigo gerarInimigoAleatorio(int nivelHeroi) {
        Template t = escolherTemplatePorPonderacao();

        // escala por nível do herói (aumenta stats linearmente) e por raridade
        int vida = (int) Math.max(10, Math.round((t.vidaBase + nivelHeroi * 12 + rand.nextInt(15)) * t.raridadeMultiplier));
        int ataque = (int) Math.max(1, Math.round((t.ataqueBase + nivelHeroi * 2 + rand.nextInt(6)) * t.raridadeMultiplier));
        int defesa = (int) Math.max(0, Math.round((t.defesaBase + nivelHeroi + rand.nextInt(4)) * t.raridadeMultiplier));

        String nome = t.nomeBase + " [" + t.raridade.name() + "]";

        // Mensagem curta no console
        System.out.println("-> Gerado inimigo: " + nome + " (" + t.elemento + ")  | Vida: " + vida + " ATQ: " + ataque + " DEF: " + defesa + " | Tipo de ataque: " + t.attackType);

        return new Inimigo(nome, vida, ataque, defesa, t.elemento, t.attackType);
    }

    // Escolhe um template com probabilidade maior para comuns, menor para chefes
    private static Template escolherTemplatePorPonderacao() {
        int roll = rand.nextInt(100);
        Raridade alvo;
        if (roll < 60) alvo = Raridade.COMUM;
        else if (roll < 85) alvo = Raridade.RARO;
        else if (roll < 95) alvo = Raridade.ELITE;
        else alvo = Raridade.CHEFE;

        List<Template> candidatos = new ArrayList<>();
        for (Template temp : TEMPLATES) {
            if (temp.raridade == alvo) candidatos.add(temp);
        }
        if (candidatos.isEmpty()) return TEMPLATES.get(rand.nextInt(TEMPLATES.size()));
        return candidatos.get(rand.nextInt(candidatos.size()));
    }
}