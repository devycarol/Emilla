package net.emilla.math;

/*internal*/ sealed interface CalcToken permits InfixToken, BitwiseToken {

    final class LParen implements InfixToken, BitwiseToken {

        static final LParen INSTANCE = new LParen();

        private LParen() {}
    }

    final class RParen implements InfixToken, BitwiseToken {

        static final RParen INSTANCE = new RParen();

        private RParen() {}
    }
}
