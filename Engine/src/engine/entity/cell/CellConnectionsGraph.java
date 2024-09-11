package engine.entity.cell;

import engine.entity.range.Range;
import engine.entity.range.RangesManager;
import engine.jaxb.schema.generated.STLCell;
import engine.jaxb.schema.generated.STLCells;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellConnectionsGraph {
    // Graph structure using adjacency list
    private final Map<CellPositionInSheet, List<CellPositionInSheet>> adjList;

    public CellConnectionsGraph(STLCells jaxbCells, RangesManager rangesManager) {
        // Create a graph
        adjList = new LinkedHashMap<>();
        // Regex to extract references
        Pattern refPattern = Pattern.compile("\\{REF,(\\w)(\\d+)}");

        for (STLCell jaxbCell: jaxbCells.getSTLCell()) {
            // Extract row and column
            CellPositionInSheet currentPosition = PositionFactory.createPosition(jaxbCell.getRow(), jaxbCell.getColumn());
            adjList.put(currentPosition, new ArrayList<>());
        }

        // Iterate through the cells
        for (STLCell jaxbCell: jaxbCells.getSTLCell()) {
            // Extract the position of current cell
            CellPositionInSheet currentPosition = PositionFactory.createPosition(jaxbCell.getRow(), jaxbCell.getColumn());
            // Extract the original value
            String originalValue = jaxbCell.getSTLOriginalValue();

            // Check for references and create edges
            Matcher matcher = refPattern.matcher(originalValue);
            while (matcher.find()) {
                String refColumn = matcher.group(1);
                String refRow = matcher.group(2);
                String position = refColumn + refRow;
                CellPositionInSheet refPosition = PositionFactory.createPosition(position);
                addEdge(refPosition, currentPosition);
            }
        }

        addEdgesDependsOnRangeOperation("\\{SUM,\\s*(.*?)\\s*\\}", jaxbCells, rangesManager);
        addEdgesDependsOnRangeOperation("\\{AVERAGE,\\s*(.*?)\\s*\\}", jaxbCells, rangesManager);
    }

    private void addEdgesDependsOnRangeOperation(String operationPattern, STLCells jaxbCells, RangesManager rangesManager) {
        Pattern pattern = Pattern.compile(operationPattern);
        // Iterate through the cells
        for (STLCell jaxbCell: jaxbCells.getSTLCell()) {
            // Extract the position of current cell
            CellPositionInSheet currentPosition = PositionFactory.createPosition(jaxbCell.getRow(), jaxbCell.getColumn());
            // Extract the original value
            String originalValue = jaxbCell.getSTLOriginalValue();
            // check if the original value includes SUM
            Matcher matcher = pattern.matcher(originalValue);
            while (matcher.find()) {
                String extractedString = matcher.group(1);
                Range range = rangesManager.getRangeByName(extractedString);
                if (range != null) {
                    for (CellPositionInSheet cellPositionInSheet: range.getIncludedPositions()) {
                        addEdge(cellPositionInSheet, currentPosition);
                    }
                }
            }
        }
    }

    private void addEdge(CellPositionInSheet from, CellPositionInSheet to) {
        adjList.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
    }

    // Topological Sort using DFS
    public List<CellPositionInSheet> sortTopologically() {
        Set<CellPositionInSheet> visited = new HashSet<>();
        Stack<CellPositionInSheet> stack = new Stack<>();
        for (CellPositionInSheet node : adjList.keySet()) {
            if (!visited.contains(node)) {
                topologicalSortUtil(node, visited, stack);
            }
        }

        // Pop elements from the stack to get the topological order
        List<CellPositionInSheet> sortedOrder = new ArrayList<>();
        while (!stack.isEmpty()) {
            sortedOrder.add(stack.pop());
        }
        return sortedOrder;
    }

    // DFS helper function for topological sort
    private void topologicalSortUtil(CellPositionInSheet node, Set<CellPositionInSheet> visited, Stack<CellPositionInSheet> stack) {
        visited.add(node);
        if (adjList.containsKey(node)) {
            for (CellPositionInSheet neighbor : adjList.get(node)) {
                if (!visited.contains(neighbor)) {
                    topologicalSortUtil(neighbor, visited, stack);
                }
            }
        }
        stack.push(node);  // Push the node to the stack after visiting all its neighbors
    }
}