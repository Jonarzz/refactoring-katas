package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

class ThresholdMatcherCreator {

    private Cost comparedCost;

    private ThresholdMatcherCreator(Cost comparedCost) {
        this.comparedCost = comparedCost;
    }

    static ThresholdMatcherCreator expense(Cost cost) {
        return new ThresholdMatcherCreator(cost);
    }

    ThresholdMatcher comparedTo(Cost base) {
        return new ThresholdMatcher(base);
    }

    class ThresholdMatcher {

        private Cost baseCost;

        private ThresholdMatcher(Cost baseCost) {
            this.baseCost = baseCost;
        }

        boolean satisfiesThreshold(SpendingThreshold threshold) {
            if (baseCost == null) {
                return false;
            }
            if (!baseCost.getCurrency().equals(comparedCost.getCurrency())) {
                return false;
            }
            return threshold.thresholdReached(baseCost.getAmount(), comparedCost.getAmount());
        }

    }
}
