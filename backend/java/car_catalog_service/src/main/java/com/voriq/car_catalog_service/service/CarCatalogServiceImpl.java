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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarCatalogServiceImpl implements CarCatalogService {

    private CarCatalogRepository repository;
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

    @Override
    public List<IdValueResponseDto> findDriveLayouts(Long brandId, Long modelId, Long yearId, Long engineId, Long transmissionsId) {
        List<IdValueResponseDto> result;
        try {
            result = repository.findDriveLayouts(brandId, modelId, yearId, engineId, transmissionsId);
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return result;
    }

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
                    req.getDriveLayoutId());
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        Long carId = result.orElseThrow(()->new NotFoundException("Car not found"));
        return new CarIdDto(carId);
    }
}
