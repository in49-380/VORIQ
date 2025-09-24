import java.io.*;
import java.util.*;

public class GenerateEmails {

    private static final int TOTAL_COUNT = 1000;
    private static final int VALID_COUNT = 800;
    private static final int INVALID_COUNT = 200;

    private static final int MAX_TOTAL_LENGTH = 255;
    private static final int MAX_LOCAL_LENGTH = 64;

    private static final List<String> DOMAINS = Arrays.asList(
        "example.com", "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
        "test.io", "my-company.org", "mail.ru", "company.co.uk", "com.ua",
        "a.io", "x.io", "i.ua", "de", "localhost", "192.168.1.1", "sub.domain.org"
    );

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("test_emails_1000.csv"))) {
            // Заголовок
            writer.println("email,expected_status,description");

            // Генерируем 800 валидных email
            for (int i = 0; i < VALID_COUNT; i++) {
                String email = generateValidEmail();
                if (email != null && isValidEmailRfc(email)) {
                    writer.printf("%s,201,\"Auto-generated valid\"%n", email);
                }
            }

            // Генерируем 200 невалидных email
            for (int i = 0; i < INVALID_COUNT; i++) {
                String email = generateInvalidEmail();
                writer.printf("%s,400,\"Auto-generated invalid\"%n", escapeCsv(email));
            }

            System.out.println("✅ Файл 'test_emails_1000.csv' успешно создан с " + TOTAL_COUNT + " email.");
        } catch (IOException e) {
            System.err.println("❌ Ошибка при записи файла: " + e.getMessage());
        }
    }

    // Генерация случайной строки заданной длины
    private static String randomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Генерация валидного email
    private static String generateValidEmail() {
        String domain = DOMAINS.get(RANDOM.nextInt(DOMAINS.size()));
        int domainLength = domain.length() + 1; // +1 для '@'
        int maxLocalLength = Math.min(MAX_LOCAL_LENGTH, MAX_TOTAL_LENGTH - domainLength);
        if (maxLocalLength < 1) return null;

        int localLength = 1 + RANDOM.nextInt(maxLocalLength); // 1..max
        String local = generateValidLocalPart(localLength);
        return local + "@" + domain;
    }

    // Генерация валидной локальной части
    private static String generateValidLocalPart(int length) {
        if (length == 0) return "x";
        StringBuilder sb = new StringBuilder();
        String allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-";
        String firstLastSafe = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < length; i++) {
            char c;
            if (i == 0 || i == length - 1) {
                c = firstLastSafe.charAt(RANDOM.nextInt(firstLastSafe.length()));
            } else {
                c = allowed.charAt(RANDOM.nextInt(allowed.length()));
            }
            // Избегаем ".."
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '.' && c == '.') {
                i--; // повторить
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    // Простая проверка email по RFC (упрощённая)
    public static boolean isValidEmailRfc(String email) {
        if (email == null || email.isEmpty() || email.length() > MAX_TOTAL_LENGTH) {
            return false;
        }

        int atIndex = email.lastIndexOf('@');
        if (atIndex <= 0 || atIndex != email.indexOf('@') || atIndex == email.length() - 1) {
            return false;
        }

        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        if (local.length() > MAX_LOCAL_LENGTH) return false;
        if (local.startsWith(".") || local.endsWith(".")) return false;
        if (local.contains("..")) return false;
        if (domain.startsWith(".") || domain.endsWith(".")) return false;
        if (domain.contains("..")) return false;
        if (!domain.contains(".")) return false; // нет TLD

        // Простые регулярки
        return local.matches("^[a-zA-Z0-9._-]+$") &&
               domain.matches("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{1,}$");
    }

    // Генерация невалидного email
    private static String generateInvalidEmail() {
        int type = RANDOM.nextInt(10);
        switch (type) {
            case 0: return "." + randomString(5) + "@" + randomDomain(); // starts with dot
            case 1: return randomString(5) + ".." + randomString(3) + "@" + randomDomain(); // double dot
            case 2: return randomString(5) + "@." + randomDomain(); // domain starts with dot
            case 3: return randomString(5) + "@" + randomDomain() + "."; // domain ends with dot
            case 4: return randomString(5) + "@domain..com"; // double dot in domain
            case 5: return randomString(70) + "@short.com"; // local > 64
            case 6: return randomString(250) + "@x.com"; // total > 255
            case 7: return randomString(5) + "@"; // no domain
            case 8: return "@" + randomDomain(); // no local
            case 9: return ""; // empty
            default: return "invalid@"; // fallback
        }
    }

    private static String randomDomain() {
        return DOMAINS.get(RANDOM.nextInt(DOMAINS.size()));
    }

    // Экранирование кавычек для CSV
    private static String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}