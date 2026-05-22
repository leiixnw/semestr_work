package oris.travelcommunity.services;

import oris.travelcommunity.models.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
}