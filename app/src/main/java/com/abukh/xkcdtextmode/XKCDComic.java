package com.abukh.xkcdtextmode;

import java.io.Serializable;
import java.util.ArrayList;

public class XKCDComic implements Serializable { // Model class to store retrieved (json) comic data

    private int comicNum;
    private int month;
    private int year;
    private String title;
    private String transcript;
    private String imageLink;

    public XKCDComic(int comicNum, int month, int year, String title, String transcript, String imageLink) {
        this.comicNum = comicNum;
        this.month = month;
        this.year = year;
        this.title = title;
        this.transcript = transcript;
        this.imageLink = imageLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public int getComicNum() {
        return comicNum;
    }

    public String getTitle() {
        return title;
    }

    public String[] getTranscript() {
        String[] lines;
        if (transcript.isEmpty()) {
            lines = new String[]{"No transcript found :("};
        } else {
            lines = transcript.split("\n");

            ArrayList<String> newLines = new ArrayList<>();

            for (int i = 0; i < lines.length; i++) {
                if (lines[i].isEmpty()) {
                    continue;
                } else if (lines[i].contains("{{")) {
                    lines = newLines.toArray(new String[0]);
                    break;
                } else {
                    newLines.add(lines[i]);
                }
            }
        }

        return lines;
    }

    public String getDate() {
        String month_name = Integer.toString(month);

        switch (month) {
            case 1 : month_name = "January"; break;
            case 2 : month_name = "February"; break;
            case 3 : month_name = "March"; break;
            case 4 : month_name = "April"; break;
            case 5 : month_name = "May"; break;
            case 6 : month_name = "June"; break;
            case 7 : month_name = "July"; break;
            case 8 : month_name = "August"; break;
            case 9 : month_name = "September"; break;
            case 10 : month_name = "October"; break;
            case 11 : month_name = "November"; break;
            case 12 : month_name = "December"; break;
        }

        return month_name + ", " + year;
    }
}
