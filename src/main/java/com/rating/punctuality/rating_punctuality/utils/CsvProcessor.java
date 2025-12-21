package com.rating.punctuality.rating_punctuality.utils;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.*;

public class CsvProcessor {

    public static List<Map<String, Object>> getTopDelayRules(String filePath, int topN) throws Exception {
    try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
        List<String[]> allRows = reader.readAll();
        
        if (allRows.size() < 2) {
            throw new Exception("Файл пуст или содержит только заголовок");
        }
        
        String[] header = allRows.get(0);
        int confidenceIndex = -1;
        int liftIndex = -1;
        int supportIndex = -1;
        int formattedRuleIndex = -1;
        
        for (int i = 0; i < header.length; i++) {
            String col = header[i].trim();
            if (col.equals("confidence")) confidenceIndex = i;
            if (col.equals("lift")) liftIndex = i;
            if (col.equals("support")) supportIndex = i;
            if (col.equals("formatted_rule")) formattedRuleIndex = i;
        }
        
        if (confidenceIndex == -1 || liftIndex == -1 || 
            supportIndex == -1 || formattedRuleIndex == -1) {
            throw new Exception("Не найдены необходимые колонки в CSV файле");
        }
        
        List<Object[]> rules = new ArrayList<>();
        for (int i = 1; i < allRows.size(); i++) {
            String[] row = allRows.get(i);
            if (row.length > Math.max(confidenceIndex, Math.max(liftIndex, 
                Math.max(supportIndex, formattedRuleIndex)))) {
                try {
                    String rule = row[formattedRuleIndex];
                    double support = Double.parseDouble(row[supportIndex]);
                    double confidence = Double.parseDouble(row[confidenceIndex]);
                    double lift = Double.parseDouble(row[liftIndex]);
                    
                    rules.add(new Object[]{rule, support, confidence, lift});
                } catch (NumberFormatException e) {}
            }
        }

        rules.sort((a, b) -> {
            double liftCompare = (double) b[3] - (double) a[3];
            if (liftCompare != 0) return liftCompare > 0 ? 1 : -1;
            double confCompare = (double) b[2] - (double) a[2];
            return confCompare > 0 ? 1 : -1;
        });
        
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, rules.size()); i++) {
            Object[] rule = rules.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("rule", rule[0]);
            map.put("support", rule[1]);
            map.put("confidence", rule[2]);
            map.put("lift", rule[3]);
            results.add(map);
        }
        
        return results;
    }
}
}