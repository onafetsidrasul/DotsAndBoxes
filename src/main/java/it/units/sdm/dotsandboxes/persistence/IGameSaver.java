package it.units.sdm.dotsandboxes.persistence;

public interface IGameSaver<T> {
  Savable<T> restoreFromFile(String filename);
  Savable<T> restoreFromData(byte[] data);
  String save(Savable<T> savable);
}
