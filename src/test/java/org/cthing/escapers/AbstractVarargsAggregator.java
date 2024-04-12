/*
 * Copyright 2024 C Thing Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cthing.escapers;

import java.lang.reflect.Array;
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
    @SuppressWarnings("unchecked")
    public T[] aggregateArguments(final ArgumentsAccessor arguments, final ParameterContext context) {
        return IntStream.range(this.startIndex, arguments.size())
                        .mapToObj(i -> arguments.get(i, this.optionsType))
                        .toArray(length -> (T[])Array.newInstance(this.optionsType, length));
    }
}
