package net.volcaronitee.nar.util.helper;

public class RelationalValue {
    private final Operator operator;
    private final float value;

    public RelationalValue(Operator operator, float value) {
        this.operator = operator;
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public float getValue() {
        return value;
    }

    public boolean evaluate(float targetValue) {
        return switch (operator) {
            case LESS_THAN -> targetValue < value;
            case GREATER_THAN -> targetValue > value;
            case LESS_THAN_OR_EQUAL -> targetValue <= value;
            case GREATER_THAN_OR_EQUAL -> targetValue >= value;
            case EQUAL -> targetValue == value;
            case NOT_EQUAL -> targetValue != value;
        };
    }

    public enum Operator {
        // @formatter:off
        LESS_THAN("<"),
        GREATER_THAN(">"),
        LESS_THAN_OR_EQUAL("<="),
        GREATER_THAN_OR_EQUAL(">="),
        EQUAL("="),
        NOT_EQUAL("!=");
        // @formatter:on

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public static Operator fromSymbol(String symbol) {
            for (Operator op : values()) {
                if (op.symbol.equals(symbol)) {
                    return op;
                }
            }
            return EQUAL;
        }
    }
}
