package frc.robot.subsystems;
import java.lang.annotation.Target;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.units.measure.AngularVelocity;
import static edu.wpi.first.units.Units.*;

import frc.robot.Constants.CAN;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.WaitCommand;



public class Shooter extends SubsystemBase {
    private final AngularVelocity kShooterMaxRMP = RPM.of(5800);
    private final double kShooterKS;
    private SparkMax angleRight;
    private SparkMax angleLeft;


    private RelativeEncoder topEncoder;
    private RelativeEncoder bottomEncoder;
}
