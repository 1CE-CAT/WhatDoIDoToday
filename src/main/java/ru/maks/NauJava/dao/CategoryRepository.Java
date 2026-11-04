package ru.maks.NauJava.dao;

import org.springframework.data.repository.CrudRepository;
import ru.maks.NauJava.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}