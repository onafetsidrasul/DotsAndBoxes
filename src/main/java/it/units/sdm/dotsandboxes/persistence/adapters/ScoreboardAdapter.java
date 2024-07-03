package it.units.sdm.dotsandboxes.persistence.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardAdapter extends TypeAdapter<HashMap<String, Integer>> {

  @Override
  public void write(JsonWriter jsonWriter, HashMap<String, Integer> playerIntegerHashMap) throws IOException {
    jsonWriter.beginObject();
    for (Map.Entry<String, Integer> entry : playerIntegerHashMap.entrySet()) {
      jsonWriter.name(entry.getKey());
      jsonWriter.value(entry.getValue());
    }
    jsonWriter.endObject();
  }

  @Override
  public HashMap<String, Integer> read(JsonReader jsonReader) throws IOException {
    final HashMap<String, Integer> map = new HashMap<>();
    jsonReader.beginObject();
    while (jsonReader.hasNext()) {
      String player = jsonReader.nextName();
      Integer score = jsonReader.nextInt();
      map.put(player, score);
    }
    jsonReader.endObject();
    return map;
  }
}
