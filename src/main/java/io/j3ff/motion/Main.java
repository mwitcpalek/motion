package io.j3ff.motion;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Main {

  public static void main(String[] args) {
    int i = 0;
    int dt = Integer.valueOf(args[i++]);
    int t1 = Integer.valueOf(args[i++]);
    int t2 = Integer.valueOf(args[i++]);
    double v_prog = Double.valueOf(args[i++]);
    double dist = Double.valueOf(args[i++]);
    Motion motion = new Motion(dt, t1, t2, v_prog, dist);

    try (FileWriter out = new FileWriter("output.csv")) {
      CSVPrinter printer =
          CSVFormat.DEFAULT.withHeader("Time", "Velocity", "Position", "Acceleration").print(out);
      while (!motion.isFinished()) {
        printer.printRecord(motion.calculate());
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
