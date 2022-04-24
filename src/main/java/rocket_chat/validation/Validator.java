package rocket_chat.validation;

public class Validator {
    public boolean isValid(String input) {
        return !input.isBlank();
    }
}
