package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.*;
import by.homesite.gator.service.dto.SiteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Site} and its DTO {@link SiteDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface SiteMapper extends EntityMapper<SiteDTO, Site> {
    SiteDTO toDto(Site s);

    @Named("title")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    SiteDTO toDtoTitle(Site site);
}
