package io.j3ff.motion;

import java.io.File;

public class Main {

  public static void main(String[] args) {
    Motion motion =
        new Motion(
            Integer.valueOf(args[0]),
            Integer.valueOf(args[1]),
            Integer.valueOf(args[2]),
            Integer.valueOf(args[3]),
            Integer.valueOf(args[4]));

    motion.outputCSV(new File("output.csv"));
  }
}
