package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.BooleanPublisher;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import frc.robot.Constants.CAN; 
import static edu.wpi.first.units.Units.*;

public class Collector extends SubsystemBase{
    private SparkMax indexer;
    private SparkMax collector;
    private DigitalInput beamBreak;
    private final BooleanPublisher noteDetect;

    public Collector() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        NetworkTable folder = inst.getTable("SmartDashboard/subsystems/collector");
        indexer = new SparkMax(CAN.index, MotorType.kBrushless);
        collector = new SparkMax(CAN.collect, MotorType.kBrushless);
        beamBreak = new DigitalInput(0);
        noteDetect = folder.getBooleanTopic("noteDetect").publish();

        SparkMaxConfig indexerConfig = new SparkMaxConfig();
        SparkMaxConfig collectorConfig = new SparkMaxConfig();
        indexerConfig.inverted(true);
        indexerConfig.smartCurrentLimit(20);

        collectorConfig.inverted(true);
        collectorConfig.smartCurrentLimit(40);

        indexer.configure(indexerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        collector.configure(collectorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    @Override
    public void periodic() {
        noteDetect.set(beamBreak.get());
    }

    public void intake(){
        collector.setVoltage(Volts.of(-8.4));
    }

    public void outtake(){
        collector.setVoltage(Volts.of(8.4));
    }

    public void index(){
        indexer.setVoltage(Volts.of(6));
    }

    public void indexIntoShooter(){
        collector.setVoltage(Volts.of(-8.4));
        indexer.setVoltage(Volts.of(6));
    }

    public void indexIntoAmp(){
        collector.setVoltage(Volts.of(8.4));
        indexer.setVoltage(Volts.of(3.6));
    }

    public void off(){
        collector.setVoltage(Volts.of(0));
        indexer.setVoltage(Volts.of(0));
    }
}