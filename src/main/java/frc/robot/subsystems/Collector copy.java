// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.CAN;

public class Collector extends SubsystemBase {
  private CANSparkMax index;
  private CANSparkMax collect;

  private boolean override;

  private DigitalInput beamBreak;
  private int RUMBLE_TIMER = 25;

  private CommandXboxController xboxD;
  private CommandXboxController xboxO;


  /** Creates a new Collector. */
  public Collector() {
    index = new CANSparkMax(CAN.index, MotorType.kBrushless);
    collect = new CANSparkMax(CAN.collect, MotorType.kBrushless);

    index.setInverted(true);
    collect.setInverted(true);

    override = false;


    beamBreak = new DigitalInput(0);

    //primaryNoteDetect = new DigitalInput(0);
    //this.xboxD = xboxD;
    //this.xboxO = xboxO;
    //parameter: CommandXboxController xboxD , CommandXboxController xboxO

  }

  @Override
  public void periodic() {
    boolean beamBreakState = beamBreak.get();
    SmartDashboard.putBoolean("BeamBreak", beamBreakState);
    //if()
    //xboxD.getHID().setRumble(RumbleType.kBothRumble , 1);
    // This method will be called once per scheduler run
  }


  public void intake(){
    collect.set(-.7);
  }

  public void outtake(){
    collect.set(.7);
  }

  public void index(){
    index.set(.5);
  }

  public void indexIntoShooter(){
    collect.set(-.7);
    index.set(.5);
  }

  public void indexIntoAmp(){
    collect.set(.7);
    index.set(.3);
  }

  public boolean getNoteDetect(){
    return beamBreak.get();
  }

  public void off(){
    collect.set(0);
    index.set(0);
  }


}
