package io.j3ff.motion;

public class MovingAverage {

  private final double[] window;
  private double sum = 0f;
  private int fill;
  private int position;

  MovingAverage(int size) {
    window = new double[size];
  }

  public void add(double number) {

    if (fill == window.length) {
      sum -= window[position];
    } else {
      fill++;
    }

    sum += number;
    window[position++] = number;

    if (position == window.length) {
      position = 0;
    }
  }

  public double getAverage() {
    return sum / fill;
  }

  public double getSum() {
    return sum;
  }

  public int getSize() {
    return window.length;
  }
}
