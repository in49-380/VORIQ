package info.voriq.testing.utils;

import info.voriq.testing.CarResolve;

import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestDataHelper {

    static final Integer MAXNUMBER = 99999;


    public enum CarField {
        BRAND, MODEL, YEAR, ENGINE, TRANSMISSION, WHEEL_DRIVE
    }

    private static Integer cfgInt(Function<String, String> cfg, String key) {
        return Integer.parseInt(cfg.apply(key));
    }

    public static CarResolve defaults(Function<String, String> cfg) {
        return new CarResolve(
                cfgInt(cfg, "brandId"),
                cfgInt(cfg, "modelId"),
                cfgInt(cfg, "yearId"),
                cfgInt(cfg, "engineId"),
                cfgInt(cfg, "transmissionId"),
                cfgInt(cfg, "wheelDriveId")
        );
    }

    public static CarResolve withOverride(Function<String, String> cfg, CarField field, Integer value) {
        // Берём значения прямо из конфига, чтобы не зависеть от геттеров CarResolve
        Integer brand         = cfgInt(cfg, "brandId");
        Integer model         = cfgInt(cfg, "modelId");
        Integer year          = cfgInt(cfg, "yearId");
        Integer engine        = cfgInt(cfg, "engineId");
        Integer transmission  = cfgInt(cfg, "transmissionId");
        Integer wheelDrive    = cfgInt(cfg, "wheelDriveId");

        switch (field) {
            case BRAND        -> brand = value;
            case MODEL        -> model = value;
            case YEAR         -> year = value;
            case ENGINE       -> engine = value;
            case TRANSMISSION -> transmission = value;
            case WHEEL_DRIVE  -> wheelDrive = value;
        }
        return new CarResolve(brand, model, year, engine, transmission, wheelDrive);
    }

    public static Stream<Arguments> carResolveNullArgs(Function<String, String> cfg) {
        return Stream.of(CarField.values())
                .map(f -> Arguments.of(Named.of(f.name() + "=null", withOverride(cfg, f, null))));
    }

    public static Stream<Arguments> carResolveMinusOneArgs(Function<String, String> cfg) {
        return Stream.of(CarField.values())
                .map(f -> Arguments.of(Named.of(f.name() + "=-1", withOverride(cfg, f, -1))));
    }

    public static Stream<Arguments> carResolveMAX(Function<String, String> cfg) {
        return Stream.of(CarField.values())
                .map(f -> Arguments.of(Named.of(f.name() + "=MAX", withOverride(cfg, f, MAXNUMBER))));
    }


    public static Stream<Arguments> validCarResolveArgs(Function<String, String> cfg) {
        return Stream.of(Arguments.of(Named.of("valid", defaults(cfg))));
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