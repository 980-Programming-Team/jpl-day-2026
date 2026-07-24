package frc.robot.commands;

import java.util.function.Function;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

public class FollowFunctionCommand extends Command {
    private final Function<Double, Double> function;
    private final double duration;
    private double startTime;
    private final double yToTimeRatio = 1.0; 

    public FollowFunctionCommand(Function<Double, Double> function, double duration) {
        this.function = function;
        this.duration = duration;
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    public void execute() {
        double elapsedTime = Timer.getFPGATimestamp() - startTime;
        if (elapsedTime < duration) {
            double output = function.apply(elapsedTime/yToTimeRatio);
        }
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - startTime >= duration;
    }
    
}
