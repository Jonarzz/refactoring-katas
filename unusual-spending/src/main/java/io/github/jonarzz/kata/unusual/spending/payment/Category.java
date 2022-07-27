package io.github.jonarzz.kata.unusual.spending.payment;

public class Category {

    private String name;

    private Category(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be blank");
        }
        this.name = name.toLowerCase();
    }

    public static Category named(String name) {
        return new Category(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
