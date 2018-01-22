package io.j3ff.motion;

import java.util.Arrays;

class JupyterInput {

  MotionInput motion;
  PathfinderInput pathfinder;

  @Override
  public String toString() {
    return "JupyterInput{" + "motionInput=" + motion + '}';
  }

  static class MotionInput {
    int dt;
    int t1;
    int t2;
    double v_prog;
    double dist;

    @Override
    public String toString() {
      return "MotionInput{"
          + "dt="
          + dt
          + ", t1="
          + t1
          + ", t2="
          + t2
          + ", v_prog="
          + v_prog
          + ", dist="
          + dist
          + '}';
    }
  }

  static class PathfinderInput {
    double dt;
    double v_max;
    double a_max;
    double j_max;
    Waypoint[] waypoints;

    @Override
    public String toString() {
      return "PathfinderInput{"
          + "dt="
          + dt
          + ", v_max="
          + v_max
          + ", a_max="
          + a_max
          + ", j_max="
          + j_max
          + ", waypoints="
          + Arrays.toString(waypoints)
          + '}';
    }

    static class Waypoint {
      double x;
      double y;
      double degrees;

      @Override
      public String toString() {
        return "Waypoint{" + "x=" + x + ", y=" + y + ", degrees=" + degrees + '}';
      }
    }
  }
}
