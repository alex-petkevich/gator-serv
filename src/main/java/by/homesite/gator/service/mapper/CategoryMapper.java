package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.*;
import by.homesite.gator.service.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = { SiteMapper.class })
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {
    @Mapping(source = "site.id", target = "siteId")
    @Mapping(source = "site.title", target = "siteTitle")
    CategoryDTO toDto(Category category);

    @Mapping(source = "siteId", target = "site.id")
    Category toEntity(CategoryDTO categoryDTO);

    default Category fromId(Long id) {
        if (id == null) {
            return null;
        }
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
