package com.alejandro.gestordenotas.data;

import java.util.List;

import org.mockito.ArgumentMatcher;


public class CustomCondition implements ArgumentMatcher<Long> {

    private Long argument;
    private final List<Long> idsValid;
    private final boolean matchIfInSet;

    public CustomCondition(List<Long> idsValid, boolean matchIfInSet) {
        this.idsValid = idsValid;
        this.matchIfInSet = matchIfInSet;
    }

    // To create a custom condition
    @Override
    public boolean matches(Long argument) {
        this.argument = argument; // init the argument
        // Check if the id is valid or not
        return matchIfInSet ? idsValid.contains(argument) : !idsValid.contains(argument);
    }

    // To create and show a custom message when the condition is not completed
    @Override
    public String toString() {
        return matchIfInSet 
            ? "You used the value: " + argument + " but You must use an existing id" 
            : "You used the value: " + argument + " but You must use an inexisting id";
    }

}
