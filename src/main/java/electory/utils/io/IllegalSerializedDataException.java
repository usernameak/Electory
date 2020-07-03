package electory.utils.io;

import java.io.IOException;

public class IllegalSerializedDataException extends IOException {
    public IllegalSerializedDataException(String message) {
        super(message);
    }
}
