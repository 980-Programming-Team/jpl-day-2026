# Copyright (c) FIRST and other WPILib contributors.
# Open Source Software; you can modify and/or share it under the terms of
# the WPILib BSD license file in the root directory of this project.

from wpimath.geometry import Translation3d
from wpimath import units
import wpilib
from wpilib import Field2d

from dataclasses import dataclass
from wpimath.geometry import Translation3d

@dataclass
class Matter:
    """Python equivalent for tracking physical component mass and center of mass."""
    position: Translation3d  # Center of mass position relative to robot origin
    mass: float              # Mass in kilograms

battery = Matter(Translation3d(0.0, 0.0, 0.10), 5.0)

# The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean constants. This
# class should not be used for any other purpose. All constants should be declared globally (i.e. public static). Do
# not put anything functional in this class.
#
# <p>It is advised to statically import this class (or one of its inner classes) wherever the
# constants are needed, to reduce verbosity.
class Constants:
    ROBOT_MASS = (148 - 20.3) * 0.453592  # 32lbs * kg per pound
    CHASSIS = Matter(Translation3d(0, 0, units.inchesToMeters(8)), ROBOT_MASS)
    LOOP_TIME = 0.13  # s, 20ms + 110ms sprk max velocity lag
    MAX_SPEED = units.feetToMeters(15.1)
    FIELD = Field2d()

    # Maximum speed of the robot in meters per second, used to limit acceleration.

    class CAN:
        # COLLECTOR
        index = 13
        collect = 14

        # SHOOTER
        flywheelRightBot = 19
        flywheelLeftTop = 17
        angleRight = 18
        angleLeft = 16

    class DrivebaseConstants:
        # Hold time on motor brakes when disabled
        WHEEL_LOCK_TIME = 10  # seconds
        ROBOT_WIDTH_NO_BUMPERS = 29  # Inches
        ROBOT_BUMPER_THICKNESS = 3  # Inches
        ROBOT_WIDTH = 34  # Inches (added 2 inches to account for gaps between bumpers and frame)

    class OperatorConstants:
        # Joystick Deadband
        k_deadband = 0.2