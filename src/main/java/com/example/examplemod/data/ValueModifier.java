package com.example.examplemod.data;

public record ValueModifier(double amount, Operation operation) {
    public enum Operation{
        ADD,
        MULTIPLY_TOTAL,
        ;
    }
}
