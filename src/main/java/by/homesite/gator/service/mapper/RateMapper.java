package by.homesite.gator.service.mapper;

import by.homesite.gator.domain.*;
import by.homesite.gator.service.dto.RateDTO;

import by.homesite.gator.service.dto.UserPropertiesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rate} and its DTO {@link RateDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RateMapper extends EntityMapper<RateDTO, Rate> {

    RateDTO toDto(Rate rate);

    Rate toEntity(Rate rateDTO);

    default Rate fromId(Long id) {
        if (id == null) {
            return null;
        }
        Rate rate = new Rate();
        rate.setId(id);
        return rate;
    }
}
