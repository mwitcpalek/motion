package io.j3ff.motion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

class Motion {

  private final int period;
  private final double nominalVelocity;
  private final Deque<Integer> velocityFilter;
  private final int velocityFilterLength;
  private final Deque<Double> accelerationFilter;
  private final int accelerationFilterLength;
  private final int totalInputs;
  private double velocityFilterOutput;
  private double velocityFilterTotal;
  private double accelerationFilterTotal;
  private int iteration;
  private int input;
  private double previousVelocity;
  private double currentVelocity;
  private double currentAcceleration;
  private double elapsedDistance;
  private double elapsedTime;

  Motion(int distance, int maxVelocity, int maxVelocityTime, int maxAccelerationTime, int period) {
    this.period = period;

    nominalVelocity = maxVelocity / 100d;
    double nominalTime = distance / nominalVelocity;
    totalInputs = (int) Math.ceil(nominalTime / period);

    velocityFilterLength = (int) Math.ceil(maxVelocityTime / period);
    velocityFilter = new ArrayDeque<>(velocityFilterLength);
    accelerationFilterLength = (int) Math.ceil(maxAccelerationTime / period);
    accelerationFilter = new ArrayDeque<>(accelerationFilterLength);
  }

  private void iterate() {
    input = iteration++ < totalInputs ? 1 : 0;

    velocityFilter.addLast(input);
    velocityFilterTotal += input;
    if (velocityFilter.size() > velocityFilterLength) {
      velocityFilterTotal -= velocityFilter.removeFirst();
    }
    velocityFilterOutput = velocityFilterTotal / velocityFilterLength;

    accelerationFilter.addLast(velocityFilterOutput);
    accelerationFilterTotal += velocityFilterOutput;
    if (accelerationFilter.size() > accelerationFilterLength) {
      accelerationFilterTotal -= accelerationFilter.removeFirst();
    }

    currentVelocity =
        (velocityFilterTotal + velocityFilterOutput + accelerationFilterTotal)
            / (velocityFilterLength + accelerationFilterLength + 1)
            * nominalVelocity;

    currentAcceleration = (currentVelocity - previousVelocity) / period;
    elapsedDistance += ((previousVelocity + currentVelocity) / 2.0) * period;
    previousVelocity = currentVelocity;

    elapsedTime += period;
  }

  private void outputCSVLine(FileWriter writer) throws IOException {
    writer.append(Double.toString(elapsedTime));
    writer.append(",");
    writer.append(Integer.toString(input * 100000));
    writer.append(",");
    writer.append(Double.toString(velocityFilterOutput * 100000));
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
    int count = totalInputs + velocityFilterLength + accelerationFilterLength;
    try (FileWriter writer = new FileWriter(file)) {
      writer.append("Time (ms),");
      writer.append("Input,");
      writer.append("Velocity Filter Output,");
      writer.append("Velocity,");
      writer.append("Distance,");
      writer.append("Acceleration\n");
      writer.append("0.0,0.0,0.0,0.0,0.0,0.0\n");
      for (int i = 0; i < count; i++) {
        iterate();
        outputCSVLine(writer);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
