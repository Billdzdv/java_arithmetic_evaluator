package dz.billdzdv.utility;

// =====================================================================================================================
// Author : Billdzdv
// Version : 1.0.0
// Date : April 7, 2018
// Brief : Parse a given string and evaluate the result into a double.
// =====================================================================================================================

public class ArithmeticEvaluator {

    // member variables ------------------------------------------------------------------------------------------------

    private String expression; // expression to be evaluated
    private char curChar; // current char
    private int position; // current char id

    // constructors ----------------------------------------------------------------------------------------------------

    ArithmeticEvaluator(String expression) {
        this.expression = expression;
        curChar = (char)-1;
        this.position = -1;
    }

    // public methods --------------------------------------------------------------------------------------------------

    // user method: reset expression
    public void setExpression(String expression) {
        this.expression = expression;
        curChar = (char)-1;
        this.position = -1;
    }

    // user method: evaluate expression
    public double evaluate() {
        if (this.expression.isEmpty())
            return 0.;
        return parse();
    }

    // private methods -------------------------------------------------------------------------------------------------

    // get the next char from expression
    private void stepIn() {
        if (++position < expression.length())
            curChar = expression.charAt(position);
        else
            curChar = (char)-1;
    }

    // remove spaces and the given char from expression
    private boolean removeChar(int c) {
        // Clear spaces
        while (curChar == ' ')
            stepIn();

        // Remove the char
        if (curChar == c) {
            stepIn();
            return true;
        }
        return false;
    }

    // parse expressions, exp: expr + expr - expr
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

    // parse terms in expression, exp: term * term / term.
    private Double parseTerm() {
        Double result = parseFactor();
        Double denom = 0.;
        while (true) {
            // multiplication
            if (removeChar('*'))
                result *= parseFactor();
                // division
            else if (removeChar('/')) {
                denom = parseFactor();
                if (denom != 0.)
                    result /= denom;
                else
                    throw new RuntimeException("Divide by 0!");
            }
            else
                return result;
        }
    }

    // parse factors in term, exp: -sin(10)
    private Double parseFactor() {
        // unary operators + and -
        if (removeChar('+'))
            return parseFactor();
        if (removeChar('-'))
            return -parseFactor();

        Double result;
        int startPos = this.position;

        // parentheses ()
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
                default:
                    throw new RuntimeException("Unknown function: " + func);
            }
        }
        else
            throw new RuntimeException("Unknown character: " + curChar);

        // power
        if (removeChar('^'))
            result = Math.pow(result, parseFactor());

        return result;
    }

    // start the parsing process
    private Double parse() {
        stepIn();
        Double result = parseExpression();
        if (this.position < expression.length())
            throw new RuntimeException("Evaluation failure: " + curChar);
        return result;
    }
}