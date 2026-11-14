package org.firstinspires.ftc.teamcode.grobo.subsystems;

import static org.firstinspires.ftc.teamcode.grobo.RobotConfig.*;

import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * ΣΗΜΑΔΕΜΑ: συνδυάζει κάμερα + οδήγηση.
 *
 *   boolean aimed = robot.aim.step(strafe);   σε κάθε κύκλο, όσο σημαδεύουμε
 *   robot.aim.reset();                         όταν σταματάμε να σημαδεύουμε
 *
 * Στρίβει ώστε bearing = 0 και πάει στην απόσταση AIM_TARGET_RANGE_CM.
 * Με AIM_SQUARE_TO_TAG κινείται και ΠΛΑΓΙΑ μέχρι yaw = 0 (απέναντι και κάθετα στο CELL).
 * Αλλιώς (ή όταν δεν φαίνεται στόχος) το πλάγιο (strafe) το δίνει ο οδηγός.
 */
public class Aim {

    private final Drive drive;
    private final Vision vision;

    private double cmdForward = 0, cmdTurn = 0, cmdStrafe = 0;
    private final ElapsedTime loopTimer = new ElapsedTime();

    public Aim(Drive drive, Vision vision) {
        this.drive = drive;
        this.vision = vision;
    }

    public void reset() {
        cmdForward = 0;
        cmdTurn = 0;
        cmdStrafe = 0;
        vision.resetTracking();
        loopTimer.reset();
    }

    /** Ένα βήμα σημαδέματος. Επιστρέφει true όταν είναι σημαδεμένο. */
    public boolean step(double strafe) {
        double dt = Math.max(loopTimer.seconds(), 0.001);
        loopTimer.reset();

        double tForward = 0, tTurn = 0;
        double tStrafe = strafe;   // χωρίς στόχο: το πλάγιο το κάνει ο οδηγός
        boolean autoStrafe = false;
        boolean aimed = false;

        if (vision.hasTarget()) {
            double bearingErr = vision.bearing();
            double rangeErr   = vision.rangeCm() - AIM_TARGET_RANGE_CM;
            double yawErr     = vision.yaw();

            boolean bearingOk = Math.abs(bearingErr) < AIM_BEARING_TOL_DEG;
            boolean rangeOk   = !AIM_USE_RANGE || Math.abs(rangeErr) < AIM_RANGE_TOL_CM;
            boolean yawOk     = !AIM_SQUARE_TO_TAG || Math.abs(yawErr) < AIM_YAW_TOL_DEG;
            aimed = bearingOk && rangeOk && yawOk;

            tTurn = AIM_TURN_SIGN * -control(bearingErr, AIM_BEARING_TOL_DEG, AIM_TURN_KP, AIM_TURN_KS, AIM_TURN_MAX);
            if (AIM_USE_RANGE) {
                tForward = AIM_DRIVE_SIGN * control(rangeErr, AIM_RANGE_TOL_CM, AIM_DRIVE_KP, AIM_DRIVE_KS, AIM_DRIVE_MAX);
            }
            if (AIM_SQUARE_TO_TAG) {
                // πλάγια γύρω από το CELL μέχρι να το κοιτάει κάθετα (yaw = 0)
                tStrafe = -AIM_STRAFE_SIGN * control(yawErr, AIM_YAW_TOL_DEG, AIM_STRAFE_KP, AIM_STRAFE_KS, AIM_STRAFE_MAX);
                autoStrafe = true;
            }
        }

        double step = AIM_SLEW_PER_SEC * dt;
        cmdTurn    = slew(cmdTurn,    tTurn,    step);
        cmdForward = slew(cmdForward, tForward, step);
        // ομαλά μόνο όταν το πλάγιο είναι αυτόματο· του οδηγού περνάει αμέσως, όπως πριν
        cmdStrafe  = autoStrafe ? slew(cmdStrafe, tStrafe, step) : tStrafe;

        drive.drive(cmdStrafe, cmdForward, cmdTurn);
        return aimed;
    }

    private static double control(double err, double tol, double kp, double ks, double max) {
        if (Math.abs(err) < tol) return 0;
        return Math.signum(err) * Math.min(ks + kp * Math.abs(err), max);
    }

    private static double slew(double current, double target, double maxStep) {
        return current + Math.max(-maxStep, Math.min(maxStep, target - current));
    }
}
