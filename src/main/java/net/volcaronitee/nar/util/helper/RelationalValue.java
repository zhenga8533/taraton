package net.volcaronitee.nar.util.helper;

/**
 * Represents a relational value with an operator and a float value.
 */
public class RelationalValue {
    private final Operator operator;
    private final float value;

    /**
     * Constructs a RelationalValue with the specified operator and value.
     * 
     * @param operator The operator to use for comparison.
     * @param value The float value to compare against.
     */
    public RelationalValue(Operator operator, float value) {
        this.operator = operator;
        this.value = value;
    }

    /**
     * Returns the operator used for comparison.
     * 
     * @return The operator used for comparison.
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Returns the float value that this relational value represents.
     * 
     * @return The float value that this relational value represents.
     */
    public float getValue() {
        return value;
    }

    /**
     * Evaluates the relational value against a target float value.
     * 
     * @param targetValue The target float value to compare against the relational value.
     * @return True if the target value satisfies the relational condition defined by this
     *         relational value, false otherwise.
     */
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

    /**
     * Represents the operator used in relational comparisons.
     */
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

        /**
         * Constructs an Operator with the specified symbol.
         * 
         * @param symbol The symbol representing the operator.
         */
        Operator(String symbol) {
            this.symbol = symbol;
        }

        /**
         * Returns the symbol representing this operator.
         * 
         * @return The symbol representing this operator.
         */
        public String getSymbol() {
            return symbol;
        }

        /**
         * Returns the Operator corresponding to the given symbol.
         * 
         * @param symbol The symbol representing the operator.
         * @return The Operator corresponding to the given symbol, or EQUAL if no match is found.
         */
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
