/**
 * Copyright (C) 2020 GZoltar contributors.
 *
 * This file is part of GZoltar.
 *
 * GZoltar is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * GZoltar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with GZoltar. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.gzoltar.irfl;

import com.gzoltar.core.spectrum.ISpectrum;
import com.gzoltar.fl.IFaultLocalization;
import com.gzoltar.fl.IFormula;
import com.gzoltar.sfl.SFL;

import java.util.ArrayList;
import java.util.List;

public class IRFL<F extends IFormula> implements IFaultLocalization<F> {

    private final SFL<F> sfl;
    private final List<F> irflFormulas = new ArrayList<F>();

    public IRFL(final List<F> irflFormulas, final List<F> sflFormulas) {
        sfl = new SFL<>(sflFormulas);
        this.irflFormulas.addAll(irflFormulas);
    }
    @Override
    public void diagnose(ISpectrum spectrum) {
        // TODO, combine rankings here
        // first compute SFL ranking and TopicModeling Ranking and then combine them
    }
}
