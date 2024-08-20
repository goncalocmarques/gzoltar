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
package com.gzoltar.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class FileUtils {

  /**
   * Find all files in a directory that satisfy a particular extension.
   * 
   * @param dir top-level directory to search for
   * @param ext extension to search for
   * @param recursively if <code>true</code> search is performed recursively
   * @return a {@link java.util.List} of all files found
   */
  public static List<File> listFiles(final File dir, final String ext, final boolean recursively) {
    List<File> files = new ArrayList<File>();

    for (File file : dir.listFiles()) {
      if (file.isDirectory() && recursively) {
        files.addAll(listFiles(file, ext, recursively));
      } else if (file.isFile() && file.getName().endsWith(ext)) {
        files.add(file);
      }
    }

    return files;
  }

  /**
   * Returns a list with all the lines in a given file.
   *
   * @param path of the file
   * @return a {@link java.util.List} of all lines in the file
   * @throws FileNotFoundException
   */
  public static List<String> loadFileByLine(String path) throws FileNotFoundException {
    File file = new File(path);
    List<String> lines = new ArrayList<>();
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        lines.add(line);
      }
    }
    return lines;
  }

}
