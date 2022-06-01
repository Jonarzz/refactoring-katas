package io.github.jonarzz.kata.unusual.spending.payment;

public class GroupingPolicies {

    private GroupingPolicies() {
    }

    public static GroupingPolicy<Category> category() {
        return Payment::category;
    }

}
