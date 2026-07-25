package frc.robot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.function.Function;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveSubsystem;

public class FollowFunctionCommand extends Command {
    private final Function<Double, Double> function;
    private final double duration;
    private double startTime;
    private double ellapsedTime;
    private final double frameRate = 2;
    private final double frameTime = 1 / frameRate;
    
    private List<Pose2d> waypoints;
    private Pose2d prevPoint;
    private SwerveSubsystem swerve;

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
    }

    @Override
    public void execute() {
        
    }

    @Override
    public boolean isFinished() {
        return ellapsedTime >= duration;
    }
}
