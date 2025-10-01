package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.request.CarRequestDto;
import com.voriq.parser_service.repository.*;
import com.voriq.parser_service.domain.entity.*;
import com.voriq.parser_service.service.interfaces.CarWriteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarWriteServiceImpl implements CarWriteService {

    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final YearRepository yearRepository;
    private final EngineRepository engineRepository;
    private final FuelTypeRepository fuelRepository;
    private final TransmissionRepository transmissionRepository;
    private final WhillDriveRepository driveRepository;
    private final CarRepository carRepository;


    @Override
    @Transactional
    public void saveCars(List<CarRequestDto> cars) {
        for (CarRequestDto dto : cars) {

            // Brand
            Brand brand = brandRepository.findByNameIgnoreCase(dto.getBrand())
                    .orElseGet(() -> brandRepository.save(new Brand(null, dto.getBrand(), null)));

            // Model
            Model model = modelRepository.findByNameIgnoreCaseAndBrand(dto.getModel(), brand)
                    .orElseGet(() -> modelRepository.save(new Model(null, dto.getModel(), brand)));

            // Year
            Year year = yearRepository.findByYear(dto.getYear())
                    .orElseGet(() -> yearRepository.save(new Year(null, dto.getYear(),null)));

            // Fuel type
            FuelType fuelType;
            if (dto.getFuelType()!=null) {
                fuelType = fuelRepository.findByNameIgnoreCase(dto.getFuelType())
                        .orElseGet(() -> fuelRepository.save(new FuelType(null, dto.getFuelType(), null)));
            } else {
                fuelType = null;
            }

            // Engine
            Engine engine = engineRepository
                    .findByTypeIgnoreCaseAndDisplacementCC(dto.getEngineType(),
                            dto.getDisplacementCC() != null ? dto.getDisplacementCC() : 0)
                    .orElseGet(() -> engineRepository.save(new Engine(
                            null,
                            dto.getEngineType(),
                            fuelType,
                            null,
                            null,
                            dto.getDisplacementCC() != null ? dto.getDisplacementCC() : 0,
                            null)));

            // Transmission
            Transmission transmission = transmissionRepository.findByMarketingNameIgnoreCaseAndGearsAndManual(
                            dto.getMarketingName(),
                            dto.getGears() != null ? dto.getGears() : 0,
                            dto.getManual() != null ? dto.getManual() : false)
                    .orElseGet(() -> transmissionRepository.save(new Transmission(
                            null,
                            dto.getGears() != null ? dto.getGears() : 0,
                            null,
                            null,
                            dto.getMarketingName(),
                            dto.getManual() != null ? dto.getManual() : false,
                            null
                    )));

            // WhillDrive
            WhillDrive whillDrive = driveRepository.findByNameIgnoreCase(dto.getDrive())
                    .orElseGet(() -> driveRepository.save(new WhillDrive(null, dto.getDrive(), null)));

            // Car
            Car car = new Car();
            car.setModel(model);
            car.setEngine(engine);
            car.setYear(year);
            car.setTransmission(transmission);
            car.setWhilldrive(whillDrive);

            carRepository.save(car);
        }
    }
}