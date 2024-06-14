/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;


public class AbstractVarargsAggregator<T> implements ArgumentsAggregator {

    private final Class<T> optionsType;
    private final int startIndex;

    protected AbstractVarargsAggregator(final Class<T> optionsType, final int startIndex) {
        this.optionsType = optionsType;
        this.startIndex = startIndex;
    }

    @Override
    public Set<T> aggregateArguments(final ArgumentsAccessor arguments, final ParameterContext context) {
        return IntStream.range(this.startIndex, arguments.size())
                        .mapToObj(i -> arguments.get(i, this.optionsType))
                        .collect(Collectors.toSet());
    }
}
