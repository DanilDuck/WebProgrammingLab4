package ru.duck.laba4.database;

import java.util.List;

public interface Database<T> {

    default void save(T dto) {
        throw new UnsupportedOperationException();
    }

    default void update(T dto) {
        throw new UnsupportedOperationException();
    }

    default void clear() {
        throw new UnsupportedOperationException();
    }

    default T findByKey(Object value) {
        throw new UnsupportedOperationException();
    }

    default List<T> find(String criterion, Object value) {
        throw new UnsupportedOperationException();
    }

    default List<T> getAll() {
        throw new UnsupportedOperationException();
    }

}
