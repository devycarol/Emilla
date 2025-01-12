package net.emilla.util;

import static java.lang.Long.parseLong;
import static java.lang.Math.pow;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

import java.util.regex.Matcher;

public final class Calculator { // i should definitely be using an expression tree LOL

    private static final String ALL_OF = "all ?of", DONE = "done|good";

    // The order in which these are parsed matters because of duplicate words in phrases like "3 by 3" versus "3 divide by 3"
    private static final String
            ADDITION = " *(add|plus|and|with|pos(itive)?) *",
            SUBTRACTION = " *(sub(tract)?|minus|without|neg(ative)?) *",
            DIVISION = " *(div(ided?( *by)?)?|over) *", // this conflicts with the potential behavior of submitting the commmand when the user utters "over"
            MULTIPLICATION = " *(mul(t(ipl(y|ied *by))?)?|times|x|of|by) *",
            EXPONENTIATION = " *(exp(onent)?|(raised? *)?to( *the( *power *of)?)?|pow(er)?|\\*\\*) *", // TTS could confuse 'to' for the number 'two'
            DECIMAL = " *(point|dot) *",
            START_PAREN = " *start *",
            END_PAREN = " *(all|end) *";
    private static final String[]
            MATH_WORDS = {ADDITION, SUBTRACTION, DIVISION, MULTIPLICATION, EXPONENTIATION, DECIMAL, START_PAREN, END_PAREN},
            MATH_SYMBOLS = {"+", "-", "/", "*", "^", ".", "(", ")"};

    private static final String
            EXP = "(\\d+)\\^-?(\\d+)",
            PROD = "(\\d+)[ */]-?(\\d+)",
            SUM = "(-?\\d+)[+-](\\d+)";

    private static long evaluate(CharSequence expr) { // It's trim-safe as a one-time method, but not for text-watching
        StringBuilder solBuilder = new StringBuilder(expr);
        Matcher m = compile(EXP).matcher(solBuilder);
        while (m.find()) {
            int lastStart = m.start(), lastEnd = m.end();
            String lastGroup1 = m.group(1), lastGroup2 = m.group(2);
            while (m.find(m.start(2))) {
                lastStart = m.start();
                lastEnd = m.end();
                lastGroup1 = m.group(1);
                lastGroup2 = m.group(2);
            }

            CharSequence before = solBuilder.subSequence(0, lastStart), after = solBuilder.subSequence(lastEnd, solBuilder.length());
            solBuilder.setLength(0);
            solBuilder.append(before);

            solBuilder.append((int) pow(parseLong(lastGroup1), parseLong(lastGroup2)));
            solBuilder.append(after);

            m.reset(solBuilder);
        }

        m.reset(solBuilder).usePattern(compile(PROD));
        while (m.find()) {
            CharSequence before = solBuilder.subSequence(0, m.start()), after = solBuilder.subSequence(m.end(), solBuilder.length());
            solBuilder.setLength(0);
            solBuilder.append(before);

            if (m.group().contains("*")) solBuilder.append(parseLong(m.group(1)) * parseLong(m.group(2)));
            else solBuilder.append(parseLong(m.group(1)) / parseLong(m.group(2)));
            solBuilder.append(after);

            m.reset(solBuilder);
        }

        m.usePattern(compile(SUM));
        while (m.find()) {
            CharSequence before = solBuilder.subSequence(0, m.start()), after = solBuilder.subSequence(m.end(), solBuilder.length());
            solBuilder.setLength(0);
            solBuilder.append(before);

            if (m.group().contains("+")) solBuilder.append(parseLong(m.group(1)) + parseLong(m.group(2)));
            else solBuilder.append(parseLong(m.group(1)) - parseLong(m.group(2)));
            solBuilder.append(after);

            m.reset(solBuilder);
        }

        return parseLong(solBuilder.toString());
    }

    private static CharSequence cleanup(CharSequence expr) { // TODO parens - "all of" must be parsed before "all"
        int i = 0;
        for (String phrase : MATH_WORDS) {
            Matcher m = compile(phrase, CASE_INSENSITIVE).matcher(expr);
            if (m.find()) expr = m.replaceAll(MATH_SYMBOLS[i]);
            ++i;
        }

        // replace numeral space-juxtaposition with stars - could cause issues with pasted/formmatted numbers: 2 345 692
        // will need to be reworked if you add log_2 34 ability. parse functions beforehand, adding explicit parens?
            // ambiguity: log2 3 - log_2(3) or log(2) * 3?
        Matcher m = compile("(\\d*\\.?\\d+) +(\\d*\\.?\\d+)").matcher(expr);
        boolean hit = false;
        while (m.find()) {
            expr = m.replaceFirst("$1*$2");
            m.reset(expr);
            if (!hit) hit = true;
        }
        if (!hit) m.reset();

        // remove spaces and commas - could cause locale issues with decimal comma: 10,45
        m.usePattern(compile("[ ,]"));
        if (m.find()) {
            expr = m.replaceAll("");
            m.reset(expr);
        } else m.reset();

        // simplify negative positives
        m.usePattern(compile("\\+-|-\\+"));
        if (m.find()) {
            m.usePattern(compile("\\+*-\\+*"));
            expr = m.replaceAll("-");
            m.reset(expr);
        } else m.reset();

        // simplify double negatives
        m.usePattern(compile("--"));
        if (m.find()) {
            expr = m.replaceAll("+");
            m.reset(expr);
        } else m.reset();

        // simplify negative positives again
        m.usePattern(compile("\\+-|-\\+"));
        if (m.find()) {
            m.usePattern(compile("\\+*-\\+*"));
            expr = m.replaceAll("-");
            m.reset(expr);
        } else m.reset();

        // remove unary/double positives
        m.usePattern(compile("(^|[+*/^])\\++"));
        if (m.find()) expr = m.replaceAll("$1");

        return expr;
    }

    public static long compute(String expression) {
        // Todo: ans, floating point math, many more math functions...
        if (expression.matches("[+-]?\\d+")) return parseLong(expression);
    try {
        return evaluate(cleanup(expression));
    } catch (NumberFormatException e) {
        throw new EmlaBadCommandException(R.string.command_calculate, R.string.error_calc_malformed_expression);
    } catch (ArithmeticException e) {
        throw new EmlaBadCommandException(R.string.command_calculate, R.string.error_calc_undefined);
    }}

    private Calculator() {}
}
