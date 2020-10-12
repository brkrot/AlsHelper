package com.example.alshelper.processingData;
import java.util.HashMap; // import the HashMap class

public class DiagnosisDataCollector {
    public DiagnosisDataCollector(){
        HashMap<String, Integer> dataMap = new HashMap<String, Integer>();
        dataMap.put("On off ability", 0);
        dataMap.put("Right ability", 0);
        dataMap.put("Left ability", 0);
        dataMap.put("Combine directions ability", 0);
    }
    public HashMap<String, Integer> dataMap;

}
