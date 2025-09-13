package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.CarIdDto;
import com.voriq.car_catalog_service.domain.dto.CarResolveRequest;
import com.voriq.car_catalog_service.domain.dto.IdValueResponseDto;
import com.voriq.car_catalog_service.exception_handler.exception.NotFoundException;
import com.voriq.car_catalog_service.exception_handler.exception.ServiceUnavailableException;
import com.voriq.car_catalog_service.repository.interfaces.CarCatalogRepository;
import com.voriq.car_catalog_service.service.interfaces.CarCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for read-only access to the car catalog and for resolving
 * a concrete car identifier based on the user's selections.
 * <p>
 * All repository failures are translated to {@link ServiceUnavailableException} to provide
 * a consistent 503 response at the HTTP layer. Absent entities are reported as
 * {@link NotFoundException} where appropriate (e.g., in {@link #getResolve(CarResolveRequest)}).
 * </p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Fetch brands, models, years, engines, transmissions, and wheel drives.</li>
 *   <li>Resolve a final car ID from a {@link CarResolveRequest} selection chain.</li>
 *   <li>Map infrastructure/data access errors to domain-specific exceptions.</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CarCatalogServiceImpl implements CarCatalogService {

    private final CarCatalogRepository repository;

    /**
     * Returns all brands available in the catalog.
     *
     * @return list of brand id/value pairs
     * @throws ServiceUnavailableException if the repository call fails
     * @author RsLan
     * @since 1.0.0
     */
    @Override
    public List<IdValueResponseDto> findALlBrands() {
        List<IdValueResponseDto> result;
        try {
            result = repository.findALlBrands();
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return result;
    }

    /**
     * Returns all models for a given brand.
     *
     * @param brandId brand identifier
     * @return list of model id/value pairs
     * @throws ServiceUnavailableException if the repository call fails
     * @since 1.0.0
     * @author RsLan
     */
    @Override
    public List<IdValueResponseDto> findModelsByBrand(Long brandId) {
        List<IdValueResponseDto> result;
        try {
            result = repository.findModelsByBrand(brandId);
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return result;
    }

    /**
     * Returns available production years for the given brand and model.
     *
     * @param brandId brand identifier
     * @param modelId model identifier
     * @return list of year id/value pairs
     * @throws ServiceUnavailableException if the repository call fails
     * @since 1.0.0
     * @author RsLan
     */
    @Override
    public List<IdValueResponseDto> findYears(Long brandId, Long modelId) {
        List<IdValueResponseDto> result;
        try {
            result = repository.findYears(brandId, modelId);
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return result;
    }

    /**
     * Returns available engines for the given brand, model, and year.
     *
     * @param brandId brand identifier
     * @param modelId model identifier
     * @param yearId  year identifier
     * @return list of engine id/value pairs
     * @throws ServiceUnavailableException if the repository call fails
     * @since 1.0.0
     * @author RsLan
     */
    @Override
    public List<IdValueResponseDto> findEngines(Long brandId, Long modelId, Long yearId) {
        List<IdValueResponseDto> result;
        try {
            result = repository.findEngines(brandId, modelId, yearId);
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return result;
    }

    /**
     * Returns available transmissions for the given selection.
     *
     * @param brandId  brand identifier
     * @param modelId  model identifier
     * @param yearId   year identifier
     * @param engineId engine identifier
     * @return list of transmission id/value pairs
     * @throws ServiceUnavailableException if the repository call fails
     * @since 1.0.0
     * @author RsLan
     */
    @Override
    public List<IdValueResponseDto> findTransmissions(Long brandId, Long modelId, Long yearId, Long engineId) {
        List<IdValueResponseDto> result;
        try {
            result = repository.findTransmissions(brandId, modelId, yearId, engineId);
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return result;
    }

    /**
     * Returns available wheel-drive options for the given selection.
     *
     * @param brandId          brand identifier
     * @param modelId          model identifier
     * @param yearId           year identifier
     * @param engineId         engine identifier
     * @param transmissionsId  transmission identifier
     * @return list of wheel-drive id/value pairs
     * @throws ServiceUnavailableException if the repository call fails
     * @since 1.0.0
     * @author RsLan
     */
    @Override
    public List<IdValueResponseDto> findWheelDrives(Long brandId, Long modelId, Long yearId, Long engineId, Long transmissionsId) {
        List<IdValueResponseDto> result;
        try {
            result = repository.findWheelDrives(brandId, modelId, yearId, engineId, transmissionsId);
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return result;
    }

    /**
     * Resolves a concrete car ID based on the full selection chain.
     *
     * @param req selection DTO containing brand, model, year, engine, transmission, and wheel drive IDs
     * @return {@link CarIdDto} wrapping the resolved car identifier
     * @throws NotFoundException           if no car matches the provided selection
     * @throws ServiceUnavailableException if the repository call fails
     * @since 1.0.0
     * @author RsLan
     */
    @Override
    public CarIdDto getResolve(CarResolveRequest req) {
        Optional<Long> result;
        try {
            result = repository.getResolve(
                    req.getBrandId(),
                    req.getModelId(),
                    req.getYearId(),
                    req.getEngineId(),
                    req.getTransmissionId(),
                    req.getWheelDriveId());
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        Long carId = result.orElseThrow(() -> new NotFoundException("Car not found"));
        return new CarIdDto(carId);
    }
}
