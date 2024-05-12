package com.example.rabie;

public class Course {
    private String id;
    private String name;
    private String description;
    private String category;
    private String pdfUrl;
    private String key;
    private String teacherName;


    public Course() {
    }

    public Course(String id, String name, String pdfUrl, String key, String description, String category) {
        this.id = id;
        this.name = name;
        this.pdfUrl = pdfUrl;
        this.key = key;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
