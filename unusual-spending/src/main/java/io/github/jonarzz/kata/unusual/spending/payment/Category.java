package io.github.jonarzz.kata.unusual.spending.payment;

public class Category {

    private String name;

    private Category(String name) {
        this.name = name;
    }

    public static Category named(String name) {
        return new Category(name.toLowerCase());
    }

    @Override
    public String toString() {
        return name;
    }
}
