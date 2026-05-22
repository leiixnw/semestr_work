package oris.travelcommunity.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import oris.travelcommunity.models.Category;
import oris.travelcommunity.services.CategoryService;

@Component
@RequiredArgsConstructor
public class StringToCategoryConverter implements Converter<String, Category> {

    private final CategoryService categoryService;

    @Override
    public Category convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            Long id = Long.parseLong(source.trim());
            return categoryService.getCategoryById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}