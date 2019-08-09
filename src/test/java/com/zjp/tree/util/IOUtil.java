package com.zjp.tree.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class IOUtil {

  public static String loadTextFileFromClasspath(String resourceName) {
    BufferedReader in = null;
    try {
      StringBuilder sb = new StringBuilder();
      InputStream is = IOUtil.class.getResourceAsStream(resourceName);
      if (is == null) {
        throw new IllegalStateException("File not found on classpath");
      }
      in = new BufferedReader(new InputStreamReader(is));
      String line;
      final String lineBreak = System.getProperty("line.separator");
      Pattern noneWords = Pattern.compile("[^\\w\\s]");
      Pattern blacks = Pattern.compile("\\s+");
      while (true) {
        line = in.readLine();
        if (line == null) {
          break;
        }
        line = noneWords.matcher(line).replaceAll("");
        line = blacks.matcher(line).replaceAll(" ");
        line = line.trim();

        line = line.toLowerCase();

        if (!line.equals("")) {
          sb.append(line).append(" ");
        }
      }

      return sb.toString();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load file from classpath: " + resourceName, e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
