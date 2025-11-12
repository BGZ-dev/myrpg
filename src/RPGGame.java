import java.util.Random;
import java.util.Scanner;

enum Elemento {
    FOGO, AGUA, TERRA, AR, LUZ, SOMBRA, RAIO, GELO;

    public double efetividadeContra(Elemento outro) {
        return switch (this) {
            case FOGO -> (outro == GELO || outro == TERRA ? 2.0 : (outro == AGUA ? 0.5 : 1.0));
            case AGUA -> (outro == FOGO || outro == RAIO ? 2.0 : (outro == TERRA ? 0.5 : 1.0));
            case TERRA -> (outro == RAIO || outro == AR ? 2.0 : (outro == AGUA ? 0.5 : 1.0));
            case AR -> (outro == TERRA ? 2.0 : (outro == RAIO ? 0.5 : 1.0));
            case LUZ -> (outro == SOMBRA ? 2.0 : 1.0);
            case SOMBRA -> (outro == LUZ ? 2.0 : 1.0);
            case RAIO -> (outro == AGUA ? 2.0 : (outro == TERRA ? 0.5 : 1.0));
            case GELO -> (outro == AR ? 2.0 : (outro == FOGO ? 0.5 : 1.0));
        };
    }
}

class Arma {
    private String nome;
    private int bonusAtaque;
    private String especial;

    public Arma(String nome, int bonusAtaque, String especial) {
        this.nome = nome;
        this.bonusAtaque = bonusAtaque;
        this.especial = especial;
    }

    public String getNome() {
        return nome;
    }

    public int getBonusAtaque() {
        return bonusAtaque;
    }

    public String getEspecial() {
        return especial;
    }
}

abstract class Personagem {
    protected String nome;
    protected int vida;
    protected int ataque;
    protected int defesa;
    protected Elemento elemento;

    public Personagem(String nome, int vida, int ataque, int defesa, Elemento elemento) {
        this.nome = nome;
        this.vida = vida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.elemento = elemento;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public String getNome() {
        return nome;
    }

    public int getVida() {
        return vida;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void receberDano(int dano) {
        vida -= dano;
        if (vida < 0) vida = 0;
    }

    public abstract int atacar();
}

class Heroi extends Personagem {
    private int nivel;
    private int experiencia;
    private int potesDeCura;
    private Arma arma;
    private Random rand = new Random();

    public Heroi(String nome, Arma arma) {
        super(nome, 100, 20 + arma.getBonusAtaque(), 10, null);
        this.arma = arma;
        this.nivel = 1;
        this.experiencia = 0;
        this.potesDeCura = 3;
    }

    @Override
    public int atacar() {
        int dano = ataque + rand.nextInt(10);
        System.out.println(nome + " ataca com " + arma.getNome() + " e causa " + dano + " de dano!");
        return dano;
    }

    public int ataqueEspecial(Elemento elementoAtaque) {
        int dano = ataque * 2 + rand.nextInt(15);
        System.out.println(nome + " usa ATAQUE ESPECIAL (" + elementoAtaque + ") e causa " + dano + " de dano base!");
        return dano;
    }

    public void curar() {
        if (potesDeCura > 0) {
            int cura = 30;
            vida += cura;
            if (vida > 100 + (nivel - 1) * 20)
                vida = 100 + (nivel - 1) * 20;
            potesDeCura--;
            System.out.println(nome + " usou uma poção e curou " + cura + " de vida!");
        } else {
            System.out.println("Você não tem mais poções!");
        }
    }

    public void ganharExperiencia(int xp) {
        experiencia += xp;
        System.out.println(nome + " ganhou " + xp + " de experiência!");
        if (experiencia >= 100 * nivel) {
            nivel++;
            experiencia = 0;
            ataque += 5;
            defesa += 3;
            vida = 100 + (nivel - 1) * 20;
            System.out.println("*** " + nome + " subiu para o nível " + nivel + "! ***");
        }
    }

    public void buffPermanente() {
        ataque += 1;
        defesa += 1;
        System.out.println("✨ " + nome + " ficou mais forte! (+1 ATQ, +1 DEF permanentemente)");
    }

    public void status() {
        System.out.println("=== STATUS ===");
        System.out.println("Nome: " + nome);
        System.out.println("Vida: " + vida);
        System.out.println("Nível: " + nivel);
        System.out.println("Ataque: " + ataque);
        System.out.println("Defesa: " + defesa);
        System.out.println("Poções: " + potesDeCura);
        System.out.println("Arma: " + arma.getNome() + " (" + arma.getEspecial() + ")");
        System.out.println("==============");
    }
}

class Inimigo extends Personagem {
    private Random rand = new Random();

    public Inimigo(String nome, int vida, int ataque, int defesa, Elemento elemento) {
        super(nome, vida, ataque, defesa, elemento);
    }

    @Override
    public int atacar() {
        int dano = ataque + rand.nextInt(8);
        System.out.println(nome + " (" + elemento + ") ataca e causa " + dano + " de dano!");
        return dano;
    }
}

public class RPGGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random rand = new Random();

        System.out.println("Bem-vindo ao RPG de Terminal!");
        System.out.print("Digite o nome do seu herói: ");
        String nomeHeroi = scanner.nextLine();

        Arma armaEscolhida = escolherArma(scanner);
        Heroi heroi = new Heroi(nomeHeroi, armaEscolhida);

        System.out.println("\nOlá, " + heroi.getNome() + "! Sua jornada começa agora...\n");

        boolean jogando = true;

        while (jogando && heroi.estaVivo()) {
            Inimigo inimigo = gerarInimigoAleatorio();
            System.out.println("\nUm inimigo apareceu: " + inimigo.getNome() + " (" + inimigo.getElemento() + ")!");
            System.out.println("Prepare-se para o combate!\n");

            while (inimigo.estaVivo() && heroi.estaVivo()) {
                System.out.println("\nSua vida: " + heroi.getVida() + " | Vida do inimigo: " + inimigo.getVida());
                System.out.println("1. Atacar");
                System.out.println("2. Ataque especial");
                System.out.println("3. Usar poção");
                System.out.println("4. Ver status");
                System.out.println("5. Fugir");
                System.out.print("Escolha uma ação: ");
                int escolha = scanner.nextInt();

                switch (escolha) {
                    case 1 -> atacarComElemento(heroi, inimigo, heroi.atacar(), null);
                    case 2 -> {
                        Elemento elementoAtaque = escolherElemento(scanner);
                        atacarComElemento(heroi, inimigo, heroi.ataqueEspecial(elementoAtaque), elementoAtaque);
                    }
                    case 3 -> heroi.curar();
                    case 4 -> heroi.status();
                    case 5 -> {
                        System.out.println("Você fugiu da batalha!");
                        jogando = false;
                        break;
                    }
                    default -> System.out.println("Opção inválida!");
                }

                if (!inimigo.estaVivo()) {
                    System.out.println("\nVocê derrotou " + inimigo.getNome() + "!");
                    int xpGanho = 50 + rand.nextInt(50);
                    heroi.ganharExperiencia(xpGanho);
                    heroi.buffPermanente();
                    break;
                }

                if (escolha >= 1 && escolha <= 3 && inimigo.estaVivo()) {
                    atacarComElemento(inimigo, heroi, inimigo.atacar(), inimigo.getElemento());
                }

                if (!heroi.estaVivo()) {
                    System.out.println("\n" + heroi.getNome() + " foi derrotado...");
                    jogando = false;
                }
            }
        }

        System.out.println("\n=== Fim de Jogo ===");
        scanner.close();
    }

    public static Arma escolherArma(Scanner scanner) {
        System.out.println("\nEscolha sua arma:");
        System.out.println("1. Espada do Amanhecer (+5 ATQ, chance de crítico)");
        System.out.println("2. Adagas Sombrias (+3 ATQ, chance de perfuração)");
        System.out.println("3. Kunais do Silêncio (+4 ATQ, chance de atordoar)");
        System.out.println("4. Sabre de Joyboy (+6 ATQ, chance de dano elemental extra)");
        int escolha = scanner.nextInt();
        return switch (escolha) {
            case 1 -> new Arma("Espada do Amanhecer", 5, "Crítico");
            case 2 -> new Arma("Adagas Sombrias", 3, "Perfuração");
            case 3 -> new Arma("Kunais do Silêncio", 4, "Atordoar");
            default -> new Arma("Sabre de Joyboy", 6, "Dano Elemental Aleatório");
        };
    }

    public static Elemento escolherElemento(Scanner scanner) {
        System.out.println("\nEscolha o elemento do ataque especial:");
        System.out.println("1. Fogo 🔥");
        System.out.println("2. Água 💧");
        System.out.println("3. Terra 🌱");
        System.out.println("4. Ar 🌪️");
        System.out.println("5. Luz ☀️");
        System.out.println("6. Sombra 🌑");
        System.out.println("7. Raio ⚡");
        System.out.println("8. Gelo ❄️");
        int escolha = scanner.nextInt();
        return switch (escolha) {
            case 1 -> Elemento.FOGO;
            case 2 -> Elemento.AGUA;
            case 3 -> Elemento.TERRA;
            case 4 -> Elemento.AR;
            case 5 -> Elemento.LUZ;
            case 6 -> Elemento.SOMBRA;
            case 7 -> Elemento.RAIO;
            case 8 -> Elemento.GELO;
            default -> Elemento.FOGO;
        };
    }

    public static void atacarComElemento(Personagem atacante, Personagem defensor, int danoBase, Elemento elementoAtaque) {
        double mult = (elementoAtaque != null) ? elementoAtaque.efetividadeContra(defensor.getElemento()) : 1.0;
        int danoFinal = (int) ((danoBase - defensor.defesa / 2) * mult);
        if (danoFinal < 0) danoFinal = 0;
        if (elementoAtaque != null)
            System.out.printf("⚡ Efeito elemental: %.1fx (%s vs %s)%n", mult, elementoAtaque, defensor.getElemento());
        defensor.receberDano(danoFinal);
        System.out.println(defensor.getNome() + " recebeu " + danoFinal + " de dano!\n");
    }

    public static Inimigo gerarInimigoAleatorio() {
        Random rand = new Random();
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
