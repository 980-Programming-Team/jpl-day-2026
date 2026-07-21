// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
//TODO: Update robot mass (in lbs) in physical propeties .json
package frc.robot;

import org.littletonrobotics.junction.LoggedRobot;

import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.utilities.CustomJoystick;
import swervelib.SwerveDrive;
import swervelib.SwerveModule;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends LoggedRobot {
  private Command m_autonomousCommand;

  private final RobotContainer m_robotContainer;
  private CustomJoystick driverController;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    Logger.recordMetadata("ProjectName", "jpl-day-2026"); // Set your project name

    if (isReal()) { // True if real, false if simulation
      // If running on a real robot, log to a USB stick and publish to NetworkTables
      Logger.addDataReceiver(new WPILOGWriter("/media/sda1/")); 
      Logger.addDataReceiver(new NT4Publisher());
      // Optional: Log power distribution data automatically
      new PowerDistribution(1, ModuleType.kRev); 
    } else {
      // If running in simulation, just publish to NetworkTables (or a local log path)
      Logger.addDataReceiver(new NT4Publisher());
    }

    // Start the logger
    Logger.start();
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
    driverController = (CustomJoystick) m_robotContainer.getDriverController();
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
    driverController.periodic();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(m_autonomousCommand);
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {}

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
    m_robotContainer.speedScale = 1;
    SwerveDrive swerveDrive = m_robotContainer.getSwerve().getSwerveDrive();
    for (SwerveModule module : swerveDrive.getModules()) {
      module.getDriveMotor().setCurrentLimit(60);
      module.getAngleMotor().setCurrentLimit(40);
    }
    swerveDrive.setCosineCompensator(false);
    swerveDrive.setHeadingCorrection(false);
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
