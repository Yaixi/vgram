package com.company;

import java.util.Comparator;

public class IndexInfo implements Comparable<IndexInfo> {

    private  int id;
    private  int position;
    private  int freq;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    @Override
    public String toString() {
        return "IndexInfo{" +
                "id=" + id +
                ", position=" + position +
                ", freq=" + freq +
                '}';
    }


    @Override
    public int compareTo(IndexInfo o) {
        return this.getId() - o.getId();
    }
}
