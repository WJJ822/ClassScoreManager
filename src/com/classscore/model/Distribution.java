package com.classscore.model;

/**
 * 成绩分布柱状图数据。
 */
public class Distribution {

    private String label;
    private String range;
    private int count;
    private double percent;
    private double barHeight;
    private String tone;

    public Distribution() {
    }

    public Distribution(String label, String range, String tone) {
        this.label = label;
        this.range = range;
        this.tone = tone;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public double getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(double barHeight) {
        this.barHeight = barHeight;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }
}
