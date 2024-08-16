package engine.impl;

import engine.entity.cell.Cell;
import engine.entity.cell.CellType;
import engine.entity.cell.EffectiveValue;

import java.util.Set;

public class CellCycleDetector {

    private static boolean detectCycle(Cell cell, Set<Cell> visited) {
        if (visited.contains(cell)) {
            return true; // Cycle detected
        }
        visited.add(cell);

        for (Cell nextCell : cell.getInfluences()) {
            if (detectCycle(nextCell, visited)) {
                return true;
            }
        }

        visited.remove(cell); // Remove from visited for other DFS paths
        return false;
    }

    public static void main(String[] args) {
        // Example usage
        Cell a = new Cell("a", new EffectiveValue(CellType.STRING, "a"), 1);
        Cell b = new Cell("b", new EffectiveValue(CellType.STRING, "b"), 1);
        Cell c = new Cell("c", new EffectiveValue(CellType.STRING, "c"), 1);
        Cell d = new Cell("d", new EffectiveValue(CellType.STRING, "d"), 1);
        Cell e = new Cell("e", new EffectiveValue(CellType.STRING, "e"), 1);
        Cell f = new Cell("f", new EffectiveValue(CellType.STRING, "f"), 1);

        // Add other cells and their influences/influencedBy
        f.addConnectionTo(a);
        a.addConnectionTo(b);
        a.addConnectionTo(c);
        a.addConnectionTo(d);
        e.addConnectionTo(a);

        boolean result = f.addConnectionTo(d);
        System.out.println("Does the connection create a cycle? " + result);
    }
}