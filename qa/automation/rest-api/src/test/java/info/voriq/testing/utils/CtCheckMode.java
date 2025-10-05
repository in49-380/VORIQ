package info.voriq.testing.utils;

public enum CtCheckMode {
    NONE,       // не проверять вообще
    IF_PRESENT, // проверять только если заголовок присутствует
    REQUIRE     // заголовок ОБЯЗАН быть и должен совпадать
}