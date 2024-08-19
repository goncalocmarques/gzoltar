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
package com.gzoltar.irfl.nlp;

import java.util.List;
import java.util.Objects;

public class ProcessedLine {
    private final String filePath;
    private final int lineNumber;
    private final List<String> tokens;

    public ProcessedLine(String filePath, int lineNumber, List<String> tokens) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.tokens = tokens;
    }

    public String getFilePath() { return filePath; }

    public int getLineNumber() { return lineNumber; }

    public List<String> getTokens() { return tokens; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessedLine that = (ProcessedLine) o;
        return lineNumber == that.lineNumber && filePath.equals(that.filePath);
    }

    @Override
    public int hashCode() { return Objects.hash(filePath, lineNumber); }

    @Override
    public String toString() { return "File: " + filePath + ", Line " + lineNumber + ": " + tokens; }
}
