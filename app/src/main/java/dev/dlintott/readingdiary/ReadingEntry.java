package dev.dlintott.readingdiary;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ReadingEntry implements Serializable {
    private long id;
    private String title;
    private long date;
    private int pageFrom;
    private int pageTo;
    private float rating;
    private String childComment;
    private String parentComment;
    private String bookImage;

    public ReadingEntry(long id, String title, long date, int pageFrom, int pageTo, float rating,
                        String childComment, String parentComment, String bookImage) {
        this.id = id;
        this.title = title;
        this.pageFrom = pageFrom;
        this.pageTo = pageTo;
        this.rating = rating;
        this.childComment = childComment;
        this.parentComment = parentComment;
        this.bookImage = bookImage;
        this.date = date;
    }

    public ReadingEntry(String title, long date, int pageFrom, int pageTo, float rating,
                        String childComment, String parentComment, String bookImage) {
        this.title = title;
        this.date = date;
        this.pageFrom = pageFrom;
        this.pageTo = pageTo;
        this.rating = rating;
        this.childComment = childComment;
        this.parentComment = parentComment;
        this.bookImage = bookImage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getPageFrom() {
        return pageFrom;
    }

    public void setPageFrom(int pageFrom) {
        this.pageFrom = pageFrom;
    }

    public int getPageTo() {
        return pageTo;
    }

    public void setPageTo(int pageTo) {
        this.pageTo = pageTo;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getChildComment() {
        return childComment;
    }

    public void setChildComment(String childComment) {
        this.childComment = childComment;
    }

    public String getParentComment() {
        return parentComment;
    }

    public void setParentComment(String parentComment) {
        this.parentComment = parentComment;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }


}
