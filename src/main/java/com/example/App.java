package com.example;

import lombok.extern.slf4j.Slf4j;

/**
 * Main: читає дерево, запускає пошук MIS, друкує:
 *  - розмір MIS
 *  - список вершин у побудованому наборі S (для перевірки)
 * <p>
 * Формат вводу
 *   N
 *   u1 v1
 *   ...
 *   u_{N-1} v_{N-1}
 *   [root]   (опційно, якщо є)
 * <p>
 * Якщо root не задано — беремо root=1.
 */
@Slf4j
public class App {

    public static void main(String[] args) throws Exception {
        SuperFastScanner fs = new SuperFastScanner(System.in);

        int n = fs.nextInt();
        Tree tree = new Tree(n);

        for (int i = 0; i < n - 1; i++) {
            int u = fs.nextInt();
            int v = fs.nextInt();
            tree.addEdge(u, v);
        }

        int root = 1;
        Integer maybeRoot = fs.tryNextInt();
        if (maybeRoot != null) {
            root = maybeRoot;
        }

        log.info("Вхід: N={}, root={}", n, root);

        MISFinder finder = new MISFinder(tree, root);
        MISResult res = finder.solve();

        System.out.println("MIS_size=" + res.getMaxSize());
        System.out.println("MIS_nodes=" + res.getSet());
    }
}
