package Dominio;

/**
 * DOMÍNIO
 * Enum que define os elementos e suas interações de efetividade.
 * É o coração do sistema de combate elemental.
 */
public enum Elemento {
    FOGO, AGUA, TERRA, AR, LUZ, SOMBRA, RAIO, GELO;

    public double efetividadeContra(Elemento outro) {
        if (outro == null) return 1.0;
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