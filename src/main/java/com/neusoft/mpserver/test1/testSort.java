package com.neusoft.mpserver.test1;

import com.neusoft.mpserver.jszkoffline.service.impl.PatentSearchServiceImpl;
import thk.analyzer.ThkAnalyzer;
import thk.analyzer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class testSort {
    public static void main(String [] args){
        List list=new ArrayList();
        try {
            list= testSort.sortByTokenFrequence(ThkAnalyzer.getInstance().analysis("本发明所要解决的技术问题是提供一种阻水电力电缆，其可以有效阻止水或水气进入电缆内，解决水或水气进入电缆中所带来的影响及危害。"));
            for(Object o : list){
                Token token = (Token)o;
                System.out.println(token.getWord()+": "+ token.getFreq());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static List sortByTokenFrequence(List sourceList){
        Collections.sort(sourceList, new testSort.TokenComparator());
        return sourceList;
    }
    private static class TokenComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Token token1 = (Token) o1;
            Token token2 = (Token) o2;
            if (token1.getFreq() > token2.getFreq())
                return -1;
            if(token1.getFreq() < token2.getFreq())
                return 1;
            return 0;
        }
    }
    private List filterListByTokenName(List source){
        List target=new ArrayList();
        int size=source.size();
        if(source == null || size < 0){
            return  null;
        }
        for(int i=0;i<size; i++){
            Token token= (Token) source.get(i);
            int length = token.getWord().length();
            if(length >= 2){
                target.add(token);
            }
        }
        return target;
    }

}
