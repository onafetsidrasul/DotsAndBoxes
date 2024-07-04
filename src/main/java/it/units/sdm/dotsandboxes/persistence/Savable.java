package it.units.sdm.dotsandboxes.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Base64;

public interface Savable<T> {
  byte[] serialized();
  T restore(byte[] data);

  Base64.Decoder decoder = Base64.getUrlDecoder();
  Base64.Encoder encoder = Base64.getUrlEncoder();
  Gson gson = new GsonBuilder().create();
}
