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
            Model model = modelRepository.findByNameIgnoreCaseAndBrand(dto.getName(), brand)
                    .orElseGet(() -> modelRepository.save(new Model(null, dto.getName(), brand)));

            // Year
            Year year = yearRepository.findByYear(Integer.parseInt(dto.getYear()))
                    .orElseGet(() -> yearRepository.save(new Year(null, Integer.parseInt(dto.getYear()),null)));

            // Fuel type
            FuelType fuelType = fuelRepository.findByNameIgnoreCase(dto.getFuelType())
                        .orElseGet(() -> fuelRepository.save(new FuelType(null, dto.getFuelType(), null)));

            // Engine
            Engine engine = engineRepository.findByTypeIgnoreCase(dto.getEngineType())
                    .orElseGet(() -> engineRepository.save(new Engine(null, dto.getEngineType(), fuelType,
                            null, null, 0, null)));

            // Transmission
            Transmission transmission = transmissionRepository.findByMarketingNameIgnoreCase(dto.getTransmissionType())
                    .orElseGet(() -> transmissionRepository.save(new Transmission(
                            null,
                            dto.getGears(),
                            null,
                            null,
                            dto.getTransmissionType(), // marketing_name
                            false,
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
        } //end for
    }
}
