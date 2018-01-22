package io.j3ff.motion;

// Definitions, from https://www.chiefdelphi.com/forums/showpost.php?p=1204107&postcount=18
//
//    dt  = iteration time, loop period in ms
//    t1  = time for first filter, in ms
//    t2  = time for second filter, in ms
//   fl1  = filter 1 window length. fl1 = roundup(t1/dt)
//   fl2  = filter 2 window length. fl2 = roundup(t2/dt)
// v_prog = desired max speed, ft/sec
//  dist  = travel distance, ft
//    t4  = time to get to destination, in ms, at vProg. t4 = dist/vProg
//     n  = number of inputs to filter. n = roundup(t4/dt)

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

class Motion {

  private final int dt;
  private final double v_prog;
  private final int[] f1;
  private final double[] f2;
  private final int n;
  private int iteration;

  private double prev_vel;
  private double prev_pos;

  public Motion(int dt, int t1, int t2, double v_prog, double dist) {
    this.dt = dt;
    this.v_prog = v_prog;
    f1 = new int[(int) Math.ceil((double) t1 / dt)];
    f2 = new double[(int) Math.ceil((double) t2 / dt)];

    double t4 = dist / v_prog * 1000;
    n = (int) Math.ceil(t4 / dt);

    System.out.println("t4 = " + t4);
    System.out.println("n = " + n);
    System.out.println("f1.length = " + f1.length);
    System.out.println("f2.length = " + f2.length);
  }

  public boolean isFinished() {
    return iteration == n + f1.length + f2.length + 1;
  }

  public List<Double> calculate() {
    int input = iteration == 0 || iteration > n ? 0 : 1;

    f1[iteration % f1.length] = input;
    double f1_out = (double) IntStream.of(f1).sum() / f1.length;
    f2[iteration % f2.length] = f1_out;
    double f2_out = DoubleStream.of(f2).sum() / f2.length;
    double curr_vel = f2_out * v_prog;
    double curr_pos = (((prev_vel + curr_vel) / 2) * dt) / 1000 + prev_pos;
    double curr_acc = (curr_vel - prev_vel) / ((double) dt / 1000);
    prev_vel = curr_vel;
    prev_pos = curr_pos;

    // output
    List<Double> output = new ArrayList<>();
    output.add((double) iteration * dt);
    output.add(curr_vel);
    output.add(curr_pos);
    output.add(curr_acc);
    iteration++;
    return output;
  }
}
