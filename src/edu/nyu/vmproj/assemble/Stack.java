package edu.nyu.vmproj.assemble;

import java.util.LinkedList;

public class Stack {
	private static Stack instance;
	private LinkedList<Integer> myStack;
	
	private Stack() {
		myStack = new LinkedList<Integer>();
	}
	
	public static Stack getInstance() {
		if (instance == null)
			instance = new Stack();
		return instance;
	}
	
	public void push(Integer i) {
		myStack.addLast(i);;
	}
	
	public Integer pop() {
		if (myStack.size() == 0) {
			System.err.println("Stack is empty.");
			return null;
		} else {
			return myStack.removeLast();
		}
	}
}
