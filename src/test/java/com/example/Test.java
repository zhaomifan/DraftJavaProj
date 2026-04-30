package com.example;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Integer> vs = new ArrayList<>();
        vs.add(null);
        vs.add(0);
        vs.add(-1);
        vs.add(1);
        vs.add(19929);
        List<String> values1= new ArrayList<>();
        List<String> values2 = new ArrayList<>();
        vs.forEach(i -> values1.add(String.valueOf(i)));
        vs.forEach(i -> values2.add(i + ""));
        System.out.println(values1);
        System.out.println(values2);
    }
}
