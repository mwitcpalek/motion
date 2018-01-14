package io.j3ff.motion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class Motion {

  private final int distance;
  private final int maxVelocity;
  private final int maxVelocityTime;
  private final int maxAccelerationTime;
  private final int period;

  private final double vProg;

  private final MovingAverage velocityFilter;
  private final MovingAverage accerationFilter;

  private final int totalInputs;

  private int iteration;
  private double previousVelocity;
  private double currentVelocity;
  private double currentAcceleration;
  private double elapsedDistance;
  private double elapsedTime;

  Motion(int distance, int maxVelocity, int maxVelocityTime, int maxAccelerationTime, int period) {
    this.distance = distance;
    this.maxVelocity = maxVelocity;
    this.maxVelocityTime = maxVelocityTime;
    this.maxAccelerationTime = maxAccelerationTime;
    this.period = period;

    vProg = maxVelocity / 100d;
    double t = distance / vProg;
    totalInputs = (int) Math.ceil(t / period);

    velocityFilter = new MovingAverage((int) Math.ceil(maxVelocityTime / period));
    accerationFilter = new MovingAverage((int) Math.ceil(maxAccelerationTime / period));
  }

  private void iterate() {
    int input = iteration++ < totalInputs ? 1 : 0;
    velocityFilter.add(input);
    double velocityFilterOutput = velocityFilter.getAverage();
    accerationFilter.add(velocityFilterOutput);

    currentVelocity =
        (velocityFilter.getSum() + velocityFilterOutput + accerationFilter.getSum())
            / (velocityFilter.getSize() + accerationFilter.getSize() + 1)
            * vProg;

    elapsedDistance += ((previousVelocity + currentVelocity) / 2.0) * period;
    currentAcceleration = (currentVelocity - previousVelocity) / period;
    previousVelocity = currentVelocity;

    elapsedTime += period;
  }

  private void outputCSVLine(FileWriter writer) throws IOException {
    writer.append(Double.toString(elapsedTime));
    writer.append(",");
    writer.append(Double.toString(currentVelocity * 100));
    writer.append(",");
    writer.append(Double.toString(elapsedDistance));
    writer.append(",");
    writer.append(Double.toString(currentAcceleration * 100 * 100));
    writer.append("\n");
  }

  void outputCSV(File file) {
    iteration = 0;
    int count = totalInputs + velocityFilter.getSize() + accerationFilter.getSize();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("Time (ms),Velocity,Distance,Acceleration\n0.0,0.0,0.0,0.0\n");
      for (int i = 0; i < count; i++) {
        iterate();
        outputCSVLine(writer);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
