package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.units.measure.AngularVelocity;
import static edu.wpi.first.units.Units.*;

public class Shooter extends SubsystemBase {
    private final AngularVelocity kShooterMaxRMP = RPM.of(5800);
    private final double kShooterKS
}
