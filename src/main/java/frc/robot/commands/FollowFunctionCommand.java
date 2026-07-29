package frc.robot.commands;

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
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.SwerveSubsystem;

public class FollowFunctionCommand extends Command {
    private final Function<Double, Double> function;
    private final double duration;
    private double startTime;
    private double ellapsedTime;
    private final double frameRate = 2; // frames per x
    private final double frameTime = 1 / frameRate;

    private final PathConstraints constraints = new PathConstraints(3.0, 3.0, Math.PI, Math.PI);
    
    private List<Pose2d> waypoints;
    private List<Waypoint> finalWaypoints;
    private List<RotationTarget> finalRotations;
    private PathPlannerPath finalPath;
    private Pose2d prevPoint;
    private SwerveSubsystem swerve;
    private Command followPathCommand;

    public FollowFunctionCommand(Function<Double, Double> function, double duration, SwerveSubsystem swerve) {
        this.function = function;
        this.duration = duration;
        this.swerve = swerve;
    }

    @Override
    public void initialize() {
        this.startTime = Timer.getFPGATimestamp();
        this.ellapsedTime = 0;
        this.waypoints = new ArrayList<>();
        this.prevPoint = null;
        this.finalWaypoints = null;
        this.finalRotations = new ArrayList<>();
        this.finalPath = null;
        this.followPathCommand = null;

        while (ellapsedTime < duration) {
            double x = ellapsedTime;
            double y = function.apply(x);

            Pose2d point = new Pose2d(x, y, Rotation2d.kZero);

            if (prevPoint != null) {
                Rotation2d angle = new Rotation2d(prevPoint.getX()-point.getX(), prevPoint.getY()-point.getY());
                point.rotateBy(angle);
            }

            prevPoint = point;

            waypoints.add(point);
            ellapsedTime += frameTime;
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

        System.out.println(
            finalPath.getPathPoses().stream()
            .map(Pose2d::toString)
            .collect(Collectors.joining("\n"))
        );

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

