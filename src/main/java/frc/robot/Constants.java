// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rectangle2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
//import com.pathplanner.lib.util.PIDConstants;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;

import static edu.wpi.first.units.Units.*;

import java.io.IOException;
import java.util.Optional;

import com.pathplanner.lib.config.RobotConfig;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import swervelib.math.Matter;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean constants. This
 * class should not be used for any other purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants
{
  public static final double ROBOT_MASS = (148 - 20.3) * 0.453592; // 32lbs * kg per pound
  public static final Matter CHASSIS = new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS);
  public static final double LOOP_TIME = 0.13; //s, 20ms + 110ms sprk max velocity lag
  public static final double MAX_SPEED = Units.feetToMeters(15.1);
  public static final Field2d FIELD = new Field2d();
  
  public static Optional<RobotConfig> ROBOT_CONFIG = Optional.empty();
  
  // Maximum speed of the robot in meters per second, used to limit acceleration.

  public static final class CAN
  {

    //COLLECTOR
    public static final int index = 13;
    public static final int collect = 14;

    //SHOOTER
    public static final int flywheelRightBot = 19;
    public static final int flywheelLeftTop = 17;
    public static final int angleRight = 18;
    public static final int angleLeft = 16;

  }

  public static final class AutoConstants
  {
    // public static final PIDConstants TRANSLATION_PID = new PIDConstants(0.7, 0, 0);
    // public static final PIDConstants ANGLE_PID       = new PIDConstants(0.4, 0, 0.01);
  }

  public static final class DrivebaseConstants
  {

    // Hold time on motor brakes when disabled
    public static final double WHEEL_LOCK_TIME = 10; // seconds
    public static final Distance ROBOT_WIDTH_NO_BUMPERS = Inches.of(29);
    public static final Distance ROBOT_BUMPER_THICKNESS = Inches.of(3);
    public static final Distance ROBOT_WIDTH = Inches.of(34); // added 2 inches to account for gaps between bumpers and frame
   
    public static final Distance BOUNDS_PADDING = ROBOT_WIDTH.times(0.5).plus(
      Inches.of(3)
    );
    public static final Distance BOUNDS_WIDTH = Feet.of(10);
    public static final Distance BOUNDS_HEIGHT = Feet.of(10);

    public static final Rectangle2d ROBOT_BOUNDS = new Rectangle2d(
        new Translation2d(BOUNDS_PADDING, BOUNDS_PADDING),
        new Translation2d(BOUNDS_HEIGHT.minus(BOUNDS_PADDING), BOUNDS_WIDTH.minus(BOUNDS_PADDING))
    );
    public static final FieldObject2d ROBOT_BOUNDS_FIELD = FIELD.getObject("Bounds Area");
    public static final Time BOUNDS_LOOK_TIME = Seconds.of(0.15);

    static {
      ROBOT_BOUNDS_FIELD.setPoses(
        new Pose2d(BOUNDS_PADDING, BOUNDS_PADDING, Rotation2d.fromDegrees(0)), 
        new Pose2d(BOUNDS_HEIGHT.minus(BOUNDS_PADDING), BOUNDS_PADDING, Rotation2d.fromDegrees(0)),
        new Pose2d(BOUNDS_HEIGHT.minus(BOUNDS_PADDING), BOUNDS_WIDTH.minus(BOUNDS_PADDING), Rotation2d.kZero),
        new Pose2d(BOUNDS_PADDING, BOUNDS_WIDTH.minus(BOUNDS_PADDING), Rotation2d.fromDegrees(0))
      );
      SmartDashboard.putData("Bounds/Bounds Field", FIELD);
    }
  }

  public static class DriveTeamConstants
  {

    public static final int driver = 0;

    public static final int operator = 1;

    public static final int prajBox = 2;

  }

  public static class OperatorConstants
  {
    public static final int k_driverControllerPort = 0;
    // Joystick Deadband
    public static final double k_deadband = 0.2; 
    //public static final double TURN_CONSTANT = 6;
  }
}
