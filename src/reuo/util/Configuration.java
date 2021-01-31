package reuo.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.json.*;

public class Configuration {
	private JSONObject map = new JSONObject();
	private String fileName;
	
	public Configuration(){
		
	}
	
	public void load(String fileName) throws IOException {
		this.fileName = fileName;
		FileChannel in;
		try {
			in = new FileInputStream(fileName).getChannel();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		}
		ByteBuffer buf = ByteBuffer.allocate((int)in.size());
		buf.clear();
		in.read(buf);
		buf.rewind();
		
		String json = new String(buf.array(),"US-ASCII");
		try {
			map = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
			map = new JSONObject();
		}
	}
	
	public Object get(String key) throws JSONException{
		return map.get(key);
	}
	
	public void set(String key, Object value) throws JSONException{
		map.put(key, value);
	}
	
	public JSONObject getJSONObject(){
		return map;
	}
	
	public void save(String fileName) throws IllegalArgumentException, JSONException, IOException{
		this.fileName = fileName;
		save();
	}
	
	public void save() throws JSONException, IOException, IllegalArgumentException{
		if(fileName == null){ 
			throw new IllegalArgumentException("File never specified.");
		}
		FileWriter out = new FileWriter(fileName);
		out.write(map.toString(1));
		out.flush();
		out.close();
	}
	
	public static void main(String[] args) throws IOException, JSONException {
		Configuration conf = new Configuration();
		JSONObject map = new JSONObject();
		JSONObject nestedMap = new JSONObject();
		map.put("nested", nestedMap);
		map.put("Cocaine", "isAHelluvaDrug");
		map.put("CharlieMurphy", "Blackness");
		nestedMap.put("WhatDidTheFingersSayToTheFace", "Slap");
		conf.set("map",map);
		
		System.out.println(((JSONObject)conf.get("map")).get("Cocaine"));
		//System.out.println(map.toString(1));
		conf.save();
	}
}
