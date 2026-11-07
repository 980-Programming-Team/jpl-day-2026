# Copyright (c) FIRST and other WPILib contributors.
# Open Source Software; you can modify and/or share it under the terms of
# the WPILib BSD license file in the root directory of this project.

import math
from pathlib import Path
from typing import Callable
import ntcore
import wpilib
from commands2 import Command, Subsystem
from wpimath import units
from wpimath.estimator import SwerveDrive4PoseEstimator
from wpimath.geometry import Pose2d, Rotation2d, Translation2d
from wpimath.kinematics import (
    ChassisSpeeds,
    SwerveDrive4Kinematics,
    SwerveModulePosition,
    SwerveModuleState,
)

import phoenix5.sensors
import phoenix6.hardware
import rev
from constants import Constants


class SwerveModule:

    def __init__(
        self,
        drive_id: int,
        angle_id: int,
        cancoder_id: int,
        offset_deg: float,
        drive_inverted: bool,
    ):
        self.drive_motor = rev.SparkMax(
            drive_id, rev.SparkMax.MotorType.kBrushless
        )
        self.angle_motor = rev.SparkMax(
            angle_id, rev.SparkMax.MotorType.kBrushless
        )

        self.cancoder = phoenix6.hardware.CANcoder(cancoder_id)
        self.offset_deg = offset_deg

        # Drive Configuration
        drive_config = rev.SparkMaxConfig()
        drive_config.inverted(drive_inverted)
        drive_config.smartCurrentLimit(20)
        drive_config.closedLoop.pid(0.0020645, 0.0, 0.0)
        drive_config.closedLoop.outputRange(-1.0, 1.0)
        drive_config.openLoopRampRate(0.05)

        wheel_circ_m = units.inchesToMeters(4.0) * math.pi
        drive_config.encoder.positionConversionFactor(wheel_circ_m / 6.75)
        drive_config.encoder.velocityConversionFactor(
            (wheel_circ_m / 6.75) / 60.0
        )

        # Angle Configuration
        angle_config = rev.SparkMaxConfig()
        angle_config.smartCurrentLimit(15)
        angle_config.closedLoop.pid(0.01, 0.0, 0.0)
        angle_config.openLoopRampRate(0.05)
        angle_config.encoder.positionConversionFactor((2.0 * math.pi) / 12.8)
        angle_config.encoder.velocityConversionFactor(
            ((2.0 * math.pi) / 12.8) / 60.0
        )

        self.drive_motor.configure(
            drive_config,
            rev.ResetMode.kResetSafeParameters,
            rev.PersistMode.kPersistParameters,
        )
        self.angle_motor.configure(
            angle_config,
            rev.ResetMode.kResetSafeParameters,
            rev.PersistMode.kPersistParameters,
        )

        self.drive_encoder = self.drive_motor.getEncoder()
        self.angle_encoder = self.angle_motor.getEncoder()
        self.drive_closed_loop = self.drive_motor.getClosedLoopController()
        self.angle_closed_loop = self.angle_motor.getClosedLoopController()

    def set_desired_state(self, state: SwerveModuleState):
        current_angle = Rotation2d(self.angle_encoder.getPosition())
        optimized_state = SwerveModuleState.optimize(state, current_angle)

        optimized_state.speedMetersPerSecondsp *= optimized_state.angle.cosine(
            current_angle
        )

        self.drive_closed_loop.setReference(
            optimized_state.speedMetersPerSecond,
            rev.SparkBase.ControlType.kVelocity,
        )
        self.angle_closed_loop.setReference(
            optimized_state.angle.radians(),
            rev.SparkBase.ControlType.kPosition,
        )

    def get_position(self) -> SwerveModulePosition:
        return SwerveModulePosition(
            self.drive_encoder.getPosition(),
            Rotation2d(self.angle_encoder.getPosition()),
        )


class SwerveSubsystem(Subsystem):

    def __init__(self, directory: Path):
        super().__init__()
        # Pigeon hardware completely encapsulated within subsystem
        self.pigeon = phoenix5.sensors.PigeonIMU(30)

        inst = ntcore.NetworkTableInstance.getDefault()
        self.rawYawPublisher = inst.getDoubleTopic(
            "SmartDashboard/PIGEON_TESTS/Raw Yaw"
        ).publish()
        self.robotYawPublisher = inst.getDoubleTopic(
            "SmartDashboard/PIGEON_TESTS/Robot Yaw"
        ).publish()

        self.prevHeading = self.getHeading()
        self.currentHeading = self.prevHeading

        self.headingThreshold = Rotation2d.fromDegrees(5)
        self.headingOffset = self.prevHeading

        track_width = units.inchesToMeters(23.0)
        wheel_base = units.inchesToMeters(23.0)

        self.kinematics = SwerveDrive4Kinematics(
            Translation2d(wheel_base / 2, track_width / 2),  # Front Left
            Translation2d(wheel_base / 2, -track_width / 2),  # Front Right
            Translation2d(-wheel_base / 2, track_width / 2),  # Back Left
            Translation2d(-wheel_base / 2, -track_width / 2),  # Back Right
        )

        self.modules = [
            SwerveModule(41, 42, 43, 296.631, False),  # Front Left
            SwerveModule(51, 52, 53, 0.0, False),  # Front Right
            SwerveModule(11, 12, 13, 190.986, False),  # Back Left
            SwerveModule(21, 22, 23, 96.328, True),  # Back Right
        ]

        currentAlliance = wpilib.DriverStation.getAlliance()
        isBlueAlliance = (
            currentAlliance == wpilib.DriverStation.Alliance.kBlue
        )

        startingPose = (
            Pose2d(Translation2d(1, 4), Rotation2d.fromDegrees(0))
            if isBlueAlliance
            else Pose2d(Translation2d(16, 4), Rotation2d.fromDegrees(180))
        )

        self.pose_estimator = SwerveDrive4PoseEstimator(
            self.kinematics,
            self.getHeading(),
            tuple(m.get_position() for m in self.modules),
            startingPose,
        )

    # --- Encapsulated Gyro Methods ---

    def getHeading(self) -> Rotation2d:
        """Returns current gyro yaw wrapped in a WPILib Rotation2d."""
        return Rotation2d.fromDegrees(self.pigeon.getYaw() * -1.0)

    def getPitch(self) -> Rotation2d:
        """Returns current gyro pitch wrapped in a WPILib Rotation2d."""
        return Rotation2d.fromDegrees(self.pigeon.getPitch())

    def zeroGyro(self):
        """Resets gyro angle and syncs odometry pose."""
        self.pigeon.setYaw(0.0)
        currentPose = self.getPose()
        self.headingOffset = self.getHeading()
        self.prevHeading = self.headingOffset
        self.resetOdometry(
            Pose2d(currentPose.translation(), Rotation2d.fromDegrees(0))
        )

    def setHeading(self, heading: Rotation2d):
        currentPose = self.getPose()
        self.pose_estimator.resetPosition(
            self.getHeading(),
            tuple(m.get_position() for m in self.modules),
            Pose2d(currentPose.translation(), heading),
        )

    # --- Subsystem Methods ---

    def periodic(self):
        self.currentHeading = self.getHeading()
        self.rawYawPublisher.set(self.currentHeading.degrees())

        currentHeadingDegrees = self.currentHeading.degrees()
        prevHeadingDegrees = self.prevHeading.degrees()

        rawDiff = currentHeadingDegrees - prevHeadingDegrees
        if rawDiff > 180:
            rawDiff -= 360
        elif rawDiff <= -180:
            rawDiff += 360

        if abs(rawDiff) >= self.headingThreshold.degrees():
            self.setHeading(
                self.currentHeading.rotateBy(self.headingOffset.times(-1))
            )
            self.robotYawPublisher.set(self.currentHeading.degrees())
            self.prevHeading = self.currentHeading

        self.pose_estimator.update(
            self.getHeading(), tuple(m.get_position() for m in self.modules)
        )

    def drive(
        self,
        translation: Translation2d,
        rotation: float = 0.0,
        fieldRelative: bool = True,
    ):
        if isinstance(translation, ChassisSpeeds):
            chassisSpeeds = translation
        else:
            chassisSpeeds = (
                ChassisSpeeds.fromFieldRelativeSpeeds(
                    translation.x, translation.y, rotation, self.getHeading()
                )
                if fieldRelative
                else ChassisSpeeds(translation.x, translation.y, rotation)
            )

        states = self.kinematics.toModuleStates(chassisSpeeds)
        SwerveDrive4Kinematics.desaturateWheelSpeeds(states, Constants.MAX_SPEED)

        for i, module in enumerate(self.modules):
            module.set_desired_state(states[i])

    def driveCommand(
        self,
        translationX: Callable[[], float],
        translationY: Callable[[], float],
        angularRotationX: Callable[[], float],
    ) -> Command:
        return self.run(
            lambda: self.drive(
                Translation2d(
                    translationX() * Constants.MAX_SPEED,
                    translationY() * Constants.MAX_SPEED,
                ),
                angularRotationX() * 2.0 * math.pi,
                True,
            )
        )

    def resetOdometry(self, initialHolonomicPose: Pose2d):
        self.pose_estimator.resetPosition(
            self.getHeading(),
            tuple(m.get_position() for m in self.modules),
            initialHolonomicPose,
        )

    def getPose(self) -> Pose2d:
        return self.pose_estimator.getEstimatedPosition()

    def setMotorBrake(self, brake: bool):
        idle_mode = (
            rev.SparkBaseConfig.IdleMode.kBrake
            if brake
            else rev.SparkBaseConfig.IdleMode.kCoast
        )
        for m in self.modules:
            config = rev.SparkBaseConfig().idleMode(idle_mode)
            m.drive_motor.configure(
                config,
                rev.ResetMode.kNoResetSafeParameters,
                rev.PersistMode.kNoPersistParameters,
            )
            m.angle_motor.configure(
                config,
                rev.ResetMode.kNoResetSafeParameters,
                rev.PersistMode.kNoPersistParameters,
            )