package org.camunda.bpm.scenario.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Probability {

    private List<Value> values = new ArrayList<>();

    private class Value {

        int percentage;
        Object value;

        Value(int percentage) {
            this.percentage = percentage;
        }

    }

    public Probability(int... p) {
        int all = 0;
        for (int i = 0; i< p.length; i++) {
            all += p[i];
            values.add(new Value(p[i]));
        }
        if (all != 100)
            throw new IllegalStateException("Percentages must add up to 100!");
    }

    public <O> O values(O... v) {
        if (v.length != this.values.size())
            throw new IllegalStateException("You must provide one value per probability you provided!");
        for (int i = 0; i< v.length; i++) {
            this.values.get(i).value = v[i];
        }
        return (O) get();
    }

    private  Object get() {
        int random = new Random().nextInt(100);
        int all = 0;
        for (Value value: values) {
            all += value.percentage;
            if (all >= random)
                return value.value;
        }
        throw new IllegalStateException();
    }

}
