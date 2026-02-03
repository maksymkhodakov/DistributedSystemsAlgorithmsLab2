package com.example;

import java.io.*;
import java.util.StringTokenizer;

/**
 * Мінімальний сканер для швидкого читання.
 */
public class SuperFastScanner {
    private final BufferedReader br;
    private StringTokenizer st;

    SuperFastScanner(InputStream in) {
        br = new BufferedReader(new InputStreamReader(in));
    }

    public String next() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            String line = br.readLine();
            if (line == null) return null;
            st = new StringTokenizer(line);
        }
        return st.nextToken();
    }

    public int nextInt() throws IOException {
        String s = next();
        if (s == null) {
            throw new EOFException("Неочікуваний кінець вводу");
        }
        return Integer.parseInt(s);
    }

    public Integer tryNextInt() throws IOException {
        String s = next();
        if (s == null) {
            return null;
        }
        return Integer.parseInt(s);
    }
}
