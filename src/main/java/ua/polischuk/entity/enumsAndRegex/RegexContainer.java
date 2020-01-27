package ua.polischuk.entity.enumsAndRegex;

public interface RegexContainer {
        String NAME_REGEX = "^[a-zA-Z]+$";
        String NAME_REGEX_RU = "^[а-яёА-ЯЁ]+";
        String EMAIL_REGEX = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
        String NOFQ_REGEX = "^[1-9][0-9]?$|^100$";
        String DIF_REGEX = "^(?:[1-9]|0[1-9]|10)$";
        String TL_REGEX = "[0-9]{1,5}$";

}
