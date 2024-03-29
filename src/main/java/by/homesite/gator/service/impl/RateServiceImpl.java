package by.homesite.gator.service.impl;

import by.homesite.gator.domain.Rate;
import by.homesite.gator.parser.CurrencyParser;
import by.homesite.gator.repository.RateRepository;
import by.homesite.gator.repository.search.RateSearchRepository;
import by.homesite.gator.service.RateService;
import by.homesite.gator.service.dto.RateDTO;
import by.homesite.gator.service.mapper.RateMapper;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Rate}.
 */
@Service
@Transactional
public class RateServiceImpl implements RateService {

    public static final Map<String, String> CURRENCIES = Map.of("usd", "$", "rub", "₽", "eur", "€");

    private final Logger log = LoggerFactory.getLogger(RateServiceImpl.class);

    private final RateRepository rateRepository;

    private final RateMapper rateMapper;

    private final RateSearchRepository rateSearchRepository;
    private final CurrencyParser currencyParser;

    public RateServiceImpl(
        RateRepository rateRepository,
        RateMapper rateMapper,
        RateSearchRepository rateSearchRepository,
        CurrencyParser currencyParser
    ) {
        this.rateRepository = rateRepository;
        this.rateMapper = rateMapper;
        this.rateSearchRepository = rateSearchRepository;
        this.currencyParser = currencyParser;
    }

    @Override
    public RateDTO save(RateDTO rateDTO) {
        log.debug("Request to save Rate : {}", rateDTO);
        Rate rate = rateMapper.toEntity(rateDTO);
        rate = rateRepository.save(rate);
        RateDTO result = rateMapper.toDto(rate);
        rateSearchRepository.save(rate);
        return result;
    }

    @Override
    public Optional<RateDTO> partialUpdate(RateDTO rateDTO) {
        log.debug("Request to partially update Rate : {}", rateDTO);

        return rateRepository
            .findById(rateDTO.getId())
            .map(
                existingRate -> {
                    rateMapper.partialUpdate(existingRate, rateDTO);

                    return existingRate;
                }
            )
            .map(rateRepository::save)
            .map(
                savedRate -> {
                    rateSearchRepository.save(savedRate);

                    return savedRate;
                }
            )
            .map(rateMapper::toDto);
    }

    /**
     * Get all the rates.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RateDTO> findAll() {
        log.debug("Request to get all Rates");
        return rateRepository.findAll().stream().map(rateMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RateDTO> findOne(Long id) {
        log.debug("Request to get Rate : {}", id);
        return rateRepository.findById(id).map(rateMapper::toDto);
    }

    @Override
    public Optional<RateDTO> findByCode(String code) {
        log.debug("Request to get Rate by code : {}", code);
        return rateRepository.findOneByCode(code.toUpperCase()).map(rateMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Rate : {}", id);
        rateRepository.deleteById(id);
        rateSearchRepository.deleteById(id);
    }

    @Override
    public void fetchRates() {
        try {
            Map<String, String> rates = currencyParser.getCurrency(CURRENCIES.keySet());
            for (String rate : rates.keySet()) {
                Rate rateItem = rateRepository.findOneByCode(rate).orElse(new Rate());
                rateItem.setCode(rate.toUpperCase());
                rateItem.setRate(Float.parseFloat(rates.get(rate)));
                rateItem.setUpdatedAt(ZonedDateTime.now());
                rateItem.setActive(true);
                rateItem.setCreatedAt(ZonedDateTime.now());
                rateItem.setMark(CURRENCIES.get(rate));
                rateRepository.save(rateItem);
            }
        } catch (IOException e) {
            log.error("Can't fetch currency rates {}", e.getMessage());
        }
    }
}
