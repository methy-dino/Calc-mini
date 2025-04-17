package com.github.calclogic;
import com.github.basics.*;
import com.github.nameManaging.AlphabetManager;

import java.util.ArrayList;
public class Parser implements Runnable{
  Stack<String> operators = new Stack<String>(-1);
  VarStorage vars;
  FnStorage functions;
  public String currMath;
  public String lastAnswer = "0";
  public byte measureType;
  public ArrayList<String> graphFns = new ArrayList<String>();
  public Parser(VarStorage varStore, FnStorage fnStore){
    System.out.print("Parser constructor called... ");
    vars = varStore;
    functions = fnStore;
    System.out.println("construction successful!");
  }
  public void run(){
    // TODO: ADD FN VALUE CALCULATOR FOR GRAPHS.
  }
  public double compute(String input){
    return calculate(parse(separate(input)));
  }
  //public ArrayList<String> function
  public String funcCalc(String functionName, ArrayList<String> toSwap){
    StringBuilder copy = new StringBuilder();
    switch (functionName) {
      case "sin":
      return measureType == 1 ? String.valueOf(Math.sin(Double.parseDouble(toSwap.get(0)))) : String.valueOf(Math.sin(Math.toRadians(Double.parseDouble(toSwap.get(0)))));
      case "cos":
      return measureType == 1 ? String.valueOf(Math.cos(Double.parseDouble(toSwap.get(0)))) : String.valueOf(Math.sin(Math.toRadians(Double.parseDouble(toSwap.get(0)))));
      case "tan":
      return measureType == 1 ? String.valueOf(Math.tan(Double.parseDouble(toSwap.get(0)))) : String.valueOf(Math.sin(Math.toRadians(Double.parseDouble(toSwap.get(0)))));
      default:
      String func = functions.getFunc(functionName);
      int prev = 0;
      for(int i = 0; i < func.length(); i++){
        if (func.charAt(i) == '<' && 47 < (int) func.charAt(i + 1) && (int) func.charAt(i + 1) < 58){
          //copy.append(function.substring(prev, i));
          prev = i + 1;
          while (func.charAt(i) != '>'){
            i++;
          }
        copy.append(toSwap.get(Integer.parseInt(func.substring(prev, i))));
      } else {
        copy.append(func.charAt(i));
      }
    }
    return String.valueOf(compute(copy.toString()));
    }

  }
  /**
   * @param operation
   * @return separatedOperation
   */
  public ArrayList<String> separate(String operation){
    //System.out.println("separate called!");
    ArrayList<String> toReturn = new ArrayList<String>();
    int prev = 0;
    int i = 0;
    boolean lastOperator = false;
    while (i < operation.length()){
      // for operators.
      if ((int) operation.charAt(i) < 48 || (int) operation.charAt(i) == 94 || (int) operation.charAt(i) > 900){
        if (operation.charAt(i) ==  'π'){
          toReturn.add(String.valueOf(Math.PI));
          i++;
          if (i == operation.length()){
            break;
          }
        }
        if (i == 0 && (operation.charAt(i) == '-' || operation.charAt(i) == '+')){
          i++;
          while (i < operation.length() - 1 && (47 < (int) operation.charAt(i) && (int) operation.charAt(i) < 58 || operation.charAt(i) == '.' || operation.charAt(i) == 'π')){
            i++;
          }
           if (operation.charAt(i) ==  'π'){
          toReturn.add(String.valueOf(Math.PI));
          i++;
          if (i == operation.length()){
            break;
          }
        }
          if (i == operation.length() - 1 && operation.charAt(i) != ')' && operation.charAt(i) != '!'){
            i++;
          }
          toReturn.add(operation.substring(prev, i));
        } else {
          if (operation.charAt(i) != '(' && operation.charAt(i) != ')' && operation.charAt(i) != '!') lastOperator = true;
          toReturn.add(Character.toString(operation.charAt(i)));
          i++;
          prev = i;
          if (i < operation.length() - 1 && lastOperator && (operation.charAt(i) == '-' || operation.charAt(i) == '+')){
            lastOperator = false;
            i++;
            if (operation.charAt(i) ==  'π'){
              toReturn.add(operation.charAt(i - 1) + String.valueOf(Math.PI));
              i++;
              if (i == operation.length()){
                break;
              }
            }
          while (i < operation.length() - 1 && (47 < (int) operation.charAt(i) && (int) operation.charAt(i) < 58 || operation.charAt(i) == '.' || operation.charAt(i) == 'π')){
            i++;
          }
          if (i == operation.length() - 1 && operation.charAt(i) != ')' && operation.charAt(i) != '!'){
            i++;
          }
          toReturn.add(operation.substring(prev, i));
          }
        }
          prev = i;
      }
        if (i == operation.length()) break;
      // for functions and vars
      if (AlphabetManager.isValidAlphabetChar(operation.charAt(i))){
        lastOperator = false;
        prev = i;
        while (AlphabetManager.isValidAlphabetChar(operation.charAt(i)) && i < operation.length() - 1){
          i++;
          //System.out.println(i + " index");
          //System.out.println(operation.substring(prev, i));
        }
        if (operation.charAt(i) == '<'){
          // bruh
          String fnName = operation.substring(prev, i);
          prev = i;
          ArrayList<String> converted = new ArrayList<String>();
          while (operation.charAt(i) != '>'){
            while (operation.charAt(i) != ',' && operation.charAt(i) != '>'){
            i++;
          }
          //System.out.println("egg " + compute(operation.substring(prev+1, i)));
          converted.add(String.valueOf(compute(operation.substring(prev+1, i))));
          prev = i;
          if (operation.charAt(i) == '>'){
            i++;
            break;
          }
          i++;
        }
        /*System.out.print("converted: [");
        for (int j = 0; j < converted.size(); j++){
          System.out.print(converted.get(j) + ", ");
        }
        System.out.println("] " + i + " length");*/
        String temp = funcCalc(fnName, converted);
        //System.out.println("temp  " + temp);
        toReturn.add(temp);
        if (i >= operation.length() - 1){
          //System.out.println("breakerh");
          return toReturn;
        }
        } else {
          if (i == operation.length() - 1 && operation.charAt(i) != ')' && !(47 < (int) operation.charAt(i) && (int) operation.charAt(i) < 58)){
          i++;
          }
          switch (operation.substring(prev, i)){
              case "ans":
              toReturn.add(lastAnswer);
              break;
            default:
              //System.out.println(vars.getVar(operation.substring(prev, i)));
              toReturn.add(vars.getVar(operation.substring(prev, i)));
          }
          //i--;
        }
        prev = i;
        //i++;
      }
      if (i == operation.length()) break;
      // for adding numbers
      if ((47 < (int) operation.charAt(i) && (int) operation.charAt(i) < 58) || (operation.charAt(i) == '.')){
        lastOperator = false;
        while (((47 < (int) operation.charAt(i) && (int) operation.charAt(i) < 58) || operation.charAt(i) == '.') && i < operation.length() - 1){
          i++;
        }
        if (i == operation.length() - 1 && operation.charAt(i) != ')' && operation.charAt(i) != '!'){
          i++;
        }
        //System.out.println("adding number");
        toReturn.add(operation.substring(prev, i));
        prev = i;
        //i++;
      }
    }
    i = 0;
    return toReturn;
  }
  boolean hasOpen = false;
  public Stack<String> parse(ArrayList<String> divided) /*throws Invalid*/{
    //String[] divided = operation.split(" ");
     Stack<String> solved = new Stack<String>(-1);
    /*System.out.print("divided (Depth " + dep + " ): [");
    for(int i = 0; i < divided.size(); i++){
      System.out.print(divided.get(i)+", ");
    }
     System.out.println("]");*/
     for (int i = 0; i < divided.size(); i++){
      System.out.println("Pushing into solved: " + divided.get(i));
     }
    for (int i = 0; i < divided.size(); i++){
      //System.out.println("Pushing into solved: " + divided.get(i));
      if ((47 < (int) divided.get(i).charAt(0) && (int) divided.get(i).charAt(0) < 58) || (divided.get(i).length() > 1 && 47 < (int) divided.get(i).charAt(1) && (int) divided.get(i).charAt(1) < 58)){
        solved.add(divided.get(i));
      } else {
        switch (divided.get(i)){
          case ")":
             while (operators.len > 0 && !(operators.get(-1).equals("("))){
              String temp = operators.pop();
              solved.add(temp);
              //System.out.println("Pushing into solved: " + temp + " 93");
            }
            operators.pop();
            //System.out.println(operators.pop());
            break;
          case "(":
            operators.add(divided.get(i));
            break;
          default:
            while (operators.len > 0 && getPriority(operators.get(-1)) >= getPriority(divided.get(i))){
              String temp = operators.pop();
              //System.out.println("Pushing into solved: " + temp + " 101");
              solved.add(temp);

              }
              operators.add(divided.get(i));
        }
      }
    }/*
    for (int i = 0; i < divided.length; i++){
      if (47 < (int) divided[i].charAt(0) && (int) divided[i].charAt(0) < 58){
              System.out.println("Pushing into solved: " + divided[i]);
          solved.add(divided[i]);
      } else {
        switch (divided[i]){
          case ")":
             while (operators.len > 0 && !(operators.get(-1).equals("("))){
              String temp = operators.pop();
              solved.add(temp);
              System.out.println("Pushing into solved: " + temp);
            }
            operators.pop();
          case "(":
            operators.add(divided[i]);
          default:
            while (operators.len > 0 && getPriority(operators.get(-1)) >= getPriority(divided[i])){
              String temp = operators.pop();
              solved.add(temp);
              System.out.println("Pushing into solved: " + temp);
              }
              operators.add(divided[i]);
        }
      }
    } */
      /*if (divided[i].equals(")")){
        while (operators.len > 0 && !(operators.get(-1).equals("("))){
              String temp = operators.pop();
              solved.add(temp);
              System.out.println("Pushing into solved: " + temp);
            }
            operators.pop();
      } else if(divided[i].equals("(")){
        operators.add(divided[i]);
      } else if (divided[i].indexOf("log(") != -1){
        solved.add(logCalc(divided[i]));
      } else {
        while (operators.len > 0 && getPriority(operators.get(-1)) >= getPriority(divided[i])){
          String temp = operators.pop();
              solved.add(temp);
              System.out.println("Pushing into solved: " + temp);
        }
        operators.add(divided[i]);
      }
    }*/
    while (operators.len > 0){
      String temp = operators.pop();
      if (temp.equals("(") && operators.len > 0){
        solved.add(operators.pop());
      } else {
        solved.add(temp);
      }
      //System.out.println("ending " + temp);
    }
    /*System.out.print("solved: [");
    for(int i = 0; i < solved.len; i++){
      System.out.print(solved.get(i)+ ", ");
    }
     System.out.println("]");*/
    return solved;
  }
  public double calculate(Stack<String> RPNCalc){
    Stack<Double> nums = new Stack<Double>(-1);
    double numA = 0;
    double numB = 0;
    String curr = "";
     //if (RPNCalc.len < 2) return Double.parseDouble(RPNCalc.pop());
    for (int i = 0; i < RPNCalc.len; i++){
      curr = RPNCalc.get(i);
      if ((47 < (int) curr.charAt(0) && (int) curr.charAt(0) < 58) || (curr.length() > 1 && 47 < (int) curr.charAt(1) && (int) curr.charAt(1) < 58)){
        nums.add(Double.parseDouble(curr));
      } else {
        switch (curr){
          case "+":
            numA = nums.pop();
            numB = nums.pop();
            nums.add(numB + numA);
            break;
          case "-":
            numA = nums.pop();
            numB = nums.pop();
            nums.add(numB - numA);
            break;
          case "*":
            numA = nums.pop();
            numB = nums.pop();
            nums.add(numB * numA);
            break;
          case "/":
            numA = nums.pop();
            numB = nums.pop();
            nums.add(numB / numA);
            break;
          case "!":
            numA = nums.pop();
            numB = numA;
            for (int j = 1; j < numB; j++){
              numA = numA * (numB - j);
            }
            nums.add(numA);
            break;
          case "^":
            numA = nums.pop();
            numB = nums.pop();
            nums.add(Math.pow(numB, numA));
            break;
          case "√":
            numA = nums.pop();
            nums.add(Math.sqrt(numA));
            break;
          case "∜":
          numA = nums.pop();
          numB = nums.pop();
          nums.add(Math.pow(numA, 1 / numB));
          break;
          case "㏒":
          numA = nums.pop();
          numB = nums.pop();
          nums.add(Math.log(numA) / Math.log(numB));
          break;
          case "sin":
          numA = nums.pop();
          if (measureType == 0){
            nums.add(Math.sin(Math.toRadians(numA)));
          } else {
            nums.add(Math.sin(numA));
          }
          break;
          case "cos":
          numA = nums.pop();
          if (measureType == 0){
            nums.add(Math.cos(Math.toRadians(numA)));
          } else {
            nums.add(Math.cos(numA));
          }
          break;
          case "tan":
          numA = nums.pop();
          if (measureType == 0){
            nums.add(Math.tan(Math.toRadians(numA)));
          } else {
            nums.add(Math.tan(numA));
          }
          break;
        }
      }

      //System.out.println(nums.len + " nums length");
    }

     for (int j = 0; j < nums.len; j++){

    }
    return nums.pop();
  }
  static int getPriority(String operator){
    switch (operator){
      case "+":
        return 1;
      case "-":
        return 1;
      case "*":
        return 2;
      case "/":
        return 2;
      case "!":
        return 3;
      case "^":
        return 3;
      case "㏒":
        return 3;
      case "√":
        return 3;
      case "nrt":
        return 3;
      case "sin":
        return 3;
      case "cos":
        return 3;
      case "tan":
        return 3;
      default:
        return 0;
    }
    //return 0;
  }
}
