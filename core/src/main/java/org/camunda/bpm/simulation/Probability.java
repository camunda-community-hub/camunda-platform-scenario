package org.camunda.bpm.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Probability<O> {

    private List<Value<O>> values = new ArrayList<Value<O>>();

    public static <O> Probability<O> select(O... v) {
        return new Probability<O>(v);
    }

    public O withProbabilities(int... p) {
        if (p.length != this.values.size())
            throw new IllegalStateException("You must provide one value per probability you provided!");
        int all = 0;
        for (int i = 0; i < p.length; i++) {
            all += p[i];
            values.get(i).percentage = p[i];
        }
        if (all != 100)
            throw new IllegalStateException("Percentages must add up to 100!");
        return get();
    }

    private Probability(O... v) {
        for (O value: v) {
            values.add(new Value<O>(value));
        }
    }

    private class Value<V> {

        int percentage;
        V value;

        Value(V value) {
            this.value = value;
        }

    }

    private O get() {
        int random = new Random().nextInt(100);
        int all = 0;
        for (Value<O> value: values) {
            all += value.percentage;
            if (all >= random)
                return value.value;
        }
        throw new IllegalStateException();
    }

}
