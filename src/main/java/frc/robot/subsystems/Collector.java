package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.ResetMode;
import com.revrobotics.PersistMode;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.NetworkTable; // Note: fixed 'NetworkTables' type typo to NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase; // Assuming this extends SubsystemBase for periodic()
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;



public class Collector extends SubsystemBase{
    private SparkMax indexer;
    private SparkMax collector;
    private DigitalInput beamBreak;

    public Collector() {
        NetworkTablesInstance inst = NetworkTablesInstance.getDefault();
        NetworkTables folder = inst.getTable("SmartDashboard/subsystems/collector");
        indexer = new SparkMax(CAN.indexer, MotorType.kBrushless);
        collector = new SparkMax(CAN.collector, MotorType.kBrushless);
        beamBreak = new DigitalInput(0);
        final BooleanPublisher noteDetect = folder.getBooleanTopic("noteDetect").publish();
        
        SparkMaxConfig motorConfig = new SparkMaxConfig();

        // 2. Set inverted to true on the config object
        motorConfig.inverted(true);

        // 3. Apply the config to your motor controllers
        indexer.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        collector.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        
        

    }

    @Override
    public void periodic() {
        noteDetect.set(beamBreak.get());
    }

    public void intake(){
        collector.set(-.7);
    }

    public void outtake(){
        collector.setVoltage(.7);
    }

    public void index(){
        indexer.setVoltage(.5);
    }

    public void indexIntoShooter(){
        collector.setVoltage(-8.4);
        indexer.setVoltage(6);
    }

    public void indexIntoAmp(){
        collector.setVoltage(8.4);
        indexer.setVoltage(3.6);
    }

    public void off(){
        collector.setVoltage(0);
        indexer.setVoltage(0);
    }
}
