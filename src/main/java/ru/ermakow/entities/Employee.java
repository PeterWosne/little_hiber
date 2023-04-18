package ru.ermakow.entities;

import ru.ermakow.Column;
import ru.ermakow.Id;
import ru.ermakow.Table;

@Table(name = "employees")
public class Employee {
    @Id
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String email;

    @Column
    private int salary;

    public Employee(String name, String phone, String email, int salary) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
    }
}
