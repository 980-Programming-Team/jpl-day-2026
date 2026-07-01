// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import org.photonvision.PhotonCamera;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class PhotonFinder extends SubsystemBase {
  private PhotonCamera camera;
  private double[] xya;
  private boolean isFound;
  /** Creates a new GamePieceTargeting. */
  public PhotonFinder() {
    camera = new PhotonCamera("pieceTracker");
    xya = new double[3];
    isFound = false;
  }

  @Override
  public void periodic() {
    SmartDashboard.putBoolean("Piece Found" , isFound);
    SmartDashboard.putNumber("Piece X", xya[0]);
    SmartDashboard.putNumber("Piece Y", xya[1]);
    SmartDashboard.putNumber("Piece Size", xya[2]);
     
    // This method will be called once per scheduler run
  }

  public double[] findClosestTarget(){
    var result = camera.getLatestResult();
    if(result.hasTargets()){
      isFound = true;
      var target = result.getBestTarget();
      xya[0] = target.getYaw();
      xya[1] = target.getPitch();
      xya[2] = target.getArea();
    }
    else{
      isFound = false;
      xya[0] = 333;
      xya[1] = 333;
      xya[2] = -1;
    }
    return xya;
  }
}
