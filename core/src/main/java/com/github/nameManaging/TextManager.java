package com.github.nameManaging;

import java.util.ArrayList;
import com.github.nameManaging.AlphabetManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.StringBuilder;
//holy moly, I really need to find a better way to integrate tokens and stuff.
public class TextManager {
    static StringBuilder showUser = new StringBuilder();
    static StringBuilder unformatted = new StringBuilder(); // AKA ugly ass strings
    static int prevLen;
    public static String update(char toAdd){
        showUser.append(toAdd);
        unformatted.append(toAdd);
        return updateColors(prevLen);
    }
    public static String update(String var){
        //showUser.append("[#7f78aa]");
        prevLen = showUser.length();
        if (unformatted.length > 0 && AlphabetManager.isNumber(unformatted.charAt(unformatted.length() - 1))){
            unformatted.append("*");
            showUser.append("*");
        }
        showUser.append(var);
        //showUser.append("[]");
        unformatted.append(var);
        return updateColors(prevLen);
    }
    public static String update(CharSequence toAdd){
        prevLen = showUser.length();
        showUser.append(toAdd);
        unformatted.append(toAdd);
        return updateColors(prevLen);
    }
    public static String addFunc(String function){
        //showUser.append("[#d0a343]");
        prevLen = showUser.length();
        if (AlphabetManager.isNumber(unformatted.charAt(unformatted.length() - 1))){
            unformatted.append("*");
            showUser.append("*");
        }
        showUser.append(function);
        unformatted.append(function);
        return updateColors(prevLen);
    }
    public static String addFuncVar(int i, ArrayList<String> funcVars){
        prevLen = showUser.length();
        showUser.append("°" + funcVars.get(i));
        unformatted.append("<"+i+">");
        return updateColors(prevLen);
    }
    public static String removeFuncVar(int index, ArrayList<String> funcVars){
        boolean hasVar = false;
        int prev;
        int varId;
        for (int i = 0; i < unformatted.length; index++){
            if (unformatted.charAt(i) == '<' && AlphabetManager.isNumber(unformatted.charAt(i+1))){
                prev = i;
                while (unformatted.charAt(i) != '>' && i < unformatted.length) {
                    i++;
                }
                varId = Integer.parseInt(unformatted.substring(prev, i));
                if (varId > index){
                    unformatted.replace(prev, i, "<" + (varId - 1) + ">");
                } else if (Integer.parseInt(unformatted.substring(prev, i)) == index){
                    unformatted.delete(prev, i);
                    hasVar = true;
                }
            }
        }
        if (hasVar){
            String toDelete = funcVars.get(index);
            prev = showUser.indexOf(toDelete, 0);
            varId = 0;
            while (prev != -1) {
                if (showUser.charAt(prev - 1) != ']'){
                    showUser.delete(prev, prev + toDelete.length());
                }
                prev = showUser.indexOf(toDelete, prev);
            }
        }
    return showUser.toString();
    }
    public static String erase(){
        if (unformatted.length == 0) return "";
        int i = unformatted.length - 1;

        if (AlphabetManager.isValidAlphabetChar(unformatted.charAt(i))) {

        } else if (unformatted.charAt(i) == '>'){
            int offset = 0;
            while (AlphabetManager.isNumber(unformatted.charAt(i - offset))){
                offset++;
            }
            if (unformatted.charAt(i - offset) == '<') {
                i -= offset;
            }
        }
        while ((AlphabetManager.isValidAlphabetChar(unformatted.charAt(i))) && i > 0){
            i--;
        }
        int delShowUser = unformatted.length() - i;
        unformatted.delete(i, unformatted.length());
        if (showUser.charAt(showUser.length() - 1) == ']') showUser.delete(showUser.length() - 2, showUser.length());
        showUser.delete(showUser.length() - delShowUser, showUser.length());
        i = showUser.length() - 1;
        if (showUser.length > 0 && showUser.charAt(showUser.length() - 1) == ']' && showUser.charAt(showUser.length() - 2) != '['){
            showUser.delete(showUser.lastIndexOf("[", i), showUser.length());
        }
        return showUser.toString();
    }
    public static void clear(){
        showUser.clear();
        unformatted.clear();
    }
    public static String getUnformatted(){
        showUser.clear();
        return unformatted.toStringAndClear();
    }
    public static String updateColors(int start){
        int prev = start;
        for (int i = start; i < showUser.length; i++){
            if (AlphabetManager.isValidAlphabetChar(showUser.charAt(i))){
                prev = i;
                while (AlphabetManager.isValidAlphabetChar(showUser.charAt(i))) i++;
                if (i < showUser.length && showUser.charAt(i) == '<'){
                    showUser.insert(prev, ColorStyle.functionColor);
                    i += 9;
                    showUser.insert(i, "[]");
                    i += 2;
                    showUser.insert(i, ColorStyle.bracketColor);
                    i += 10;
                    showUser.insert(i, "[]");
                    i += 1;
                } else {
                    showUser.insert(prev, ColorStyle.globalVarColor);
                    i += 9;
                    showUser.insert(i, "[]");
                    i += 1;
                }
            } else if (showUser.charAt(i) == '>'){
                showUser.insert(i, ColorStyle.bracketColor);
                i += 10;
                showUser.insert(i, "[]");
                i += 1;
            } else if (showUser.charAt(i) == '°'){
                showUser.insert(i, ColorStyle.intervalVarColor);
                i += 10;
                while ( i < showUser.length && AlphabetManager.isValidAlphabetChar(showUser.charAt(i))) i++;
                showUser.insert(i, "[]");
                i += 1;

            }
        }
        return ColorStyle.defaultColor + showUser.toString();
    }
    public static String updateColors(int start, StringBuilder builder){
        int prev = start;
        for (int i = start; i < builder.length; i++){
            if (AlphabetManager.isValidAlphabetChar(builder.charAt(i))){
                prev = i;
                while (i < builder.length && AlphabetManager.isValidAlphabetChar(builder.charAt(i))) i++;
                if (i < builder.length && builder.charAt(i) == '<'){
                    builder.insert(prev, ColorStyle.functionColor);
                    i += 9;
                    builder.insert(i, "[]");
                    i += 2;
                    builder.insert(i, ColorStyle.bracketColor);
                    i += 10;
                    builder.insert(i, "[]");
                    i += 1;
                } else {
                    builder.insert(prev, ColorStyle.globalVarColor);
                    i += 9;
                    builder.insert(i, "[]");
                    i += 1;
                }
            } else if (builder.charAt(i) == '>'){
                builder.insert(i, ColorStyle.bracketColor);
                i += 10;
                builder.insert(i, "[]");
                i += 2;
            } else if (builder.charAt(i) == '°'){
                builder.insert(i, ColorStyle.intervalVarColor);
                i += 10;
                while ( i < builder.length && AlphabetManager.isValidAlphabetChar(builder.charAt(i))) i++;
                builder.insert(i, "[]");
                i += 2;
            }
        }
        return ColorStyle.defaultColor + builder.toString();
    }
}
