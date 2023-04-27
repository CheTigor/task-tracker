package manager.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

import static manager.file.CSVTaskFormat.DATE_TIME_FORMATTER;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            String value = "null";
            jsonWriter.value(value);
            return;
        }
        String value = localDateTime.format(DATE_TIME_FORMATTER);
        jsonWriter.value(value);
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        if (value.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }
}
