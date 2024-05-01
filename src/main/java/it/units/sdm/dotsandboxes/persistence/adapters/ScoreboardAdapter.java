package it.units.sdm.dotsandboxes.persistence.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.units.sdm.dotsandboxes.core.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardAdapter extends TypeAdapter<HashMap<Player, Integer>> {

  @Override
  public void write(JsonWriter jsonWriter, HashMap<Player, Integer> playerIntegerHashMap) throws IOException {
    jsonWriter.beginObject();
    for (Map.Entry<Player, Integer> entry : playerIntegerHashMap.entrySet()) {
      jsonWriter.name(entry.getKey().id());
      jsonWriter.value(entry.getValue());
    }
    jsonWriter.endObject();
  }

  @Override
  public HashMap<Player, Integer> read(JsonReader jsonReader) throws IOException {
    final HashMap<Player, Integer> map = new HashMap<>();
    jsonReader.beginObject();
    while (jsonReader.hasNext()) {
      Player player = new Player(jsonReader.nextName(), null, null);
      Integer score = jsonReader.nextInt();
      map.put(player, score);
    }
    jsonReader.endObject();
    return map;
  }
}
