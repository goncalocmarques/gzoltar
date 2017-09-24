package com.gzoltar.core.spectrum;

import com.gzoltar.core.events.IEventListener;
import com.gzoltar.core.model.NodeType;

public class SpectrumBuilder implements IEventListener {

  protected SpectrumImpl spectrum;

  public SpectrumBuilder() {
    resetSpectrum();
  }

  public void resetSpectrum() {
    spectrum = new SpectrumImpl();
  }

  public ISpectrum getSpectrum() {
    return spectrum;
  }

  @Override
  public void endTransaction(String transactionName, boolean[] activity, boolean isError) {
    spectrum.addTransaction(transactionName, activity, isError);
  }

  @Override
  public void endTransaction(String transactionName, boolean[] activity, int hashCode,
      boolean isError) {
    spectrum.addTransaction(transactionName, activity, hashCode, isError);
  }

  @Override
  public void addNode(int id, String name, NodeType type, int parentId) {
    spectrum.getTree().addNode(name, type, parentId);
  }

  @Override
  public void addProbe(int id, int nodeId) {
    spectrum.addProbe(id, nodeId);
  }

  @Override
  public void endSession() {}

}
