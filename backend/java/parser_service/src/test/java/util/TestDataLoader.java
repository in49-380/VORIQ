package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@UtilityClass
public class TestDataLoader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> List<T> loadListFromJson(String resourcePath, Class<T> clazz) {
        try {
            String json = Files.readString(Path.of("src/test/resources/" + resourcePath));
            return objectMapper.readValue(json, new TypeReference<List<T>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data from " + resourcePath, e);
        }
    }

    public static String loadJsonFromFileToString(String resourcePath) {
        try {
            return Files.readString(Path.of("src/test/resources/" + resourcePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data from " + resourcePath, e);
        }
    }
}
