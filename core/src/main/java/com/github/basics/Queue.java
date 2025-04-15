package com.github.basics;
public class Queue<Type> /*throws Invalid*/{
  private LinkNode<Type> head = null;
  private LinkNode<Type> last = head;
  private LinkNode<Type> temp = null;
  private int length = 0;
  public Type lookLast(){
    return last.val;
  }
  public void add(Type value){
    //if (!(length == maxLength)){
    if (head == null){
       head = new LinkNode<Type>(value);
       last = head;
    } else {
    last.next = new LinkNode<Type>(value);
    last = last.next;
    }
    length++;
    //}
  }
  public void addBottom(Type value){
    //if (!(length == maxLength)){
    if (head == null){
       head = new LinkNode<Type>(value);
       last = head;
    } else {
      LinkNode temp = new LinkNode<Type>(value);
    temp.next = head;
    head = temp;
    }
    length++;
    //}
  }
  public Type pop(){
    if (length == 0) /*throw new Invalid() */;
    length--;
    temp = head;
    head = head.next;
    return temp.val;
  }
  public void clear(){
    while (length > 0){
      System.out.print(" " + this.pop());
    }
  }
}
class LinkNode<T>{
  LinkNode<T> next = null;
  T val;
  LinkNode(T value){
    super();
    val = value;
  }
}