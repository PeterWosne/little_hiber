package ru.ermakow.entities;

import ru.ermakow.Column;
import ru.ermakow.Id;
import ru.ermakow.Table;

@Table(name = "cats")
public class Cat {

    @Id
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private String color;

    public Cat(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Cat(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
