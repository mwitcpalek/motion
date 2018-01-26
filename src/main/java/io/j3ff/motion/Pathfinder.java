package io.j3ff.motion;

import com.moandjiezana.toml.Toml;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.Waypoint;
import java.util.ArrayList;
import java.util.List;

public class Pathfinder {

  private final Trajectory.Config config;
  private final Waypoint[] waypoints;

  public Pathfinder(String tomlString) {
    Toml toml = new Toml().read(tomlString);
    config = toml.to(Trajectory.Config.class);

    List<Toml> waypointTomlArray = toml.getTables("waypoints");
    waypoints = new Waypoint[waypointTomlArray.size()];
    for (int i = 0; i < waypoints.length; i++) {
      Toml wp = waypointTomlArray.get(i);
      waypoints[i] =
          new Waypoint(wp.getDouble("x"), wp.getDouble("y"), Math.toRadians(wp.getDouble("angle")));
    }
  }

  public List<List<Double>> calculate() {
    Trajectory trajectory = jaci.pathfinder.Pathfinder.generate(waypoints, config);
    List<List<Double>> output = new ArrayList<>();
    for (Segment s : trajectory.segments) {
      List<Double> row = new ArrayList<>();
      output.add(row);
      row.add(s.dt);
      row.add(s.x);
      row.add(s.y);
      row.add(s.position);
      row.add(s.velocity);
      row.add(s.acceleration);
      row.add(s.jerk);
      row.add(s.heading);
    }
    return output;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("[");
    for (Waypoint wp : waypoints) {
      builder
          .append("WayPoint{x=")
          .append(wp.x)
          .append(", y=")
          .append(wp.y)
          .append(", angle=")
          .append(Math.toDegrees(wp.angle))
          .append("}, ");
    }
    builder.delete(builder.length() - 2, builder.length());
    builder.append("]");
    return "PathController{"
        + "config=Trajectory.Config{fit="
        + config.fit
        + ", dt="
        + config.dt
        + ", samples="
        + config.sample_count
        + ", max_velocity="
        + config.max_velocity
        + ", max_acceleration="
        + config.max_acceleration
        + ", max_jerk="
        + config.max_jerk
        + "}, waypoints="
        + builder.toString()
        + '}';
  }
}
