package frc.robot.utilities;

import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class CustomJoystick extends CommandXboxController {

    public enum OS {
        WINDOWS(4, 5, 3, 2),
        MACOS(2, 3, 6, 5);

        public final int rightStickX;
        public final int rightStickY;
        public final int rightTrigger;
        public final int leftTrigger;

        OS(int rightStickX, int rightStickY, int rightTrigger, int leftTrigger) {
            this.rightStickX = rightStickX;
            this.rightStickY = rightStickY;
            this.rightTrigger = rightTrigger;
            this.leftTrigger = leftTrigger;
        }
    }

    private final OS currentOS;

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

    public CustomJoystick(OS os, int joystickId) {
        super(joystickId);
        this.currentOS = os;

        // Data Logging
        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        NetworkTable rightStickTable = inst.getTable(String.format("SmartDashboard/Joy%s/rightStick", joystickId));
        rightStickXPub = rightStickTable.getDoubleTopic("x").publish();
        rightStickYPub = rightStickTable.getDoubleTopic("y").publish();

        NetworkTable leftStickTable = inst.getTable(String.format("SmartDashboard/Joy%s/leftStick", joystickId));
        leftStickXPub = leftStickTable.getDoubleTopic("x").publish();
        leftStickYPub = leftStickTable.getDoubleTopic("y").publish();

        NetworkTable buttonsTable = inst.getTable(String.format("SmartDashboard/Joy%s/buttons", joystickId));
        aPub = buttonsTable.getBooleanTopic("a").publish();
        bPub = buttonsTable.getBooleanTopic("b").publish();
        xPub = buttonsTable.getBooleanTopic("x").publish();
        yPub = buttonsTable.getBooleanTopic("y").publish();

        NetworkTable triggersTable = inst.getTable(String.format("SmartDashboard/Joy%s/triggers", joystickId));
        leftTriggerPub = triggersTable.getDoubleTopic("left").publish();
        rightTriggerPub = triggersTable.getDoubleTopic("right").publish();

        NetworkTable dPadTable = inst.getTable(String.format("SmartDashboard/Joy%s/dPad", joystickId));
        dPadUpPub = dPadTable.getBooleanTopic("up").publish();
        dPadDownPub = dPadTable.getBooleanTopic("down").publish();
        dPadLeftPub = dPadTable.getBooleanTopic("left").publish();
        dPadRightPub = dPadTable.getBooleanTopic("right").publish();

        NetworkTable bumpersTable = inst.getTable(String.format("SmartDashboard/Joy%s/bumpers", joystickId));
        lbPub = bumpersTable.getBooleanTopic("lb").publish();
        rbPub = bumpersTable.getBooleanTopic("rb").publish();

        new Trigger(()->true).whileTrue(
            new RunCommand(this::periodic).ignoringDisable(true)
        );
    }

    private void periodic() {
        rightStickXPub.set(this.getRightX());
        rightStickYPub.set(this.getRightY());
        leftStickXPub.set(this.getLeftX());
        leftStickYPub.set(this.getLeftY());
        
        aPub.set(this.a().getAsBoolean());
        bPub.set(this.b().getAsBoolean());
        xPub.set(this.x().getAsBoolean());
        yPub.set(this.y().getAsBoolean());
        
        leftTriggerPub.set(this.getLeftTriggerAxis());
        rightTriggerPub.set(this.getRightTriggerAxis());
        
        int pov = this.getHID().getPOV();
        dPadUpPub.set(pov == 0);
        dPadRightPub.set(pov == 90);
        dPadDownPub.set(pov == 180);
        dPadLeftPub.set(pov == 270);

        lbPub.set(this.leftBumper().getAsBoolean());
        rbPub.set(this.rightBumper().getAsBoolean());
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
    public double getRightTriggerAxis() {
        return this.getRawAxis(currentOS.rightTrigger);
    }

    @Override
    public double getLeftTriggerAxis() {
        return this.getRawAxis(currentOS.leftTrigger);
    }
}
