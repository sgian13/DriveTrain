package org.firstinspires.ftc.teamcode.grobo.subsystems;

import static org.firstinspires.ftc.teamcode.grobo.RobotConfig.*;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * ΚΑΜΕΡΑ: βρίσκει το CELL της συμμαχίας μας που σκοράρει (BIOBUZZ clusters).
 *
 *   setAlliance(red)   πριν το START
 *   update()           μία φορά σε κάθε κύκλο του βρόχου
 *   hasTarget()        βλέπει CELL που σκοράρει;
 *   bearing(), rangeCm(), targetName()
 *
 * Αν δεν υπάρχει κάμερα στο configuration, απλώς δεν βρίσκει ποτέ στόχο.
 */
public class Vision {

    private final LinearOpMode opMode;
    private VisionPortal portal;
    private AprilTagProcessor aprilTag;
    private boolean available = false;

    private boolean redAlliance = true;
    private String  trackedName = null;
    private AprilTagClusterDetection current = null;
    private boolean haveFilter = false;
    private double  fBearing, fRange, fYaw;
    private final ElapsedTime sinceSeen = new ElapsedTime();

    public Vision(LinearOpMode opMode) {
        this.opMode = opMode;

        WebcamName cam = opMode.hardwareMap.tryGet(WebcamName.class, WEBCAM);
        if (cam == null) return;   // χωρίς κάμερα

        aprilTag = new AprilTagProcessor.Builder()
                .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(true)
                .build();
        aprilTag.setDecimation(DECIMATION);

        portal = new VisionPortal.Builder()
                .setCamera(cam)
                .setCameraResolution(new Size(640, 480))
                .addProcessor(aprilTag)
                .enableLiveView(true)
                .build();
        available = true;
    }

    public boolean isAvailable() { return available; }

    public void setAlliance(boolean red) { redAlliance = red; }
    public boolean isRed()               { return redAlliance; }

    /** Ξεχνάει τον τωρινό στόχο: στο επόμενο update() διαλέγει από την αρχή. */
    public void resetTracking() {
        trackedName = null;
        haveFilter = false;
    }

    /** Καλείται σε κάθε κύκλο. */
    public void update() {
        current = available ? findScorableCell(aprilTag.getDetections()) : null;

        if (current == null) {
            if (sinceSeen.seconds() > 0.5) haveFilter = false;
            return;
        }
        sinceSeen.reset();

        double bearing = current.ftcPose.bearing;
        double range   = horizontalRange(current);
        double yaw     = current.ftcPose.yaw;
        if (!haveFilter) {
            fBearing = bearing;
            fRange = range;
            fYaw = yaw;
            haveFilter = true;
        } else {
            fBearing += VISION_FILTER_ALPHA * (bearing - fBearing);
            fRange   += VISION_FILTER_ALPHA * (range   - fRange);
            fYaw     += VISION_FILTER_ALPHA * (yaw     - fYaw);
        }
    }

    public boolean hasTarget()  { return current != null && haveFilter; }
    /** Βλέπει αυτή τη στιγμή CELL της συμμαχίας μας (και χωρίς σημάδεμα). */
    public boolean seesTarget() { return current != null; }
    public double  bearing()    { return fBearing; }      // + = στόχος αριστερά
    public double  rangeCm()    { return fRange; }        // οριζόντια απόσταση
    public double  yaw()        { return fYaw; }          // 0 = κάθετα στο CELL
    public String  targetName() { return current == null ? "-" : current.metadata.name; }
    public String  tagsSeen()   { return current == null ? "-" : current.percentClusterFound + "%"; }

    /** Χειροκίνητη έκθεση: λιγότερο θόλωμα σε κίνηση. Καλέστε το στο INIT. */
    public void setManualExposure() {
        if (!available || !USE_MANUAL_EXPOSURE) return;

        while (!opMode.isStopRequested()
                && portal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            opMode.sleep(20);
        }
        if (opMode.isStopRequested()) return;

        try {
            ExposureControl exposure = portal.getCameraControl(ExposureControl.class);
            if (exposure.getMode() != ExposureControl.Mode.Manual) {
                exposure.setMode(ExposureControl.Mode.Manual);
                opMode.sleep(50);
            }
            exposure.setExposure(EXPOSURE_MS, TimeUnit.MILLISECONDS);
            opMode.sleep(20);
            portal.getCameraControl(GainControl.class).setGain(CAMERA_GAIN);
            opMode.sleep(20);
        } catch (Exception e) {
            opMode.telemetry.addLine("Η κάμερα δεν δέχεται χειροκίνητη έκθεση");
        }
    }

    public void close() {
        if (portal != null) portal.close();
    }

    // ---------------------------------------------------------------- εσωτερικά

    /** Cluster της συμμαχίας μας ΚΑΙ όρθιο (|roll| < 90). Μένει στο ίδιο όσο φαίνεται. */
    private AprilTagClusterDetection findScorableCell(List<AprilTagDetection> detections) {
        String prefix = redAlliance ? "R" : "B";
        AprilTagClusterDetection best = null;

        for (AprilTagDetection d : detections) {
            if (!(d instanceof AprilTagClusterDetection)) continue;
            AprilTagClusterDetection c = (AprilTagClusterDetection) d;
            if (c.ftcPose == null) continue;

            String name = c.metadata.name;
            if (name == null || !name.toUpperCase().startsWith(prefix)) continue;
            if (Math.abs(c.ftcPose.roll) >= 90) continue;

            if (name.equals(trackedName)) return c;
            if (best == null || horizontalRange(c) < horizontalRange(best)) best = c;
        }

        if (best != null && !best.metadata.name.equals(trackedName)) {
            trackedName = best.metadata.name;
            haveFilter = false;
        }
        return best;
    }

    private static double horizontalRange(AprilTagDetection d) {
        return Math.hypot(d.ftcPose.x, d.ftcPose.y);
    }
}
