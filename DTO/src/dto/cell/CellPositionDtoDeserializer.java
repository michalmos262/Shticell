package dto.cell;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CellPositionDtoDeserializer implements JsonDeserializer<CellPositionDto> {
    @Override
    public CellPositionDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            // Case when it's a string, like "C6"
            String positionStr = json.getAsString();
            return new CellPositionDto(positionStr);  // Convert string to CellPositionDto
        } else if (json.isJsonObject()) {
            // Case when it's an object, like { "row": 6, "column": 3, "positionInSheet": "C6" }
            JsonObject jsonObject = json.getAsJsonObject();
            int row = jsonObject.get("row").getAsInt();
            int column = jsonObject.get("column").getAsInt();
            return new CellPositionDto(row, column);  // Convert object to CellPositionDto
        }
        throw new JsonParseException("Expected a string or object for CellPositionDto but got something else");
    }
}