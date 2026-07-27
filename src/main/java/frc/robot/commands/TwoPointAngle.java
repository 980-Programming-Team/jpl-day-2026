package frc.robot.commands;

import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.subsystems.SwerveSubsystem;
import swervelib.SwerveInputStream;

public class TwoPointAngle extends Command {
    Translation2d watchpoint;
    Translation2d currentPoint;
    SwerveSubsystem swerve;
    double threshold = 0.2; //degrees
    boolean left;

    public static Translation2d rotationVec = new Translation2d(0, 0);
    public static boolean aim = false;
    public static SwerveInputStream driveTargetAngle = Robot.m_robotContainer.driveAngularVelocity.copy()
        .withControllerHeadingAxis(() -> -rotationVec.getX(), () -> -rotationVec.getY())
        .headingWhile(()->aim);

    public TwoPointAngle(SwerveSubsystem swerve, boolean left) {
        this.swerve = swerve;
        this.left = left;
    }

    public boolean isAiming(double threshold, Rotation2d target, Rotation2d current) {
        double rawDiff = target.minus(current).getDegrees();
        double diff = Math.abs(rawDiff);
        return diff <= threshold;
    }

    public void initialize() {
        watchpoint = swerve.getPose().getTranslation();
        aim = true;
    }

    public void execute() {
        Translation2d currentPoint = swerve.getPose().getTranslation();
        rotationVec = watchpoint.minus(currentPoint);
        Rotation2d rotation = rotationVec.getAngle();
    }

    public void end(boolean interrupted) {
        swerve.resetOdometryAgainstBounds(left);
        aim = false;
    }
}
