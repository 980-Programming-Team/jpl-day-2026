package frc.robot.utilities;

import edu.wpi.first.math.geometry.Transform3d;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.List;
import java.util.Optional;


/**
 * Example PhotonVision class to aid in the pursuit of accurate odometry. Taken from
 * https://gitlab.com/ironclad_code/ironclad-2024/-/blob/master/src/main/java/frc/robot/vision/Vision.java?ref_type=heads
 */
public class Vision
{
  private final PhotonCamera camera;
  private PhotonPipelineResult latestResult = new PhotonPipelineResult();

  public Vision(String cameraName) {
    this.camera = new PhotonCamera(cameraName);
  }

  public void update() {
    List<PhotonPipelineResult> unreadResults = camera.getAllUnreadResults();

    if (!unreadResults.isEmpty()) {
      latestResult = unreadResults.get(unreadResults.size()-1);
    }
  }

  public PhotonPipelineResult getLatestResult() {
    return latestResult;
  }

  public Optional<Transform3d> getTransformToTag(int targetId) {
    if (latestResult.hasTargets()) {
      for (PhotonTrackedTarget target : latestResult.getTargets()) {
        if (target.getFiducialId() == targetId && target.getPoseAmbiguity() < 0.2) {
          return Optional.of(target.getBestCameraToTarget());
        }
      }
    }
    return Optional.empty();
  }

  public Optional<PhotonTrackedTarget> getBestTarget() {
    if (latestResult.hasTargets()) {
      return Optional.of(latestResult.getBestTarget());
    }
    return Optional.empty();
  }
}