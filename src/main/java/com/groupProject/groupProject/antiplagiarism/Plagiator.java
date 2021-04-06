package com.groupProject.groupProject.antiplagiarism;

import java.util.ArrayList;
import java.util.List;

public class Plagiator {
    int MIN_LCS_LENGTH = 17; //мінімальна довжина однакової ділянки коду, яка свідчить про плагіат
    public double longestCommonSubstringTest(String test, String other){
        int originalLength = test.length();//початкова довжина рядка
        int lcsLength;//довжина найдовшого спільного рядка (НСР)
        do//повторюємо дії, поки НСР не буде закоротким
        {
            int n = test.length();
            int m = other.length();
            int[][] matr = new int[n][m];
            lcsLength = 0;
            int maxI = 0;
            for (int i = 0; i < n; i++)//заповнює матрицю пошуку НСР
            {
                for (int j = 0; j < m; j++)
                {
                    if (test.charAt(i) == other.charAt(j))
                    {
                        matr[i][j] = (i == 0 || j == 0) ? 1 : matr[i - 1][j - 1] + 1;
                        if (matr[i][j] > lcsLength)
                        {
                            lcsLength = matr[i][j];
                            maxI = i;
                        }
                    }
                }
            }

            if (lcsLength > 0)//якщо НСР знайдено
            {
                String lcs1 = test.substring(maxI + 1 - lcsLength, maxI + 1);//знаходимо цей НСР
                test = test.replace(lcs1, "");//видаляємо з рядка, який перевіряємо, всі входження НСР
                String lcs2 = other.substring(other.indexOf(lcs1), other.indexOf(lcs1)+lcsLength);
                other = replaceFirst2(other , lcs2 , "");//із рядка, з яким перевіряємо, видаляємо лише перше входження НСР
            }
        }
        while (lcsLength >= MIN_LCS_LENGTH);//повторюємо дії, поки НСР не буде закоротким

        //коефіцієнт плагіату знаходиться як відношення довжини унікальної частини тексту до всієї довжини рядка
        return 1.0 - (double)test.length() / originalLength;
    }
    public double WShinglingTest(String test, String other)//метод шинглів
    {
        //шукаємо шингли рядка test та відразу рахуємо їхній хеш
        int testCountShingles = test.length() - MIN_LCS_LENGTH + 1;
        List<Integer> testShingles = new ArrayList<Integer>();
        for (int i = 0; i < testCountShingles; ++i)
        {
            testShingles.add(test.substring(i, i+ MIN_LCS_LENGTH).hashCode());
        }

        //шукаємо шингли рядка other та відразу рахуємо їхній хеш
        int otherCountShingles = other.length() - MIN_LCS_LENGTH + 1;
        List<Integer> otherShingles = new ArrayList<Integer>();
        for (int i = 0; i < otherCountShingles; ++i)
        {
            otherShingles.add(other.substring(i, i+ MIN_LCS_LENGTH).hashCode());
        }
        long count = intersection(testShingles , otherShingles).size();
        //коефіцієнт плагіату знаходиться як відношення кількості однакових хешів до кількості всіх хешів рядка test
        return (double)count/ testShingles.stream().distinct().count();
    }
    public double AveragePlagiatTest(String test, String other)//середнє арифметичне обох методів
    {
        return (longestCommonSubstringTest(test, other) + WShinglingTest(test, other)) / 2;
    }
    private static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();
        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }
    private static String replaceFirst2(String source, String target, String replacement) {
        int index = source.indexOf(target);
        if (index == -1) {
            return source;
        }

        return source.substring(0, index)
                .concat(replacement)
                .concat(source.substring(index+target.length()));
    }
}
