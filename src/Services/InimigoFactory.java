package Services; // Declara que pertence ao pacote Services


import Dominio.Elemento;
import Dominio.Inimigo;
import java.util.Random;


/**
 * SERVIÇO (Factory)
 * Responsável por criar e fornecer objetos do tipo Inimigo.
 */
public class InimigoFactory {
    private static Random rand = new Random();

    public static Inimigo gerarInimigoAleatorio(int nivelHeroi) {
        // Futuramente, o nível do herói pode influenciar o inimigo gerado
        int tipo = rand.nextInt(4);
        Elemento elemento = Elemento.values()[rand.nextInt(Elemento.values().length)];
        return switch (tipo) {
            case 0 -> new Inimigo("Goblin", 60, 15, 5, elemento);
            case 1 -> new Inimigo("Orc", 80, 18, 8, elemento);
            case 2 -> new Inimigo("Troll", 100, 20, 10, elemento);
            default -> new Inimigo("Dragão Jovem", 130, 25, 12, elemento);
        };
    }
}