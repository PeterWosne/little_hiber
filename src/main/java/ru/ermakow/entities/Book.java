package ru.ermakow.entities;

import ru.ermakow.Column;
import ru.ermakow.Id;
import ru.ermakow.Table;

@Table(name = "books")
public class Book {

    @Id(autoincrement = false)
    @Column(name = "book_id")
    private int id;

    @Column(name = "book_title")
    private String title;

    @Column(name = "book_price")
    private int price;

    public Book(String title, int price) {
        this.title = title;
        this.price = price;
    }

    public Book(int id, String title, int price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }
}
