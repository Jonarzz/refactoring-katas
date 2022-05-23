package io.github.jonarzz.kata.string.calculator.oop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

class ValuesValidatingSplitter<T> implements ValuesSplitter<T> {

    private Function<String, T> validValueTransformer;
    private Collection<EagerValidator> eagerValidators;
    private Collection<Supplier<LazyValidator>> lazyValidatorSuppliers;

    ValuesValidatingSplitter(Function<String, T> validValueTransformer,
                             Collection<EagerValidator> eagerValidators,
                             Collection<Supplier<LazyValidator>> lazyValidatorSuppliers) {
        this.validValueTransformer = validValueTransformer;
        this.eagerValidators = eagerValidators;
        this.lazyValidatorSuppliers = lazyValidatorSuppliers;
    }

    @Override
    public final List<T> split(String separatedValues, Delimiter delimiter) {
        if (separatedValues.isEmpty()) {
            return List.of();
        }
        List<T> list = new ArrayList<>();
        var lazyValidators = lazyValidatorSuppliers.stream()
                                                   .map(Supplier::get)
                                                   .toList();
        for (var value : delimiter.split(separatedValues)) {
            for (var eagerly : eagerValidators) {
                eagerly.validate(value);
            }
            var valid = true;
            for (var lazily : lazyValidators) {
                valid &= lazily.validate(value);
            }
            if (valid) {
                list.add(validValueTransformer.apply(value));
            }
        }
        lazyValidators.forEach(LazyValidator::throwValidationError);
        return list;
    }

}
