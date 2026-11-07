# Copyright (c) FIRST and other WPILib contributors.
# Open Source Software; you can modify and/or share it under the terms of
# the WPILib BSD license file in the root directory of this project.

from pathlib import Path
import wpilib
from commands2.button import CommandXboxController
from wpimath import applyDeadband

from custom_joystick import CustomJoystick
from swervesubsystem import SwerveSubsystem


class RobotContainer:
    def __init__(self):
        self.speedScale = 0.25
        self.driver_controller = CustomJoystick(CustomJoystick.OS.MACOS, 0)

        # Initialize Drivetrain
        self.drivebase = SwerveSubsystem(
            Path(wpilib.getDeployDirectory()) / "swerve"
        )

        # Zero gyro on startup
        self.drivebase.zeroGyro()

        # Configure default commands and controller bindings
        self.configureBindings()

    def getDriverController(self) -> CustomJoystick:
        return self.driver_controller

    def getSwerve(self) -> SwerveSubsystem:
        return self.drivebase

    def configureBindings(self):
        # Set default teleop driving command using joystick suppliers
        self.drivebase.setDefaultCommand(
            self.drivebase.driveCommand(
                translationX=lambda: applyDeadband(
                    -self.driver_controller.getLeftY(), 0.1
                ),
                translationY=lambda: applyDeadband(
                    -self.driver_controller.getLeftX(), 0.1
                ),
                angularRotationX=lambda: applyDeadband(
                    -self.driver_controller.getRightX(), 0.1
                ),
            )
        )

    def setMotorBrake(self, brake: bool):
        self.drivebase.setMotorBrake(brake)