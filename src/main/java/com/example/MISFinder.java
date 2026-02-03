package com.example;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Пошук Maximum Independent Set (MIS) у дереві за O(|V|+|E|).
 * MIS: Незалежний набір вершин S — це такий набір, що між будь-якими двома вершинами з S
 * НЕ існує ребра (тобто сусідні вершини не можуть одночасно входити в S).
 * MIS (Maximum Independent Set) — незалежний набір максимальної потужності.
 * <p>
 * === ФОРМУЛА ЗІ СЛАЙДІВ ЛЕКЦІЇ ===
 * На слайді:
 * <p>
 * I[u] = max(
 *          1 + Σ I[w]   по w = grandchild(u),
 *          Σ I[w]       по w = child(u)
 *       )
 * <p>
 * Пояснення:
 *  - Якщо беремо u в S, то дітей u брати НЕ можна, отже можна брати онуків => 1 + сума по онуках
 *  - Якщо не беремо u, то можемо брати дітей => сума по дітях
 * <p>
 * === ДЕТАЛІ РЕАЛІЗАЦІЇ ===
 * Прямо рахувати "онуків" незручно. Стандартна і еквівалентна форма DP:
 * <p>
 * include[u] = 1 + Σ exclude[child(u)]
 * exclude[u] =     Σ max(include[child(u)], exclude[child(u)])
 * <p>
 * Тут:
 *  - include[u] — оптимальна кількість у піддереві u, якщо u ВХОДИТЬ до MIS
 *  - exclude[u] — оптимальна кількість у піддереві u, якщо u НЕ входить до MIS
 * <p>
 * Чому include[u] еквівалентний "1 + сума по онуках"?
 *  - Якщо u взяли, то child брати не можна.
 *  - Але в піддереві кожної дитини child оптимум при умові "child НЕ взяли" — це exclude[child].
 *  - exclude[child] якраз і містить вибір по ВНУТРІШНІХ вершинах піддерева, зокрема по онуках u.
 * <p>
 * Тобто:
 *    1 + Σ exclude[child]  ==  1 + Σ I[grandchild]
 * (просто "онуки" враховані неявно через exclude-стан)
 * <p>
 * Остаточна відповідь:
 *    I[u] = max(include[u], exclude[u])
 * <p>
 * === ВІДНОВЛЕННЯ НАБОРУ S ===
 * Після DP ми будуємо сам набір:
 *  - Якщо беремо u (include), то дітей беремо в exclude-режимі (тобто НЕ беремо їх)
 *  - Якщо u не беремо (exclude), то для кожної дитини беремо кращий з include/exclude
 */
@Slf4j
public class MISFinder {

    private final Tree tree;
    private final int root;

    // DP-масиви за означенням вище
    private int[] include;
    private int[] exclude;

    public MISFinder(Tree tree, int root) {
        this.tree = tree;
        this.root = root;
    }

    public MISResult solve() {
        int n = tree.size();
        include = new int[n + 1];
        exclude = new int[n + 1];

        log.info("Початок пошуку MIS у дереві. Корінь (root) = {}", root);

        // 1) DP-обчислення (DFS знизу-вгору)
        dfsDp(root, 0);

        int best = Math.max(include[root], exclude[root]);
        log.info("DP завершено: include[root]={}, exclude[root]={}, I[root]={}", include[root], exclude[root], best);

        // 2) Відновлення набору S (один з оптимальних)
        boolean takeRoot = include[root] >= exclude[root];
        List<Integer> set = new ArrayList<>();
        restore(root, 0, takeRoot, set);

        Collections.sort(set);

        log.info("Набір S відновлено. |S| = {}", set.size());
        log.info("S = {}", set);

        return new MISResult(best, set);
    }

    /**
     * DFS для DP:
     * Рахує include[u] та exclude[u] через значення дітей.
     * <p>
     * Важливо: дерево неорієнтоване, тому для "дітей" ми вважаємо сусідів, крім parent.
     */
    private void dfsDp(int u, int parent) {
        // Базові значення:
        // Якщо беремо u => мінімум 1 (сама вершина)
        include[u] = 1;
        // Якщо не беремо u => мінімум 0
        exclude[u] = 0;

        log.info("DFS u={} (parent={})", u, parent);

        for (int v : tree.neighbors(u)) {
            if (v == parent) continue;

            // Спочатку рахуємо DP для піддерева дитини v
            dfsDp(v, u);

            // === Реалізація формул ===
            // 1) include[u] = 1 + Σ exclude[v]
            //    Бо якщо u в MIS, то v (дитину) брати не можна, а оптимум в її піддереві при v NOT chosen — exclude[v]
            include[u] += exclude[v];

            // 2) exclude[u] = Σ max(include[v], exclude[v])
            //    Бо якщо u НЕ в MIS, то v можна або взяти, або не взяти — обираємо максимум
            exclude[u] += Math.max(include[v], exclude[v]);
        }

        log.info("DP[u={}]: include={} (беремо u), exclude={} (не беремо u), I[u]={}",
                u, include[u], exclude[u], Math.max(include[u], exclude[u]));
    }

    /**
     * Відновлення (конструкція) множини S за вже порахованими include/exclude.
     *
     * @param takeU true якщо в цій гілці ми вирішили включити u в S
     */
    private void restore(final int u,
                         final int parent,
                         final boolean takeU,
                         final List<Integer> set) {
        if (takeU) {
            // Якщо вершина u у множині S — додаємо
            set.add(u);
            log.debug("Включаємо u={} у S => дітей брати НЕ можна.", u);
        } else {
            log.debug("НЕ включаємо u={} у S => по дітях беремо оптимум.", u);
        }

        for (int v : tree.neighbors(u)) {
            if (v == parent) {
                continue;
            }

            if (takeU) {
                // Якщо u взяли, то дитину v брати заборонено => викликаємо restore для v з takeV=false
                restore(v, u, false, set);
            } else {
                // Якщо u не взяли, то по дитині v обираємо кращий стан:
                // v береться, якщо include[v] >= exclude[v]
                boolean takeV = include[v] >= exclude[v];
                restore(v, u, takeV, set);
            }
        }
    }
}
