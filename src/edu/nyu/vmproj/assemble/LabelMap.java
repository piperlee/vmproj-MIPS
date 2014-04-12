package edu.nyu.vmproj.assemble;

import java.util.HashMap;

public class LabelMap {
	private static LabelMap instance;
	private HashMap<String, Integer> labelMap;
	
	private LabelMap() {
		labelMap = new HashMap<String, Integer>();
	}
	
	public static LabelMap getInstance() {
		if (instance == null) instance = new LabelMap();
		return instance;
	}
	
	public void put(String k, Integer v) {
		labelMap.put(k, v);
	}
	
	public Integer get(String k) {
		if (!labelMap.containsKey(k)) {
			System.err.println("Label "+k+" is not found!");
			return null;
		} else {
			return labelMap.get(k);
		}
	}
	
	public boolean contains(String k) {
	  return labelMap.containsKey(k);
	}
}
