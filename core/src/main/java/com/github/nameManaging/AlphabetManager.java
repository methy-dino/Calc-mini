package com.github.nameManaging;
import com.badlogic.gdx.utils.StringBuilder;
public class AlphabetManager {
    public static boolean isValidAlphabetChar(char character){
        return ((int) character > 64 && (int) character < 91) || ((int) character > 96 && (int) character < 123);
    }
    public static boolean isNumber (char character){
      return (47 < (int) character && (int) character < 58);
    }
    /**
     * validator who uses isValidAlphabetChar.
     * @param name — String that needs validation, variable or function name.
     * @return true if valid, false if invalid.
     */
    public static boolean isValidName(String name){
      if (name.length() == 0 || name == null) return false;
      for (int i = 0; i < name.length(); i++){
        if (!(isValidAlphabetChar(name.charAt(i)))) return false;
      }
      return true;
    }
    /**
     * returns a DEFINED mathematical function with readable arguments (substituting the <n> breaks).
     * @param rawFunctionData — function data from the FnStorage. 
     * @return String — human readable math function :)
     */
    public static StringBuilder varFunction(String[] rawFunctionData){
        StringBuilder manipulator = new StringBuilder();
        int prev = 0;
        for (int i = 0; i < rawFunctionData[0].length(); i++){
                if (rawFunctionData[0].charAt(i) == '<' && 47 < (int) rawFunctionData[0].charAt(i + 1) && (int) rawFunctionData[0].charAt(i + 1) < 58){
                  prev = i + 1;
                  while (rawFunctionData[0].charAt(i) != '>'){
                    i++;
                  }
                  manipulator.append(rawFunctionData[Integer.parseInt(rawFunctionData[0].substring(prev, i)) + 1]);
              } else {
                manipulator.append(rawFunctionData[0].charAt(i));
              }
        } 
        return manipulator;

    }
}
