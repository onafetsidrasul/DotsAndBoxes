package it.units.sdm.dotsandboxes.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.units.sdm.dotsandboxes.GameSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class JsonGameSaver implements IGameSaver<GameSession> {

  private static final SimpleDateFormat timeFormatter =
          new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
  private static final Base64.Decoder decoder = Base64.getUrlDecoder();
  private static final Base64.Encoder encoder = Base64.getUrlEncoder();
  private static final Logger logger = Logger.getLogger("JsonGameSaver");
  private static final Gson gson = new GsonBuilder().create();

  public record SavedGame(long timestamp, String className, String data) {}

  public JsonGameSaver() {
  }

  @Override
  public String save(Savable<GameSession> savable) {
    final String filename = "saves/gamesave_" + timeFormatter.format(new Date()) + ".json";
    try (final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename))) {
      final String gameData = new String(
              encoder.encode(savable.save()), StandardCharsets.UTF_8);
      final String payload = gson.toJson(new SavedGame(
              new Date().getTime(), savable.getClass().getName(), gameData));
      bos.write(payload.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.warning(String.format("Could not save game session to %s: %s", filename, e));
      return null;
    }
    return filename;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Savable<GameSession> restoreFromData(byte[] data) {
    final SavedGame savedGame = gson.fromJson(new String(data), SavedGame.class);
    final Savable<GameSession> savable;
    try {
      savable = (Savable<GameSession>) Class.forName(savedGame.className)
              .getConstructor()
              .newInstance();
      return savable.restore(decoder.decode(savedGame.data));
    } catch (Exception e) {
      logger.warning(String.format("Could not restore game session from byte array: %s", e));
      return null;
    }
  }

  @Override
  public Savable<GameSession> restoreFromFile(String filename) {
    try (final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename))) {
      return restoreFromData(bis.readAllBytes());
    } catch (IOException e) {
      logger.warning(String.format("Could not restore game session from %s: %s", filename, e));
      return null;
    }
  }
}
