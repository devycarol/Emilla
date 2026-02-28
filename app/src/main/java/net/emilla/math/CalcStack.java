package net.emilla.math;

import androidx.annotation.StringRes;

import java.util.function.IntFunction;

final class CalcStack
<
    V,
    O extends Enum<O> & CalcOperator<V>,
    S extends Enum<S> & CalcSign<V>
>
{
    private final V[] vals;
    private final EnumStack<S> signs;
    @StringRes
    private final int errorTitle;
    private int size = 0;

    CalcStack(
        int capacity,
        IntFunction<S> signs,
        IntFunction<V[]> generator,
        @StringRes int errorTitle
    ) {
        this.vals = generator.apply(capacity);
        this.signs = new EnumStack<S>(capacity, signs, errorTitle);
        this.errorTitle = errorTitle;
    }

    public void push(V operand) {
        while (signs.notEmpty()) {
            if (signs.peek() == null) {
                // left paren
                break;
            }

            operand = signs.pop().apply(operand);
            // peek is valid, therefore pop is valid.
        }

        vals[size] = operand;
        ++size;
    }

    public void squish(O operator) {
        if (size < 2) {
            throw Maths.malformedExpression(errorTitle);
        }

        --size;
        vals[size - 1] = operator.apply(vals[size - 1], vals[size]);
    }

    public void applySign(S sign) {
        if (sign.isPostfix()) {
            int last = size - 1;
            vals[last] = sign.apply(vals[last]);
        } else if (!sign.isIdempotent()) {
            signs.push(sign);
        }
    }

    public void applyOperator(O operator, EnumStack<O> operators) {
        while (operators.notEmpty()) {
            O peek = operators.peek();
            if (peek == null
                // left paren
                || operator.precedence() > peek.precedence()
                || operator.isRightAssociative() && operator.precedence() == peek.precedence()
            ) {
                break;
            }
            squish(operators.pop()); // peek is valid, therefore pop is valid.
        }
        operators.push(operator);
    }

    public void applyLParen() {
        signs.push(null);
    }

    public void applyRParen(EnumStack<O> operators) {
        while (operators.notEmpty()) {
            O pop = operators.pop();
            if (pop == null) {
                // left paren
                break;
            }

            squish(pop);
        }

        if (signs.notEmpty()) {
            closeSignParen();
        }
    }

    public void applyRemainingSigns() {
        while (signs.notEmpty()) {
            closeSignParen();
        }
    }

    private void closeSignParen() {
        if (signs.peek() == null) {
            // left paren
            signs.pop();
            int last = size - 1;
            while (signs.notEmpty()) {
                S peek = signs.peek();
                if (peek == null) {
                    // left paren
                    break;
                }

                vals[last] = signs.pop().apply(vals[last]);
                // peek is valid, therefore pop is valid.
            }
        }
    }

    public V value() {
        if (size != 1) {
            throw Maths.malformedExpression(errorTitle);
        }

        return vals[0];
    }
}
