package io.j3ff.motion;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.Waypoint;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Main {

  public static void main(String[] args) {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<JupyterInput> jsonAdapter = moshi.adapter(JupyterInput.class);
    try {
      JupyterInput jupyterInput = jsonAdapter.fromJson(args[0]);
      assert jupyterInput != null;
      if (jupyterInput.motion != null) {
        motion(jupyterInput.motion);
        return;
      }
      if (jupyterInput.pathfinder != null) {
        pathfinder(jupyterInput.pathfinder);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void motion(JupyterInput.MotionInput in) throws IOException {
    Motion motion = new Motion(in.dt, in.t1, in.t2, in.v_prog, in.dist);

    try (FileWriter out = new FileWriter("output.csv")) {
      CSVPrinter printer =
          CSVFormat.DEFAULT.withHeader("Time", "Velocity", "Position", "Acceleration").print(out);
      while (!motion.isFinished()) {
        printer.printRecord(motion.calculate());
      }
    }
  }

  private static void pathfinder(JupyterInput.PathfinderInput in) throws IOException {
    Waypoint points[] = new Waypoint[in.waypoints.length];
    for (int i = 0; i < in.waypoints.length; i++) {
      points[i] =
          new Waypoint(
              in.waypoints[i].x, in.waypoints[i].y, Pathfinder.d2r(in.waypoints[i].degrees));
    }

    Trajectory.Config config =
        new Trajectory.Config(
            Trajectory.FitMethod.HERMITE_CUBIC,
            Trajectory.Config.SAMPLES_HIGH,
            in.dt / 1000,
            in.v_max * 0.3048,
            in.a_max * 0.3048,
            in.j_max * 0.3048);

    Trajectory trajectory = Pathfinder.generate(points, config);

    try (FileWriter out = new FileWriter("trajectory.csv")) {
      CSVPrinter printer =
          CSVFormat.DEFAULT
              .withHeader(
                  "Time", "X", "Y", "Position", "Velocity", "Acceleration", "Jerk", "Heading")
              .print(out);
      printer.printRecord(0, 0, 0, 0, 0, 0, 0, 0);
      double elapsed_time = 0;
      for (Segment seg : trajectory.segments) {
        elapsed_time += seg.dt;
        printer.printRecord(
            elapsed_time * 1000,
            seg.x,
            seg.y,
            seg.position,
            seg.velocity * 3.28084,
            seg.acceleration * 3.28084,
            seg.jerk * 3.28084,
            seg.heading);
      }
    }
  }
}
