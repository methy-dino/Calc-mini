package com.github.basics;
import java.util.ArrayList;
public class Stack<Type>{
  ArrayList<Type> arr;
  public int len = 0;
  public Stack(int maxLen){
    arr = new ArrayList<Type>();
  }
  public void add(Type value){
    len++;
    arr.add(value);
  }
  public Type get(int num){
    return (num < 0) ? arr.get(arr.size() + num) : arr.get(num);
  }
  public Type pop(){
    len--;
    Type temp = arr.get(len);
    arr.remove(len);
    return temp;
  }
  public void clear(){
    for (Type thing : arr){
      System.out.println(thing);
    }
    arr.clear();
  }
  public String join(String between){
    String toReturn = "";
    for (int i = 0; i < arr.size() - 1; i++){
      toReturn += arr.get(i) + between;
    }
    toReturn += arr.get(arr.size() - 1);
    return toReturn;
  }
}