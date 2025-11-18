package Services;

/**
 * Resultado padrão retornado pelo ReactionService/Strategies.
 */
public class ReactionResult {
    public int damageTaken;
    public int counterDamage;
    public boolean dodged;
    public boolean blocked;
    public String message;

    public ReactionResult() {
        this.damageTaken = 0;
        this.counterDamage = 0;
        this.dodged = false;
        this.blocked = false;
        this.message = "";
    }
}