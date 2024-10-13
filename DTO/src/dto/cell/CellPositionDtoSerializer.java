package dto.cell;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CellPositionDtoSerializer implements JsonSerializer<CellPositionDto> {
    @Override
    public JsonElement serialize(CellPositionDto src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());  // Convert CellPositionDto to its string representation (e.g., "C6")
    }
}