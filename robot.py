# Copyright (c) FIRST and other WPILib contributors.
# Open Source Software; you can modify and/or share it under the terms of
# the WPILib BSD license file in the root directory of this project.

import wpilib.simulation
import ntcore
import hal
from commands2 import CommandScheduler
from robotcontainer import RobotContainer
from swervesubsystem import SwerveSubsystem


# The methods in this class are called automatically corresponding to each mode, as described in
# the TimedRobot documentation. If you change the name of this class or the package after creating
# this project, you must also update the Main.java file in the project.
class Robot(wpilib.TimedRobot):
    # private Command m_autonomousCommand;

    m_robotContainer: RobotContainer = None

    def __init__(self):
        super().__init__()
        # Logger.recordMetadata("ProjectName", "jpl-day-2026") # Set your project name

        if self.isReal():  # True if real, false if simulation
            # If running on a real robot, log to a USB stick and publish to NetworkTables
            # Logger.addDataReceiver(new WPILOGWriter("/media/sda1/")) 
            # Logger.addDataReceiver(new NT4Publisher())
            # Optional: Log power distribution data automatically
            wpilib.PowerDistribution(1, wpilib.PowerDistribution.ModuleType.kRev)
        else:
            # If running in simulation, just publish to NetworkTables (or a local log path)
            pass

        # Start the logger
        # Logger.start()
        # Instantiate our RobotContainer.  This will perform all our button bindings, and put our
        # autonomous chooser on the dashboard.
        Robot.m_robotContainer = RobotContainer()
        self.driverController = Robot.m_robotContainer.getDriverController()

        inst = ntcore.NetworkTableInstance.getDefault()
        self.batteryVoltagePublisher = inst.getDoubleTopic("SmartDashboard/Battery Voltage").publish()

    # This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
    # that you want ran during disabled, autonomous, teleoperated and test.
    #
    # <p>This runs after the mode specific periodic functions, but before LiveWindow and
    # SmartDashboard integrated updating.
    def robotPeriodic(self):
        # Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
        # commands, running already-scheduled commands, removing finished or interrupted commands,
        # and running subsystem periodic() methods.  This must be called from the robot's periodic
        # block in order for anything in the Command-based framework to work.
        CommandScheduler.getInstance().run()

        if self.isReal():
            self.batteryVoltagePublisher.set(wpilib.RobotController.getBatteryVoltage())

    # This function is called once each time the robot enters Disabled mode.
    def disabledInit(self):
        pass

    def disabledPeriodic(self):
        pass

    # This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
    def autonomousInit(self):
        pass

    # This function is called periodically during autonomous.
    def autonomousPeriodic(self):
        pass

    def teleopInit(self):
        # This makes sure that the autonomous stops running when
        # teleop starts running. If you want the autonomous to
        # continue until interrupted by another command, remove
        # this line or comment it out.
        # if (m_autonomousCommand != None):
        #     m_autonomousCommand.cancel()
        pass

    # This function is called periodically during operator control.
    def teleopPeriodic(self):
        pass

    def testInit(self):
        # Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().cancelAll()

    # This function is called periodically during test mode.
    def testPeriodic(self):
        pass

    # This function is called once when the robot is first started up.
    def simulationInit(self):
        # m_robotContainer.speedScale = 1
        wpilib.simulation.DriverStationSim.setAllianceStationId(hal.AllianceStationID.Blue1)
        wpilib.simulation.DriverStationSim.notifyNewData()

    # This function is called periodically whilst in simulation.
    def simulationPeriodic(self):
        pass


if __name__ == "__main__":
    wpilib.run(Robot)