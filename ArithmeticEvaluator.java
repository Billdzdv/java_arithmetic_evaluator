package dz.billdzdv.utility;

// =====================================================================================================================
// Author : Billdzdv (iorexx)
// Version : 1.0.1
// Date : April 7, 2018
// Brief : Parse a given arithmetic string expression and evaluate it into a Double.
// =====================================================================================================================
// operators: + - * / unary+ unary- %
// functions: sin cos tan sqrt log ln ^ exp fact
// constants: pi
// =====================================================================================================================

public class ArithmeticEvaluator {
    // variables -------------------------------------------------------------------------------------------------------

    private String expression;
    private char curChar;
    private int position;

    // construction ----------------------------------------------------------------------------------------------------

    ArithmeticEvaluator(String expression) {
        this.expression = expression;
        curChar = (char)-1;
        this.position = -1;
    }

    // user ----------------------------------------------------------------------------------------------------------

    public Double evaluate() {
        if (this.expression.isEmpty())
            return 0.;
        return parse();
    }

    // core ------------------------------------------------------------------------------------------------------------

    private void stepIn() {
        if (++position < expression.length())
            curChar = expression.charAt(position);
        else
            curChar = (char)-1;
    }

    private boolean removeChar(int c) {
        while (curChar == ' ')
            stepIn();
        if (curChar == c) {
            stepIn();
            return true;
        }
        return false;
    }

    private Double parseExpression() {
        Double result = parseTerm();
        while (true) {
            // addition
            if (removeChar('+'))
                result += parseTerm();
            // subtraction
            else if (removeChar('-'))
                result -= parseTerm();
            else
                return result;
        }
    }

    private Double parseTerm() {
        Double result = parseFactor();
        Double tmp = 0.;
        while (true) {
            // multiplication
            if (removeChar('*'))
                result *= parseFactor();
            // division
            else if (removeChar('/')) {
                tmp = parseFactor();
                if (tmp != 0.)
                    result /= tmp;
                else
                    throw new RuntimeException("division by 0");
            }
            // modulation
            else if (removeChar('%')) {
                tmp = parseFactor();
                if (tmp != 0.)
                    result %= tmp;
                else
                    throw new RuntimeException("modulo 0");
            }
            else
                return result;
        }
    }

    private Double parseFactor() {
        // unary operators + and -
        if (removeChar('+'))
            return parseFactor();
        if (removeChar('-'))
            return -parseFactor();

        Double result;
        int startPos = this.position;

        // parentheses
        if (removeChar('(')) {
            result = parseExpression();
            removeChar(')');
        }

        // numbers
        else if ((curChar >= '0' && curChar <= '9') || curChar == '.') {
            while ((curChar >= '0' && curChar <= '9') || curChar == '.')
                stepIn();
            result = Double.parseDouble(expression.substring(startPos, this.position));
        }

        // functions
        else if (curChar >= 'a' && curChar <= 'z') {
            while (curChar >= 'a' && curChar <= 'z')
                stepIn();
            String func = expression.substring(startPos, this.position);
            result = parseFactor();
            switch (func) {
                case "sqrt":
                    result = Math.sqrt(result);
                    break;
                case "sin":
                    result = Math.sin(result);
                    break;
                case "cos":
                    result = Math.cos(result);
                    break;
                case "tan":
                    result = Math.tan(result);
                    break;
                case "ln":
                    result = Math.log(result);
                    break;
                case "log":
                    result = Math.log10(result);
                    break;
                case "exp":
                    result = Math.exp(result);
                    break;
                case "fact":
                    result = this.fact(result);
                    break;
                case "pi":
                    result = Math.PI ; break;
                default:
                    throw new RuntimeException("syntax error");
            }
        }
        else
            throw new RuntimeException("syntax error");

        // power
        if (removeChar('^'))
            result = Math.pow(result, parseFactor());

        return result;
    }

    private Double parse() {
        stepIn();
        Double result = parseExpression();
        if (this.position < expression.length())
            throw new RuntimeException("syntax error");
        return result;
    }

    private Double fact(Double n) {
        Double result = 1.;
        while (n >= 2) {
            result *= n--;
        }
        return result;
    }
}
