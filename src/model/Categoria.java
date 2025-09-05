package model;

public enum Categoria {
    FESTA,
    ESPORTE,
    SHOW,
    CONFERENCIA,
    TEATRO,
    OUTROS;

    public static Categoria fromString(String s) {
        try {
            return Categoria.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return OUTROS;
        }
    }
}
