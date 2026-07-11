// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

<<<<<<< HEAD
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
=======
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
>>>>>>> 2df575ca02422bdb5feadc7a339e6a239e1a28fd
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
<<<<<<< HEAD
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.DriveTeamConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.CollectCommand;
import frc.robot.commands.IndexCommand;
import frc.robot.commands.IndexToShooter;
import frc.robot.commands.OuttakeCommand;
import frc.robot.subsystems.AmpShooter;
import frc.robot.subsystems.Collector;
import frc.robot.subsystems.PhotonFinder;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.SwerveSubsystem;

import java.io.File;

import com.pathplanner.lib.auto.AutoBuilder;
=======
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.SwerveSubsystem;
import java.io.File;
import swervelib.SwerveInputStream;
>>>>>>> 2df575ca02422bdb5feadc7a339e6a239e1a28fd

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{

<<<<<<< HEAD
  // The robot's subsystems and commands are defined here...
  private final SwerveSubsystem drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
                                                                         "swerve"));

  private final Shooter shooter = new Shooter();
  public final static Collector collector = new Collector();
  private final AmpShooter ampShooter = new AmpShooter();
  private final PhotonFinder finder = new PhotonFinder();


  private final SendableChooser<Command> autoChooser;


      // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController driver = new CommandXboxController(DriveTeamConstants.driver);

  private final CommandXboxController operator = new CommandXboxController(DriveTeamConstants.operator);
=======
  // Replace with CommandPS4Controller or CommandJoystick if needed
  final CommandXboxController driverXbox = new CommandXboxController(0);
  // The robot's subsystems and commands are defined here...
  private final SwerveSubsystem drivebase  = new SwerveSubsystem(
    new File(Filesystem.getDeployDirectory(),"swerve")
  );

  // Establish a Sendable Chooser that will be able to be sent to the SmartDashboard, allowing selection of desired auto
  private final SendableChooser<Command> autoChooser;

  /**
   * Converts driver input into a field-relative ChassisSpeeds that is controlled by angular velocity.
   */
  SwerveInputStream driveAngularVelocity = SwerveInputStream.of(
    drivebase.getSwerveDrive(),
    () -> driverXbox.getLeftY() * -1,
    () -> driverXbox.getLeftX() * -1)
    .withControllerRotationAxis(driverXbox::getRightX)
    .deadband(Constants.OperatorConstants.k_deadband)
    .scaleTranslation(0.8)
    .allianceRelativeControl(true);

  /**
   * Clone's the angular velocity input stream and converts it to a fieldRelative input stream.
   */
  SwerveInputStream driveDirectAngle = driveAngularVelocity.copy()
    .withControllerHeadingAxis(
      driverXbox::getRightX,
      driverXbox::getRightY)
      .headingWhile(true);

  /**
   * Clone's the angular velocity input stream and converts it to a robotRelative input stream.
   */
  SwerveInputStream driveRobotOriented = driveAngularVelocity.copy()
    .robotRelative(true)
    .allianceRelativeControl(false);

  SwerveInputStream driveAngularVelocityKeyboard = SwerveInputStream.of(
    drivebase.getSwerveDrive(),
    () -> -driverXbox.getLeftY(),
    () -> -driverXbox.getLeftX())
    .withControllerRotationAxis(() -> driverXbox.getRawAxis(2))
    .deadband(Constants.OperatorConstants.k_deadband)
    .scaleTranslation(0.8)
    .allianceRelativeControl(true);
  // Derive the heading axis with math!
  SwerveInputStream driveDirectAngleKeyboard = driveAngularVelocityKeyboard.copy()
    .withControllerHeadingAxis(() -> Math.sin(
        driverXbox.getRawAxis(2) * Math.PI) * (Math.PI * 2),
        () -> Math.cos(
          driverXbox.getRawAxis(
            2) * Math.PI) * (Math.PI * 2)
    )
  .headingWhile(true)
  .translationHeadingOffset(true)
  .translationHeadingOffset(Rotation2d.fromDegrees(0));
>>>>>>> 2df575ca02422bdb5feadc7a339e6a239e1a28fd

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer()
  {
    // Configure the trigger bindings
<<<<<<< HEAD
    registerCommands();
    configurePathPlanner();
    configureBindings();

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto", autoChooser);

    // Applies deadbands and inverts controls because joysticks
    // are back-right positive while robot
    // controls are front-left positive
    // left stick controls translation
    // right stick controls the angular velocity of the robot
    Command driveFieldOrientedAnglularVelocity = drivebase.driveCommand(
        () -> MathUtil.applyDeadband(-driver.getLeftY(), OperatorConstants.LEFT_Y_DEADBAND),
        () -> MathUtil.applyDeadband(-driver.getLeftX(), OperatorConstants.LEFT_X_DEADBAND),
        () -> -driver.getRightX());

    Command driveFieldOrientedDirectAngleSim = drivebase.simDriveCommand(
        () -> MathUtil.applyDeadband(-driver.getLeftY(), OperatorConstants.LEFT_Y_DEADBAND),
        () -> MathUtil.applyDeadband(-driver.getLeftX(), OperatorConstants.LEFT_X_DEADBAND),
        () -> -driver.getRightX());

    drivebase.setDefaultCommand(
        !RobotBase.isSimulation() ? driveFieldOrientedAnglularVelocity : driveFieldOrientedDirectAngleSim);

    shooter.setDefaultCommand(Commands.run(
      () -> shooter.tiltShooter(operator.getRightY()),
      shooter
    ));

    // //ALLOWS POSE2D TO GO ON SMARTDASHBOARD 
    // //TODO NEEDS TO RUN ENTIRE TIME WHILST IN SIMULATION
    // Field2d field2d = new Field2d();
    // Pose2d pose = drivebase.getPose();
    // field2d.setRobotPose(pose);

    // SmartDashboard.putData("2D Pose" , field2d);
    
  }

  public void configurePathPlanner() 
  {



      drivebase.setupPathPlanner();

  }

  private void registerCommands()
  {

          // NamedCommands GO HERE 
      // Example: NamedCommands.registerCommand("ShootNote", new Shooter());

    
=======
    configureBindings();
    DriverStation.silenceJoystickConnectionWarning(true);
    
    //Create the NamedCommands that will be used in PathPlanner
    NamedCommands.registerCommand("test", Commands.print("I EXIST"));

    //Have the autoChooser pull in all PathPlanner autos as options
    autoChooser = AutoBuilder.buildAutoChooser();

    //Set the default auto (do nothing) 
    autoChooser.setDefaultOption("Do Nothing", Commands.none());

    //Add a simple auto option to have the robot drive forward for 1 second then stop
    autoChooser.addOption("Drive Forward", drivebase.driveForward().withTimeout(1));

    //Put the autoChooser on the SmartDashboard
    SmartDashboard.putData("Auto Chooser", autoChooser);
>>>>>>> 2df575ca02422bdb5feadc7a339e6a239e1a28fd
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary predicate, or via the
   * named factories in {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */
  private void configureBindings()
  {
<<<<<<< HEAD

    driver.a().onTrue((Commands.runOnce(drivebase::zeroGyro)));
    operator.rightBumper().whileTrue(new CollectCommand());
    operator.rightTrigger().whileTrue(new IndexToShooter());
    
    // operator.leftBumper().whileTrue(new OuttakeCommand());
    // operator.rightBumper().whileTrue((Commands.runOnce(collector::intake))); //indexIntoShooter
    // operator.leftBumper().whileTrue((Commands.runOnce(collector::off)));
    // // driverXbox.x().whileTrue(Commands.runOnce(drivebase::lock, drivebase).repeatedly());




    //prajbox safety switch on activates climbers on sticks, disables collector
    //hold button to reverse

    
=======
    Command driveFieldOrientedDirectAngle      = drivebase.driveFieldOriented(driveDirectAngle);
    Command driveFieldOrientedAnglularVelocity = drivebase.driveFieldOriented(driveAngularVelocity);
    Command driveRobotOrientedAngularVelocity  = drivebase.driveFieldOriented(driveRobotOriented);
    Command driveSetpointGen = drivebase.driveWithSetpointGeneratorFieldRelative(
        driveDirectAngle);
    Command driveFieldOrientedDirectAngleKeyboard      = drivebase.driveFieldOriented(driveDirectAngleKeyboard);
    Command driveFieldOrientedAnglularVelocityKeyboard = drivebase.driveFieldOriented(driveAngularVelocityKeyboard);
    Command driveSetpointGenKeyboard = drivebase.driveWithSetpointGeneratorFieldRelative(
        driveDirectAngleKeyboard);

    if (RobotBase.isSimulation())
    {
      drivebase.setDefaultCommand(driveFieldOrientedDirectAngleKeyboard);
    } else
    {
      drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity);
    }

    if (Robot.isSimulation())
    {
      Pose2d target = new Pose2d(new Translation2d(1, 4),
                                 Rotation2d.fromDegrees(90));
      //drivebase.getSwerveDrive().field.getObject("targetPose").setPose(target);
      driveDirectAngleKeyboard.driveToPose(() -> target,
                                           new ProfiledPIDController(5,
                                                                     0,
                                                                     0,
                                                                     new Constraints(5, 2)),
                                           new ProfiledPIDController(5,
                                                                     0,
                                                                     0,
                                                                     new Constraints(Units.degreesToRadians(360),
                                                                                     Units.degreesToRadians(180))
                                           ));
      driverXbox.start().onTrue(Commands.runOnce(() -> drivebase.resetOdometry(new Pose2d(3, 3, new Rotation2d()))));
      driverXbox.button(1).whileTrue(drivebase.sysIdDriveMotorCommand());
      driverXbox.button(2).whileTrue(Commands.runEnd(() -> driveDirectAngleKeyboard.driveToPoseEnabled(true),
                                                     () -> driveDirectAngleKeyboard.driveToPoseEnabled(false)));

//      driverXbox.b().whileTrue(
//          drivebase.driveToPose(
//              new Pose2d(new Translation2d(4, 4), Rotation2d.fromDegrees(0)))
//                              );

    }
    if (DriverStation.isTest())
    {
      drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity); // Overrides drive command above!

      driverXbox.x().whileTrue(Commands.runOnce(drivebase::lock, drivebase).repeatedly());
      driverXbox.start().onTrue((Commands.runOnce(drivebase::zeroGyro)));
      driverXbox.back().whileTrue(drivebase.centerModulesCommand());
      driverXbox.leftBumper().onTrue(Commands.none());
      driverXbox.rightBumper().onTrue(Commands.none());
    } else
    {
      driverXbox.a().onTrue((Commands.runOnce(drivebase::zeroGyro)));
      driverXbox.start().whileTrue(Commands.none());
      driverXbox.back().whileTrue(Commands.none());
      driverXbox.leftBumper().whileTrue(Commands.runOnce(drivebase::lock, drivebase).repeatedly());
      driverXbox.rightBumper().onTrue(Commands.none());
    }
>>>>>>> 2df575ca02422bdb5feadc7a339e6a239e1a28fd

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand()
  {
<<<<<<< HEAD
    // An example command will be run in autonomous
    return autoChooser.getSelected();
  }

  public void setDriveMode()
  {
    //drivebase.setDefaultCommand();
  }

=======
    // Pass in the selected auto from the SmartDashboard as our desired autnomous commmand 
    return autoChooser.getSelected();
  }

>>>>>>> 2df575ca02422bdb5feadc7a339e6a239e1a28fd
  public void setMotorBrake(boolean brake)
  {
    drivebase.setMotorBrake(brake);
  }
<<<<<<< HEAD
}
=======
}
>>>>>>> 2df575ca02422bdb5feadc7a339e6a239e1a28fd
