package io.github.jonarzz.kata.banking.account.statement;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

abstract class Row {

    private static final String COLUMN_SEPARATOR = "  ";

    private String firstCell;
    private String[] otherCells;

    protected Row(String firstCell, String... otherCells) {
        this.firstCell = firstCell;
        this.otherCells = otherCells;
    }

    String formatted(int... maxCellLengths) {
        var lengthsCount = maxCellLengths.length;
        var cellsCount = 1 + otherCells.length;
        if (lengthsCount != cellsCount) {
            throw new IllegalStateException("Number of provided max cell lengths: " + lengthsCount
                                            + " should be equal to number of cells: " + cellsCount);
        }
        List<String> cells = new ArrayList<>();
        cells.add(alignedLeft(firstCell, maxCellLengths[0]));
        for (int i = 0; i < otherCells.length; i++) {
            cells.add(alignedRight(otherCells[i], maxCellLengths[i + 1]));
        }
        return String.join(COLUMN_SEPARATOR, cells);
    }

    private String alignedLeft(String cell, int alignSize) {
        return cell + nSpaces(alignSize - cell.length());
    }

    private String alignedRight(String cell, int alignSize) {
        return nSpaces(alignSize - cell.length()) + cell;
    }

    private static String nSpaces(int n) {
        return IntStream.range(0, n)
                        .mapToObj(i -> " ")
                        .collect(joining());
    }

}
