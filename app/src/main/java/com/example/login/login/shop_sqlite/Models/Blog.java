package com.example.login.login.shop_sqlite.Models;

public class Blog {
    private int blogId;
    private Integer blogCategoryId;
    private String blogTittle;
    private String blogContent;

    public int getBlogId() { return blogId; }
    public void setBlogId(int blogId) { this.blogId = blogId; }

    public Integer getBlogCategoryId() { return blogCategoryId; }
    public void setBlogCategoryId(Integer blogCategoryId) { this.blogCategoryId = blogCategoryId; }

    public String getBlogTittle() { return blogTittle; }
    public void setBlogTittle(String blogTittle) { this.blogTittle = blogTittle; }

    public String getBlogContent() { return blogContent; }
    public void setBlogContent(String blogContent) { this.blogContent = blogContent; }
} 