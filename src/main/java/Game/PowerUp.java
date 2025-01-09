package Game;

public class PowerUp {
    public enum Type {
        SHIELD, ICE, LINE, COLUMN, HINT
    }

    private Type type;
    private int benefits; // Benefícios ativos (para Shield e Ice)

    public PowerUp(Type type) {
        this.type = type;
        this.benefits = 0;
    }

    public Type getType() {
        return type;
    }

    public int getBenefits() {
        return benefits;
    }

    public void incrementBenefits(int value) {
        this.benefits += value;
    }

    public void activate() {
        switch (type) {
            case SHIELD:
                incrementBenefits(1); // Incremental
                break;
            case ICE:
                incrementBenefits(3); // Incremental (3 jogadas por ativação)
                break;
            case LINE:
                // Implementação para revelar linha
                break;
            case COLUMN:
                // Implementação para revelar coluna
                break;
            case HINT:
                // Implementação para sugerir célula segura
                break;
        }
    }
}
