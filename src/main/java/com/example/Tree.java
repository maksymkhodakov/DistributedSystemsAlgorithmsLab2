package com.example;
import java.util.ArrayList;
import java.util.List;

/**
 * Дерево (неорієнтований граф без циклів) у вигляді списків суміжності.
 * Вершини нумеруються від 1 до N.
 * Для MIS нам достатньо:
 *  - мати сусідів кожної вершини
 *  - під час DFS "вкорінити" дерево, тобто визначити parent і children
 */
public final class Tree {
    private final int n;
    private final List<List<Integer>> adj;

    public Tree(int n) {
        this.n = n;
        this.adj = new ArrayList<>(n + 1);
        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public int size() {
        return n;
    }

    public void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    public List<Integer> neighbors(int u) {
        return adj.get(u);
    }
}

