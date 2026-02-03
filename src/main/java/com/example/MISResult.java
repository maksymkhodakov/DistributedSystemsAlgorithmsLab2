package com.example;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Результат:
 *  - maxSize: максимальна кількість вершин у незалежному наборі (MIS)
 *  - set: конкретний побудований набір вершин S (один з можливих оптимальних)
 */
@Getter
@AllArgsConstructor
public class MISResult {
    private final int maxSize;
    private final List<Integer> set;
}
