package com.thomosim.consentcoin.Persistence;

import java.io.Serializable;
import java.util.List;

public interface DAOInterface <T extends Serializable> {
    List<T> getAll();

    T findById(int id);

    void insert(T entity);

    void update(T entity);

    void delete(T entity);

    void deleteAll();
}
