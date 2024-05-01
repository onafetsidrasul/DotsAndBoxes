package it.units.sdm.dotsandboxes.persistence.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayersAdapter extends TypeAdapter<List<Player>> {

  @Override
  public void write(JsonWriter jsonWriter, List<Player> players) throws IOException {
    jsonWriter.beginArray();
    for (final Player player : players) {
      jsonWriter.beginObject();
      jsonWriter.name("color").value(player.color().name());
      jsonWriter.name("name").value(player.name());
      jsonWriter.name("hash").value(player.hashCode());
      jsonWriter.endObject();
    }
    jsonWriter.endArray();
  }

  @Override
  public List<Player> read(JsonReader jsonReader) throws IOException {
    final List<Player> players = new ArrayList<>();
    jsonReader.beginArray();
    while (jsonReader.hasNext()) {
      jsonReader.beginObject();

      long hash = -1;
      String name = null;
      Color color = null;
      while (jsonReader.hasNext()) {
        String key;
        if ((key = jsonReader.nextName()).equals("hash")) {
          hash = jsonReader.nextLong();
        } else if (key.equals("name")) {
          name = jsonReader.nextString();
        } else if (key.equals("color")) {
          color = Color.valueOf(jsonReader.nextString());
        }
      }
      players.add(new Player(name, color));
      jsonReader.endObject();
    }
    jsonReader.endArray();
    return players;
  }
}
