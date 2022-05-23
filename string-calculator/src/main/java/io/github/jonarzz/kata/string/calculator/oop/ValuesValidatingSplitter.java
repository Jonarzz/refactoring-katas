package io.github.jonarzz.kata.string.calculator.oop;

import static java.lang.String.join;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

class ValuesValidatingSplitter<T> implements ValuesSplitter<T> {

    private Function<String, T> validValueTransformer;
    private Collection<ThrowingValueValidator> eagerValidators;
    private Collection<ValueAcceptancePolicy> lazyAcceptancePolicies;

    ValuesValidatingSplitter(Function<String, T> validValueTransformer,
                             Collection<ThrowingValueValidator> eagerValidators,
                             Collection<ValueAcceptancePolicy> lazyAcceptancePolicies) {
        this.validValueTransformer = validValueTransformer;
        this.eagerValidators = eagerValidators;
        this.lazyAcceptancePolicies = lazyAcceptancePolicies;
    }

    @Override
    public final List<T> split(String separatedValues, Delimiter delimiter) {
        if (separatedValues.isEmpty()) {
            return List.of();
        }
        List<T> list = new ArrayList<>();
        Multimap<ValueAcceptancePolicy, String> invalidValuesByValidator = ArrayListMultimap.create();
        for (var value : delimiter.split(separatedValues)) {
            for (var validator : eagerValidators) {
                validator.validate(value);
            }
            for (var policy : lazyAcceptancePolicies) {
                if (policy.isInvalid(value)) {
                    invalidValuesByValidator.put(policy, value);
                }
            }
            list.add(validValueTransformer.apply(value));
        }
        invalidValuesByValidator.asMap()
                                .forEach((policy, invalidValues) -> {
                                    throw new IllegalArgumentException(policy.formatErrorMessage(join(", ", invalidValues)));
                                });
        return list;
    }

}
