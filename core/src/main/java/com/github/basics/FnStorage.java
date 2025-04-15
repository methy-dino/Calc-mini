package com.github.basics;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
public class FnStorage{
    final private HashMap<String, String[]> functions;
    final private FileHandle file;
    final private Json json;
    final private Preferences prefs;
    final private String prefKey;
    public FnStorage(String filePath, Json writer) {
      prefKey = null;
      json = writer;
      file = Gdx.files.local(filePath);
      prefs = null;
      System.out.println("Importing functions...  ");
      functions = writer.fromJson(HashMap.class, String[].class, file);
      System.out.println("Importing sucessful!");
    }
    public FnStorage(String prefName, String key, Json writer){
      file = null;
      json = writer;
      prefKey = key;
      prefs = Gdx.app.getPreferences(prefName);
      functions = writer.fromJson(HashMap.class, String[].class,prefs.getString(key, "{}"));
    }
    public boolean has(String target){
        return functions.containsKey(target);
    }
    public String[] getFuncData(String target){
      return functions.get(target);
    }
    public String getFunc(String target) {
        return functions.get(target)[0];
    }
    public void saveFunctions(){
      System.out.print("saving functions...");
      if (prefs != null){
        prefs.putString(prefKey, json.prettyPrint(functions));
        prefs.flush();
      } else {
        file.writeString(json.prettyPrint(functions), false);
      }
      System.out.println(" Save was sucessful!");
    }
    /**
    *
    **/
    public void setFunc(String fnName, String[] fnData){
      functions.put(fnName, fnData);
    }
    public boolean importJson(String absFilePath){
      try{
        HashMap<String, String[]> toAdd = json.fromJson(HashMap.class, String[].class, Gdx.files.external(absFilePath));
        for (String i : toAdd.keySet()){
          functions.put(i, toAdd.get(i));
        }
        return true;
      } catch(Exception e){
        return false;
      }
    }
    public void deleteAll(){
      functions.clear();
    }
    public void deleteFunc(String target){
      functions.remove(target);
    }
    public void delete(String[] targets){
      for (String i : targets){
        functions.remove(i);
      }
    }
    public Set<String> keys(){
      return functions.keySet();
    }
  }
