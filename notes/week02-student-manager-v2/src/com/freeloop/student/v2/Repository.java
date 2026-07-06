package com.freeloop.student.v2;

import java.util.List;

public interface Repository<T, ID> {
    boolean add(T entity);

    T findById(ID id);

    List<T> findAll();

    boolean update(T entity);

    boolean deleteById(ID id);
}
