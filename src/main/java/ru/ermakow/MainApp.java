package ru.ermakow;

import ru.ermakow.entities.Book;
import ru.ermakow.entities.Cat;
import ru.ermakow.entities.Employee;

import java.util.List;

public class MainApp {

    public static void main(String[] args) {
        LittleHiber.createTable(Cat.class);
        LittleHiber.createTable(Employee.class);
        LittleHiber.createTable(Book.class);

        Cat cat = new Cat("Lada", "grey");
        //LittleHiber.insertObject(cat);

        List<Employee> employees = List.of(
                new Employee("Peter", "+7(977)666-66-66", "welcome_to_hell@gmail.com", 100000),
                new Employee("Anna", "03", "welcome_to_hell2@gmail.com", 98000)
        );

//        for (Employee employee : employees) {
//            LittleHiber.insertObject(employee);
//        }

//        LittleHiber.insertObject(new Book(1, "Tihiy Don", 699));
//        LittleHiber.insertObject(new Book(5, "Справочник Java", 2000));

//        LittleHiber.insertObject(new Employee("Grigory", "66376783777","someemail@yahoo.com", 80000));

//        LittleHiber.insertObject(new Cat("Plyusha", "black"));

//        LittleHiber.insertObject(new Book(14, "Камасутра", 1000));

        LittleHiber.insertObject(new Employee("Java developer", "none", "none", 100000));
    }
}
