package hr.algebra.surfspot.service;


import java.util.List;
import java.util.Optional;

public interface BaseService<T, I> {

    List<T> findAll();
    Optional<T> findById(I id);
    T save(T entity);
    void update(T entity);
    void delete(I id);
}
