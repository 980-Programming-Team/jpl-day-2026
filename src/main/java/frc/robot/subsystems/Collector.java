package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.CANSparkMax;
public class Collector extends SubsystemBase{
    private CANSparkMax indexer;
    private CANSparkMax collector;
    private DigitalInput beamBreak;

    public Collector() {
        NetworkTablesInstance inst = NetworkTablesInstance.getDefault();
        NetworkTables folder = inst.getTable("SmartDashboard/subsystems/collector");
        indexer = new CANSparkMax(CAN.index, MotorType.kBrushless);
        collector = new CANSparkMax(CAN.collect, MotorType.kBrushless);
        beamBreak = new DigitalInput(0);
        private BooleanPublisher noteDetect = folder.getBooleanTopic("noteDetect").publish();
        index.setInverted(true);
        collect.setInverted(true);
    }

    @Override
    public void periodic() {
        noteDetect.set(beamBreak.get());
    }

    public void intake(){
        collect.set(-.7);
    }

    public void outtake(){
        collect.setVoltage(.7);
    }

    public void index(){
        index.setVoltage(.5);
    }

    public void indexIntoShooter(){
        collect.setVoltage(-8.4);
        index.setVoltage(6);
    }

    public void indexIntoAmp(){
        collect.setVoltage(8.4);
        index.setVoltage(3.6);
    }

    public void off(){
        collect.setVoltage(0);
        index.setVoltage(0);
    }
}
