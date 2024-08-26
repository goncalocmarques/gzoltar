package com.gzoltar.irfl.formulas;

import com.gzoltar.core.spectrum.ISpectrum;
import com.gzoltar.fl.IFormula;


public interface IRFLCombiner extends IFormula {
    public abstract void combine(ISpectrum spectrum);
}
