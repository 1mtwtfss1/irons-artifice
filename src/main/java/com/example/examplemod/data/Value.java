package com.example.examplemod.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

public record Value(double base, Multimap<ValueModifier.Operation, ValueModifier> modifiersByOperation) {
    public Collection<ValueModifier> modifiers() {
        return modifiersByOperation.values();
    }

    public static Value of(double base) {
        return new Value(base, ArrayListMultimap.create());
    }

    public void addModifier(ValueModifier modifier) {
        this.modifiersByOperation.put(modifier.operation(), modifier);
    }

    public double compute() {
        double sum = base + modifiersByOperation.get(ValueModifier.Operation.ADD).stream().mapToDouble(ValueModifier::amount).sum();
        for (ValueModifier modifier : modifiersByOperation.get(ValueModifier.Operation.MULTIPLY_TOTAL)) {
            sum *= 1 + modifier.amount();
        }
        return sum;
    }
}
