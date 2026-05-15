package hr.algebra.surfspot.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, I> {
    Optional<T> findById(I id);
    List<T> findAll();
    T save(T entity);
    T update(T entity);
    void delete(I id);
}
