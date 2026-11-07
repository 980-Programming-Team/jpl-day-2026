# Copyright (c) FIRST and other WPILib contributors.
# Open Source Software; you can modify and/or share it under the terms of
# the WPILib BSD license file in the root directory of this project.

from enum import Enum
import wpilib
import ntcore
from commands2.button import CommandXboxController, Trigger


class CustomJoystick(CommandXboxController):
    class OS(Enum):
        WINDOWS = (4, 5, 3, 2, 3, 4, 6, 5)
        MACOS = (2, 3, 4, 5, 4, 5, 8, 7)

        def __init__(
            self,
            rightStickX,
            rightStickY,
            rightTrigger,
            leftTrigger,
            x,
            y,
            rb,
            lb,
        ):
            self.rightStickX = rightStickX
            self.rightStickY = rightStickY
            self.rightTrigger = rightTrigger
            self.leftTrigger = leftTrigger
            self.x = x
            self.y = y
            self.rb = rb
            self.lb = lb

    def __init__(self, os: OS, joystick_id: int):
        super().__init__(joystick_id)
        self.current_os = os
        self.joystick_id = joystick_id

        # Data Logging
        inst = ntcore.NetworkTableInstance.getDefault()

        right_stick_table = inst.getTable(f"SmartDashboard/Joysticks/Joy{joystick_id}/rightStick")
        self.right_stick_x_pub = right_stick_table.getDoubleTopic("x").publish()
        self.right_stick_y_pub = right_stick_table.getDoubleTopic("y").publish()

        left_stick_table = inst.getTable(f"SmartDashboard/Joysticks/Joy{joystick_id}/leftStick")
        self.left_stick_x_pub = left_stick_table.getDoubleTopic("x").publish()
        self.left_stick_y_pub = left_stick_table.getDoubleTopic("y").publish()

        buttons_table = inst.getTable(f"SmartDashboard/Joysticks/Joy{joystick_id}/buttons")
        self.a_pub = buttons_table.getBooleanTopic("a").publish()
        self.b_pub = buttons_table.getBooleanTopic("b").publish()
        self.x_pub = buttons_table.getBooleanTopic("x").publish()
        self.y_pub = buttons_table.getBooleanTopic("y").publish()

        triggers_table = inst.getTable(f"SmartDashboard/Joysticks/Joy{joystick_id}/triggers")
        self.left_trigger_pub = triggers_table.getDoubleTopic("left").publish()
        self.right_trigger_pub = triggers_table.getDoubleTopic("right").publish()

        d_pad_table = inst.getTable(f"SmartDashboard/Joysticks/Joy{joystick_id}/dPad")
        self.d_pad_up_pub = d_pad_table.getBooleanTopic("up").publish()
        self.d_pad_down_pub = d_pad_table.getBooleanTopic("down").publish()
        self.d_pad_left_pub = d_pad_table.getBooleanTopic("left").publish()
        self.d_pad_right_pub = d_pad_table.getBooleanTopic("right").publish()

        bumpers_table = inst.getTable(f"SmartDashboard/Joysticks/Joy{joystick_id}/bumpers")
        self.lb_pub = bumpers_table.getBooleanTopic("lb").publish()
        self.rb_pub = bumpers_table.getBooleanTopic("rb").publish()

        table = inst.getTable(f"SmartDashboard/Joysticks/Joy{joystick_id}")
        self.os_pub = table.getStringTopic("OS").publish()

    def periodic(self):
        self.right_stick_x_pub.set(wpilib.DriverStation.getStickAxis(self.joystick_id, self.current_os.rightStickX))
        self.right_stick_y_pub.set(wpilib.DriverStation.getStickAxis(self.joystick_id, self.current_os.rightStickY))
        self.left_stick_x_pub.set(wpilib.DriverStation.getStickAxis(self.joystick_id, 0))
        self.left_stick_y_pub.set(wpilib.DriverStation.getStickAxis(self.joystick_id, 1))

        self.a_pub.set(wpilib.DriverStation.getStickButton(self.joystick_id, 1))
        self.b_pub.set(wpilib.DriverStation.getStickButton(self.joystick_id, 2))
        self.x_pub.set(wpilib.DriverStation.getStickButton(self.joystick_id, self.current_os.x))
        self.y_pub.set(wpilib.DriverStation.getStickButton(self.joystick_id, self.current_os.y))

        raw_lt = wpilib.DriverStation.getStickAxis(self.joystick_id, self.current_os.leftTrigger)
        raw_rt = wpilib.DriverStation.getStickAxis(self.joystick_id, self.current_os.rightTrigger)

        self.left_trigger_pub.set((raw_lt + 1.0) / 2.0 if self.current_os == self.OS.MACOS else raw_lt)
        self.right_trigger_pub.set((raw_rt + 1.0) / 2.0 if self.current_os == self.OS.MACOS else raw_rt)

        pov = wpilib.DriverStation.getStickPOV(self.joystick_id, 0)
        self.d_pad_up_pub.set(pov == 0)
        self.d_pad_right_pub.set(pov == 90)
        self.d_pad_down_pub.set(pov == 180)
        self.d_pad_left_pub.set(pov == 270)

        self.lb_pub.set(wpilib.DriverStation.getStickButton(self.joystick_id, self.current_os.lb))
        self.rb_pub.set(wpilib.DriverStation.getStickButton(self.joystick_id, self.current_os.rb))

        self.os_pub.set(self.current_os.name)

    def setOS(self, os: OS):
        self.current_os = os

    def getOS(self) -> OS:
        return self.current_os

    def getRightX(self) -> float:
        return self.getRawAxis(self.current_os.rightStickX)

    def getRightY(self) -> float:
        return self.getRawAxis(self.current_os.rightStickY)

    def getLeftTriggerAxis(self) -> float:
        raw = self.getRawAxis(self.current_os.leftTrigger)
        if self.current_os == self.OS.MACOS:
            return (raw + 1.0) / 2.0
        return raw

    def getRightTriggerAxis(self) -> float:
        raw = self.getRawAxis(self.current_os.rightTrigger)
        if self.current_os == self.OS.MACOS and raw == 0.0:
            return (raw + 1.0) / 2.0
        return raw

    def x(self) -> Trigger:
        return Trigger(lambda: self.getHID().getRawButton(self.current_os.x))

    def y(self) -> Trigger:
        return Trigger(lambda: self.getHID().getRawButton(self.current_os.y))

    def rightBumper(self) -> Trigger:
        return Trigger(lambda: self.getHID().getRawButton(self.current_os.rb))

    def leftBumper(self) -> Trigger:
        return Trigger(lambda: self.getHID().getRawButton(self.current_os.lb))