package com.example.merobook.models;

public class Book {
    private String bookId;
    private String bookName;
    private String authorName;
    private String imageUrl;

    public Book(String bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

