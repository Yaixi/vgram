package com.company;

import org.apache.lucene.util.RamUsageEstimator;

import java.util.ArrayList;

public class Main {

    public static long count = 0L;
    public static void vgram(ArrayList s, int qmin, int qmax,int T){
        VGramDictionary qgramdictionary=new VGramDictionary(qmin,qmax);
        qgramdictionary.createDic(qgramdictionary.getroot(), s);
        qgramdictionary.Prune(qgramdictionary.getroot(), T);
        qgramdictionary.printDic(qgramdictionary.getroot());
    }
    public static void vtokengram(ArrayList s, int qmin, int qmax,int T){
        VTokenGramDictionary vTokenGramDictionary = new VTokenGramDictionary(qmin, qmax);
        vTokenGramDictionary.createDic(vTokenGramDictionary.getroot(), s);
        vTokenGramDictionary.Prune(vTokenGramDictionary.getroot(), T);

        vTokenGramDictionary.printDic(vTokenGramDictionary.getroot());
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String gramfile = "src/main/resources/testgram.txt"; //1.gram
        String tokenfile = "src/main/resources/test.txt"; // 2.tokengram
        readfile r=new readfile(gramfile);
        ArrayList s;
        s=r.readtxt();
        for(int i=0;i<s.size();i++)
            System.out.println(s.get(i));
        int qmin=2;
        int qmax=3;
        int  T=6;
        //1.vgram 建立gram trie树的过程
        vgram(s,qmin,qmax,T);
        //2. v-token gram 建立树的过程
//        vtokengram(s,qmin,qmax,3);

        System.out.println(RamUsageEstimator.humanReadableUnits(count));


    }
}
