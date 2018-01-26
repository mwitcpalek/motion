package io.j3ff.motion;

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
    String toml;
  }
}
