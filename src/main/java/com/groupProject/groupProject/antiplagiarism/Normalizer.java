package com.groupProject.groupProject.antiplagiarism;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Normalizer {
    static String separator = "";
    static List<String> punctuator = new ArrayList<>();
    static List<String> keyWord = new ArrayList<>();
    static List<String> numbers = new ArrayList<>();
    static Map<String , String> keyWordMap = new HashMap<>();

    public Normalizer(){
        grammarReader("token.txt");
    }

    public String getNormalizedCode(String code){
        code = removeComments(code);
        code = removeText(code);
        findKeyWords(code);
        fillDictionary();
        return normalizeCode(code);
    }
    private String normalizeCode(String code){
        for(String s : keyWord){
            if(s.length()>1){
                code = code.replaceAll("\\b" +s + "\\b", separator + keyWordMap.get(s));
            }
            else
            {
                if (keyWordMap.containsValue(s))
                {
                    code = code.replaceAll(separator + s, separator + separator);
                    code = code.replaceAll(s, separator + keyWordMap.get(s));
                    code = code.replaceAll(separator + separator, separator + s);
                }
                else
                {
                    code = code.replace(s, separator + keyWordMap.get(s));
                }
            }
        }
        for(String s : numbers)
        code = code.replace(s, "#number#");
        code = code.replace("#number#.#number#", "#number#");
        code = code.replace("#number#,#number#", "#number#");
        code = code.replace("#number#", separator +  keyWordMap.get("int"));
        code = code.replace(separator, "");

        //видалення відступів
        code = code.replace(" ", "");
        code = code.replace("\n", "");
        code = code.replace("\r", "");
        return code;
    }
    private static void fillDictionary()//заповнює словник токенів
    {
        boolean allKeyworsAdded = true;
        do
        {
            for (int i = 1; i < keyWord.size(); ++i)
            {
                if (!keyWordMap.containsKey(keyWord.get(i)))
                {
                    if (keyWordMap.containsKey(keyWord.get(i-1)))
                    {
                        keyWordMap.put(keyWord.get(i), keyWordMap.get(keyWord.get(i-1)));
                    }
                    else
                    {
                        if (keyWordMap.containsKey(keyWord.get(i+1)))
                        {
                            keyWordMap.put(keyWord.get(i), keyWordMap.get(keyWord.get(i+1)));
                        }
                        else
                        {
                            allKeyworsAdded = false;
                        }
                    }
                }
            }
        }
        while (!allKeyworsAdded);
    }
    public static void grammarReader(String fileName){
        try(FileReader reader = new FileReader(fileName))
        {
            Scanner sc = new Scanner(reader);
            separator = sc.nextLine();
          while(sc.hasNext()){
              String line = sc.nextLine();
              if(line.equals(separator)){
                  break;
              }
              punctuator.add(line);
          }
          while(sc.hasNext()){
              String line = sc.nextLine();
              if(line.equals(separator)){
                  break;
              }
              String[] separateLine = line.split(separator);
              String[] keys = separateLine[0].split(" ");
              if (separateLine.length > 1){
                 for( String s : keys){
                     keyWordMap.put(s,separateLine[1]);
                  }
              }

          }
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }

    private static void findKeyWords(String code){
        code = code.replace("\n" , " ");
        code = code.replace("\r" , " ");
        for(String s : punctuator){
            code =   code.replace(s , " ");
        }
        String[] splitCode = code.split(" ");
        for(String s : splitCode){
            if(s.length()>0 && !keyWord.contains(s)){
                if (!isNumeric(s))
                {
                    keyWord.add(s);
                }
                else{
                    if(!numbers.contains(s))
                        numbers.add(s);
                }
            }
        }
    }
    private static String removeComments(String code){
        while (code.contains("//")){
            int i = code.indexOf("//");
            String buf1 = code.substring(0, i);
            String buf2 = code.substring(i + 2 );
            i = buf2.indexOf("\n");
            if (i != -1) buf2 = buf2.substring(i);
            code = buf1 + buf2;
        }
        while (code.contains("/*")){
            int i = code.indexOf("/*");
            String buf1 = code.substring(0, i);
            String buf2 = code.substring(i + 2 );
            i = buf2.indexOf("*/");
            if (i != -1) buf2 = buf2.substring(i);
            code = buf1 + buf2;
        }
        return code;

    }
    private static String removeText(String code){
        code = code.replace("\\\"", " ");
        while (code.contains("\"")){
            int i = code.indexOf("\"");
            String buf1 = code.substring(0, i);
            String buf2 = code.substring(i + 1);
            i = buf2.indexOf("\"");
            if (i != -1) buf2 = buf2.substring(i+1);
            code = buf1 + buf2;
        }
        code = code.replace("\\\"", " ");
        while (code.contains("\'")){
            int i = code.indexOf("\'");
            String buf1 = code.substring(0, i);
            String buf2 = code.substring(i + 1);
            i = buf2.indexOf("\'");
            if (i != -1) buf2 = buf2.substring(i);
            code = buf1 + buf2;
        }
        return code;

    }
    private static boolean isNumeric(String str){
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
