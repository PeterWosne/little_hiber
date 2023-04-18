package ru.ermakow;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// LittleHiber -> библиотечка на основе Reflection API, которая позволяет разметить любые классы определенными анннотациями
// и может генерить таблицы для этих классов и инсертить данные в БД
public class LittleHiber {
    private static Connection connection;

    public static void createTable(Class c) {
        var typeMap = getTypeMap();

        // если класс не помечен аннотацией @Table -> таблицу не генерим
        if(!c.isAnnotationPresent(Table.class)) {
            throw new RuntimeException("Class not annotated with @Table");
        }

        //получим имя будущей таблицы(-> не получилось разобраться почему не работает c.getAnnotation(Table.class).name()), потому пока что сделаем костыльно
        String tableName = getTableName(c);

        // получаем список полей класса
        Field[] fields = c.getDeclaredFields();

        // проверим что аннотация @Id одна +
        boolean hasId = false;
        for (Field field : fields) {
            if(field.isAnnotationPresent(Id.class)) {
                if(hasId) {
                    throw new RuntimeException("Only one annotation @Id possible");
                }
                if(field.getType() != int.class) throw new RuntimeException("Only field with type int can be annotated with @Id"); // TODO в дальнейшим расширить для long
                hasId = true;
            }
        }

        // собираем SQL-скрипт для Statement ->
        // create table if not exists cats(id integer primary key autoincrement, name text, color text);
        StringBuilder sb = new StringBuilder("create table if not exists ").append(tableName).append("(");
        for (Field field : fields) {
            if(field.isAnnotationPresent(Column.class)) {
                var columnName = field.getAnnotation(Column.class).name().isBlank() ? field.getName() : field.getAnnotation(Column.class).name();
                sb.append(columnName + " ").append(typeMap.get(field.getType()));
                if(field.isAnnotationPresent(Id.class)) {
                    sb.append(" primary key");
                    if(field.getAnnotation(Id.class).autoincrement()) {
                        sb.append(" autoincrement");
                    }
                }
                sb.append(",");
            }
        }
        sb.setLength(sb.length() - 1);
        sb.append(");");
        String createCommand = sb.toString();

        try {
            connect();
            Statement statement = connection.createStatement();
            statement.executeUpdate(createCommand);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static <T> void insertObject(T o) {
        Class c = o.getClass();

        if(!c.isAnnotationPresent(Table.class)) {
            throw new RuntimeException("Class of this object not annotated with @Table and cannot be inserted into database");
        }

        String tableName = getTableName(c);

        //insert into cats(name, color) values (?,?);
        StringBuilder sb = new StringBuilder("insert into " + tableName + "(");


        //TODO если таблица с autoincrement то это работает
        Field[] fields = c.getDeclaredFields();
        List<Object> values = new LinkedList<>();
        var questionsCount = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            // если у класса аннотация @Id с автоинкрементом то пропускаем ее и не записываем в values
            if(field.isAnnotationPresent(Id.class) && field.getAnnotation(Id.class).autoincrement()) continue;
            var columnName = field.getAnnotation(Column.class).name().isBlank() ? field.getName() : field.getAnnotation(Column.class).name();
            sb.append(columnName).append(",");
            questionsCount++;

            try {
                values.add(field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        sb.setLength(sb.length()-1);
        sb.append(") values (");
        for (int i = 0; i < questionsCount; i++) {
            sb.append("?,");
        }
        sb.setLength(sb.length()-1);
        sb.append(");");
        String insertCommand = sb.toString();


        try {
            connect();
            PreparedStatement ps = connection.prepareStatement(insertCommand);
            int num = 1;
            for (Object value : values) {
                ps.setObject(num++, value);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    // подключаемся к БД
    private static void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:db/littlehiber.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // отключаемся от БД
    private static void disconnect() {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getTableName(Class c) {
        String annotation = c.getAnnotation(Table.class).toString();
        return annotation.substring(24, annotation.length()-2);
    }

    // мапа соответствия java-типов c типами sqlite
    private static Map<Class, String> getTypeMap() {
        Map<Class, String> typeMap = new HashMap<>();
        typeMap.put(int.class, "integer");
        typeMap.put(String.class, "text");
        return typeMap;
    }
}
