package com.example.examplemod.data;

public record ValueModifier(double amount, Operation operation, Type type) {
    public enum Operation {
        ADD,
        MULTIPLY_TOTAL,
        ;
    }

    public enum Type {
        BENEFICIAL,
        HARMFUL,
        NEUTRAL,
        ;
    }
}
