package pva;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class FrequencyList
{
    public FrequencyList()
    {
        map = new HashMap<>();
    }
    Map<String, Integer> map = new HashMap<>();
    private Map<String, Integer> sorted_map;
    
    public void add(String x, int freq)
    {
        map.put(x, freq);
    }
    
    public void clear()
    {
        map = new HashMap<>();
    }
            
    public void cut(int minFreq)
    {
        for (int i = 1; i < minFreq; i++)
        {
            map.values().removeAll(Collections.singleton(i));
        }
    }
    
    public int size()
    {
        return map.size();
    }
    
    public String[] getTokens()
    {
        return map.keySet().toArray(new String[map.size()]);
    }
    
    public int getFrequency(String item)
    {
        try
        {
        if (map.containsKey(item))
        {
            return map.get(item);
        }
        }
        catch (NullPointerException e) { return 0;}
        return 0;
    }
    
    public void sort()
    {
        ValueComparator comparator = new ValueComparator(map);
        sorted_map = new TreeMap<> (comparator);
        for (String w : map.keySet())
        {
            sorted_map.put(w, map.get(w));
        }
        map = sorted_map;
    }
    
    public void print()
    {
        for (String w : map.keySet())
        {
            int count = (Integer) map.get(w);
            System.out.println(count + "\t" + w);
        }
    }
    
    public void writeToFile(String fname)
    {
        try
        {
            PrintWriter pw = new PrintWriter(new FileWriter(fname));
            for (String w : map.keySet())
            {
                int count = (Integer) map.get(w);
                pw.println(count + "\t" + w);
            }
            pw.close();
        }
        catch (IOException ex)
        {
            System.err.println("ERROR: Can't export to file!");
        }
    }
    
    public void loadFromFile(File f)
    {
        try
        {
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine())
            {
                String x = scanner.nextLine();
                if (x.contains("\t"))
                {
                    String[] xarr = x.trim().split("\t");
                    Integer num = Integer.parseInt(xarr[0]);
                    String word = xarr[1];
                    map.put(word, num);
                }
            }
            scanner.close();
            sort();
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("ERROR: Can't import from file!");
        }
    }
    
    public class ValueComparator implements Comparator
    {
 
    Map map;

    public ValueComparator(Map map){
      this.map = map;
    }
    public int compare(Object keyA, Object keyB){

      Comparable valueA = (Comparable) map.get(keyA);
      Comparable valueB = (Comparable) map.get(keyB);

      //System.out.println(valueA +" - "+valueB);
      if (valueB.compareTo(valueA) != 0)
        return valueB.compareTo(valueA);
      
      
      return ((Comparable) keyA).compareTo((Comparable) keyB);
    }
  }
}