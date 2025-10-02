package info.voriq.testing.utils;

import info.voriq.testing.config.ConfigManager;
import info.voriq.testing.CarResolve;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestDataHelper {

    static final Integer MAXNUMBER = 99999;
    static final Integer VALUE_LESS_ZERO = -1;
    static final Integer VALUE_NULL = null;

    public enum CarField {
        BRAND, MODEL, YEAR, ENGINE, TRANSMISSION, WHEEL_DRIVE
    }

    public static CarResolve defaults() {
        return new CarResolve(
                ConfigManager.brandId(),
                ConfigManager.modelId(),
                ConfigManager.yearId(),
                ConfigManager.engineId(),
                ConfigManager.transmissionId(),
                ConfigManager.wheelDriveId()
        );
    }

    public static CarResolve withOverride(Function<String, String> cfg, CarField field, Integer value) {
        Integer brand = ConfigManager.brandId();
        Integer model = ConfigManager.modelId();
        Integer year = ConfigManager.yearId();
        Integer engine = ConfigManager.engineId();
        Integer transmission = ConfigManager.transmissionId();
        Integer wheelDrive = ConfigManager.wheelDriveId();

        switch (field) {
            case BRAND -> brand = value;
            case MODEL -> model = value;
            case YEAR -> year = value;
            case ENGINE -> engine = value;
            case TRANSMISSION -> transmission = value;
            case WHEEL_DRIVE -> wheelDrive = value;
        }
        return new CarResolve(brand, model, year, engine, transmission, wheelDrive);
    }

    public static Stream<Arguments> carResolveNullArgs(Function<String, String> cfg) {
        return Stream.of(CarField.values())
                .map(f -> Arguments.of(Named.of(f.name() + "=null", withOverride(cfg, f, VALUE_NULL))));
    }

    public static Stream<Arguments> carResolveMinusOneArgs(Function<String, String> cfg) {
        return Stream.of(CarField.values())
                .map(f -> Arguments.of(Named.of(f.name() + "=-1", withOverride(cfg, f, VALUE_LESS_ZERO))));
    }

    public static Stream<Arguments> carResolveMAX(Function<String, String> cfg) {
        return Stream.of(CarField.values())
                .map(f -> Arguments.of(Named.of(f.name() + "=MAX", withOverride(cfg, f, MAXNUMBER))));
    }


    public static Stream<Arguments> suffixAndSchema() {
        return Stream.of(
                arguments("objectCarBrands", "brands-schema.json"),
                arguments("objectCarModels", "models-schema.json"),
                arguments("objectCarYears", "years-schema.json"),
                arguments("objectCarEngines", "engines-schema.json"),
                arguments("objectCarTransmissions", "transmissions-schema.json"),
                arguments("objectCarWheelDrive", "wheel_drive-schema.json")
        );
    }
}