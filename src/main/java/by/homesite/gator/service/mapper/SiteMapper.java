package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.*;
import by.homesite.gator.service.dto.SiteDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Site} and its DTO {@link SiteDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface SiteMapper extends EntityMapper<SiteDTO, Site> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    SiteDTO toDto(Site site);

    @Mapping(source = "userId", target = "user")
    Site toEntity(SiteDTO siteDTO);

    default Site fromId(Long id) {
        if (id == null) {
            return null;
        }
        Site site = new Site();
        site.setId(id);
        return site;
    }
}
