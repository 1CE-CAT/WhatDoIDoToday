package ru.maks.NauJava.dao;

import org.springframework.data.repository.CrudRepository;
import ru.maks.NauJava.entity.Category;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "category") 
public interface CategoryRepository extends CrudRepository<Category, Long> {
}