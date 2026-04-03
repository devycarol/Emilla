package net.emilla.exception;

public final class UnreachableError extends Error {
    public UnreachableError() {
        super("This should never happen");
    }
}
