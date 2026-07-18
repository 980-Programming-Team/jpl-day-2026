package frc.robot.utilities;

import java.util.function.BooleanSupplier;

import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class CustomJoystick extends CommandXboxController {

    public enum OS {
        WINDOWS(4, 5, 3, 2, 3, 4, 6, 5),
        MACOS(2, 3, 4, 5, 4, 5, 8, 7);

        public final int rightStickX;
        public final int rightStickY;
        public final int rightTrigger;
        public final int leftTrigger;
        public final int x;
        public final int y;
        public final int rb;
        public final int lb;

        OS(int rightStickX, int rightStickY, int rightTrigger, int leftTrigger, int x, int y, int rb, int lb) {
            this.rightStickX = rightStickX;
            this.rightStickY = rightStickY;
            this.rightTrigger = rightTrigger;
            this.leftTrigger = leftTrigger;
            this.x = x;
            this.y = y;
            this.rb = rb;
            this.lb = lb;
        }
    }

    private OS currentOS;

    private final DoublePublisher rightStickXPub;
    private final DoublePublisher rightStickYPub;
    private final DoublePublisher leftStickXPub;
    private final DoublePublisher leftStickYPub;
    
    private final BooleanPublisher aPub;
    private final BooleanPublisher bPub;
    private final BooleanPublisher xPub;
    private final BooleanPublisher yPub;
    
    private final DoublePublisher leftTriggerPub;
    private final DoublePublisher rightTriggerPub;
    
    private final BooleanPublisher dPadUpPub;
    private final BooleanPublisher dPadDownPub;
    private final BooleanPublisher dPadLeftPub;
    private final BooleanPublisher dPadRightPub;
    
    private final BooleanPublisher lbPub;
    private final BooleanPublisher rbPub;
    private final StringPublisher osPub;

    private int id;

    public CustomJoystick(OS os, int joystickId) {
        super(joystickId);
        this.currentOS = os;
        this.id = joystickId;

        // Data Logging
        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        NetworkTable rightStickTable = inst.getTable(String.format("SmartDashboard/Joysticks/Joy%s/rightStick", joystickId));
        rightStickXPub = rightStickTable.getDoubleTopic("x").publish();
        rightStickYPub = rightStickTable.getDoubleTopic("y").publish();

        NetworkTable leftStickTable = inst.getTable(String.format("SmartDashboard/Joysticks/Joy%s/leftStick", joystickId));
        leftStickXPub = leftStickTable.getDoubleTopic("x").publish();
        leftStickYPub = leftStickTable.getDoubleTopic("y").publish();

        NetworkTable buttonsTable = inst.getTable(String.format("SmartDashboard/Joysticks/Joy%s/buttons", joystickId));
        aPub = buttonsTable.getBooleanTopic("a").publish();
        bPub = buttonsTable.getBooleanTopic("b").publish();
        xPub = buttonsTable.getBooleanTopic("x").publish();
        yPub = buttonsTable.getBooleanTopic("y").publish();

        NetworkTable triggersTable = inst.getTable(String.format("SmartDashboard/Joysticks/Joy%s/triggers", joystickId));
        leftTriggerPub = triggersTable.getDoubleTopic("left").publish();
        rightTriggerPub = triggersTable.getDoubleTopic("right").publish();

        NetworkTable dPadTable = inst.getTable(String.format("SmartDashboard/Joysticks/Joy%s/dPad", joystickId));
        dPadUpPub = dPadTable.getBooleanTopic("up").publish();
        dPadDownPub = dPadTable.getBooleanTopic("down").publish();
        dPadLeftPub = dPadTable.getBooleanTopic("left").publish();
        dPadRightPub = dPadTable.getBooleanTopic("right").publish();

        NetworkTable bumpersTable = inst.getTable(String.format("SmartDashboard/Joysticks/Joy%s/bumpers", joystickId));
        lbPub = bumpersTable.getBooleanTopic("lb").publish();
        rbPub = bumpersTable.getBooleanTopic("rb").publish();

        NetworkTable table = inst.getTable(String.format("SmartDashboard/Joysticks/Joy%s", joystickId));
        osPub = table.getStringTopic("OS").publish();
    }

    public void periodic() {
        rightStickXPub.set(DriverStation.getStickAxis(id, currentOS.rightStickX));
        rightStickYPub.set(DriverStation.getStickAxis(id, currentOS.rightStickY));
        leftStickXPub.set(DriverStation.getStickAxis(id, 0));
        leftStickYPub.set(DriverStation.getStickAxis(id, 1));

        aPub.set(DriverStation.getStickButton(id, 1));
        bPub.set(DriverStation.getStickButton(id, 2));
        xPub.set(DriverStation.getStickButton(id, currentOS.x));
        yPub.set(DriverStation.getStickButton(id, currentOS.y));

        double rawLT = DriverStation.getStickAxis(id, currentOS.leftTrigger);
        double rawRT = DriverStation.getStickAxis(id, currentOS.rightTrigger);
        
        leftTriggerPub.set((currentOS == OS.MACOS) ? (rawLT + 1.0) / 2.0 : rawLT);
        rightTriggerPub.set((currentOS == OS.MACOS) ? (rawRT + 1.0) / 2.0 : rawRT);
        
        int pov = DriverStation.getStickPOV(id, 0);
        dPadUpPub.set(pov == 0);
        dPadRightPub.set(pov == 90);
        dPadDownPub.set(pov == 180);
        dPadLeftPub.set(pov == 270);

        lbPub.set(DriverStation.getStickButton(id, currentOS.lb));
        rbPub.set(DriverStation.getStickButton(id, currentOS.rb));
        
        osPub.set(currentOS.toString());
    }

    public void setOS(OS os) {
        this.currentOS = os;
    }

    public OS getOS() {
        return this.currentOS;
    }

    @Override
    public double getRightX() {
        return this.getRawAxis(currentOS.rightStickX);
    }

    @Override
    public double getRightY() {
        return this.getRawAxis(currentOS.rightStickY);
    }

    @Override
    public double getLeftTriggerAxis() {
        double raw = this.getRawAxis(currentOS.leftTrigger);
        
        if (currentOS == OS.MACOS) {
            return (raw + 1.0) / 2.0;
        }
        return raw;
    }

    @Override
    public double getRightTriggerAxis() {
        double raw = this.getRawAxis(currentOS.rightTrigger);
        
        if (currentOS == OS.MACOS && raw == 0.0) {
            return (raw + 1.0) / 2.0;
        }
        return raw;
    }

    @Override
    public Trigger x() {
        return new Trigger((BooleanSupplier) () -> this.getHID().getRawButton(currentOS.x));
    }

    @Override
    public Trigger y() {
        return new Trigger((BooleanSupplier) () -> this.getHID().getRawButton(currentOS.y));
    }

    @Override
    public Trigger rightBumper() {
        return new Trigger((BooleanSupplier) () -> this.getHID().getRawButton(currentOS.rb));
    }

    @Override
    public Trigger leftBumper() {
        return new Trigger((BooleanSupplier) () -> this.getHID().getRawButton(currentOS.lb));
    }
}
