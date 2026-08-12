package frc.robot.commands;

import static edu.wpi.first.units.Units.Meters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.RotationTarget;
import com.pathplanner.lib.path.Waypoint;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class FollowFunctionCommand extends Command {
    private final Function<Double, Double> function;
    private double x = 0;
    private int maxPoints;
    private final double frameRate = 5; // frames per x
    private final double frameTime = 1 / frameRate;
    private double inclusiveDomainStart; // meters
    private double inclusiveDomainEnd;
    private double inclusiveRangeStart;
    private double inclusiveRangeEnd;
    private final Field2d followFunctionField = new Field2d();
    static {
        SmartDashboard.putData("Follow Function Field", new Field2d());
    }


    private final PathConstraints constraints = new PathConstraints(3.0, 3.0, Math.PI, Math.PI);
    
    private List<Pose2d> waypoints;
    private List<Waypoint> finalWaypoints;
    private List<RotationTarget> finalRotations;
    private PathPlannerPath finalPath;
    private Pose2d prevPoint;
    private Command followPathCommand;

    public FollowFunctionCommand(Function<Double, Double> function, double inclusiveDomainStart, double inclusiveDomainEnd, double inclusiveRangeStart, double inclusiveRangeEnd) {
        this.function = function;
        this.maxPoints = -1;
        this.inclusiveDomainStart = inclusiveDomainStart;
        this.inclusiveDomainEnd = inclusiveDomainEnd;
        this.inclusiveRangeStart = inclusiveRangeStart;
        this.inclusiveRangeEnd = inclusiveRangeEnd;
    }

    public FollowFunctionCommand(Function<Double, Double> function, double inclusiveDomainEnd, double inclusiveRangeEnd) {
        this.function = function;
        this.maxPoints = -1;
        this.inclusiveDomainStart = 0;
        this.inclusiveDomainEnd = inclusiveDomainEnd;
        this.inclusiveRangeStart = 0;
        this.inclusiveRangeEnd = inclusiveRangeEnd;
    }

    public FollowFunctionCommand(Function<Double, Double> function, int maxPoints) {
        this.function = function;
        this.maxPoints = maxPoints;
        this.inclusiveDomainStart = -1;
        this.inclusiveDomainEnd = -1;
        this.inclusiveRangeStart = -1;
        this.inclusiveRangeEnd = -1;
    }

    @Override
    public void initialize() {
        this.waypoints = new ArrayList<>();
        this.prevPoint = null;
        this.finalWaypoints = null;
        this.finalRotations = new ArrayList<>();
        this.finalPath = null;
        this.followPathCommand = null;

        while (
            prevPoint == null || 
            maxPoints == -1 && prevPoint.getX() >= inclusiveDomainStart && prevPoint.getX() <= inclusiveDomainEnd && prevPoint.getY() >= inclusiveRangeStart && prevPoint.getY() <= inclusiveRangeEnd ||
            waypoints.size() < maxPoints
        ) {
            double y = function.apply(x);

            Pose2d point = new Pose2d(x, y, Rotation2d.kZero);

            if (prevPoint != null) {
                Rotation2d angle = new Rotation2d(point.getX()-prevPoint.getX(), point.getY()-prevPoint.getY());
                point = new Pose2d(point.getTranslation(), angle);
                int ln = waypoints.size();
                Pose2d prevPoint = waypoints.get(ln-1);
                waypoints.set(waypoints.size()-1, new Pose2d(prevPoint.getTranslation(), angle));
            }

            prevPoint = point;

            waypoints.add(point);
            x += frameTime;
        }

        finalWaypoints = PathPlannerPath.waypointsFromPoses(waypoints);
        for (int i = 0; i < waypoints.size(); i++) {
            finalRotations.add(new RotationTarget((double)(i), waypoints.get(i).getRotation()));
        }

        finalPath = new PathPlannerPath(
            finalWaypoints, 
            finalRotations, 
            List.of(),
            List.of(),
            List.of(),
            constraints,
            null,
            new GoalEndState(0.0, prevPoint.getRotation()),
            false
        );

        followFunctionField.getObject("Path").setPoses(finalPath.getAllPathPoints().stream().map((e) -> new Pose2d(e.position, Rotation2d.kZero)).toList());

        followPathCommand = AutoBuilder.followPath(finalPath);

        followPathCommand.initialize();
    }

    @Override
    public void execute() {
        followPathCommand.execute();
    }

    @Override
    public boolean isFinished() {
        return followPathCommand.isFinished();
    }
}

