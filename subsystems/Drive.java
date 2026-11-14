package org.firstinspires.ftc.teamcode.grobo.subsystems;

import static org.firstinspires.ftc.teamcode.grobo.RobotConfig.*;

import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/*
 * ΟΔΗΓΗΣΗ (mecanum + encoders + γυροσκόπιο)
 *
 *   drive(strafe, forward, turn)  άμεση εντολή
 *   driveManual(gamepad1)         οδήγηση με sticks
 *   driveCm(cm)                   ευθεία μπροστά (+) / πίσω (−)
 *   turnToHeading(deg)            στροφή σε απόλυτη γωνία (αριστερά = +)
 *   turnDeg(deg)                  στροφή κατά τόσες μοίρες (αριστερά = +)
 */
public class Drive {

    private final LinearOpMode opMode;
    private final Motor[] motors;
    private final MecanumDrive mecanum;
    private final IMU imu;

    public Drive(LinearOpMode opMode) {
        this.opMode = opMode;

        Motor fl = new Motor(opMode.hardwareMap, M_FRONT_LEFT);
        Motor fr = new Motor(opMode.hardwareMap, M_FRONT_RIGHT);
        Motor bl = new Motor(opMode.hardwareMap, M_BACK_LEFT);
        Motor br = new Motor(opMode.hardwareMap, M_BACK_RIGHT);
        motors = new Motor[]{fl, fr, bl, br};
        for (Motor m : motors) {
            m.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
            m.resetEncoder();
        }
        mecanum = new MecanumDrive(fl, fr, bl, br);

        imu = opMode.hardwareMap.get(IMU.class, IMU_NAME);
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(HUB_LOGO, HUB_USB)));
        imu.resetYaw();
    }

    // ---------------------------------------------------------------- βασικά

    /** strafe + = δεξιά, forward + = μπροστά, turn + = δεξιόστροφα. */
    public void drive(double strafe, double forward, double turn) {
        mecanum.driveRobotCentric(strafe, forward, turn);
    }

    public void stop() {
        mecanum.stop();
    }

    /** Αριστερό stick = κίνηση, δεξί stick = στροφή. */
    public void driveManual(Gamepad g) {
        drive(deadzone(g.left_stick_x), deadzone(-g.left_stick_y), deadzone(g.right_stick_x));
    }

    /** Όπως driveManual, αλλά με slow = true κινείται αργά για ακρίβεια. */
    public void driveManual(Gamepad g, boolean slow) {
        double move = slow ? SLOW_DRIVE : 1.0;
        double turn = slow ? SLOW_TURN  : 1.0;
        drive(curve(deadzone(g.left_stick_x))  * move,
                curve(deadzone(-g.left_stick_y)) * move,
                curve(deadzone(g.right_stick_x)) * turn);
    }

    private static double curve(double v) {
        return v * Math.abs(v);   // τετράγωνο με πρόσημο
    }

    // ---------------------------------------------------------------- αισθητήρες

    /** Γωνία σε μοίρες (αριστερά = θετικό). */
    public double heading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void resetHeading() {
        imu.resetYaw();
    }

    /** Μέση θέση των 4 τροχών σε εκατοστά. */
    public double positionCm() {
        double sum = 0;
        for (int i = 0; i < 4; i++) sum += ENC_SIGN[i] * motors[i].getCurrentPosition();
        return sum / 4.0 / TICKS_PER_CM;
    }

    /** Μηδενίζει τους 4 encoders. */
    public void resetEncoders() {
        for (Motor m : motors) m.resetEncoder();
    }

    public int encoder(int index) {
        return ENC_SIGN[index] * motors[index].getCurrentPosition();
    }

    // ---------------------------------------------------------------- αυτόματες κινήσεις

    /** Ευθεία μπροστά (+) ή πίσω (−) τόσα εκατοστά, κρατώντας την κατεύθυνση. */
    public void driveCm(double cm) {
        double start = positionCm();
        double target = start + cm;
        double targetHeading = heading();
        ElapsedTime t = new ElapsedTime();
        String result = "TIMEOUT: δεν έφτασε στον στόχο";

        while (opMode.opModeIsActive() && t.seconds() < DRIVE_TIMEOUT_S) {
            double pos = positionCm();
            double err = target - pos;

            // 1. Έφτασε
            if (Math.abs(err) < DRIVE_TOL_CM) { result = "ΕΦΤΑΣΕ"; break; }

            // 2. Ξεπέρασε τον στόχο (το σφάλμα άλλαξε πρόσημο) -> σταματά, δεν γυρίζει πίσω
            if (Math.signum(err) != Math.signum(cm)) { result = "ΞΕΠΕΡΑΣΕ τον στόχο"; break; }

            // 3. Ασφάλεια: μετά από 0,7 δευτ. οι encoders δεν έχουν μετρήσει σχεδόν τίποτα
            //    ή μετράνε προς τα ΠΙΣΩ -> λάθος ENC_SIGN ή αποσυνδεδεμένο καλώδιο encoder
            double moved = (pos - start) * Math.signum(cm);
            if (t.seconds() > 0.7 && moved < 1.0) {
                result = "ΣΤΟΠ: οι encoders δεν μετράνε σωστά (δες ENC_SIGN)";
                break;
            }

            double power = DRIVE_MAX_POWER * Math.min(1.0, Math.abs(err) / SLOW_DOWN_CM);
            power = Math.max(power, DRIVE_MIN_POWER);
            power = Math.min(power, DRIVE_MIN_POWER + ACCEL_PER_S * t.seconds());

            double headingErr = angleDiff(targetHeading, heading());
            double turn = clip(-HEADING_SIGN * HEADING_KP * headingErr, MAX_HEADING_TURN);

            drive(0, Math.signum(err) * power, turn);

            opMode.telemetry.addData("driveCm", "στόχος %.1f cm, διένυσε %.1f cm", cm, pos - start);
            opMode.telemetry.addData("Encoders FL/FR/BL/BR", "%d / %d / %d / %d",
                    encoder(0), encoder(1), encoder(2), encoder(3));
            opMode.telemetry.update();
        }
        stop();

        opMode.telemetry.addData("driveCm", result);
        opMode.telemetry.addData("Διένυσε", "%.1f cm από %.1f", positionCm() - start, cm);
        opMode.telemetry.addData("Encoders FL/FR/BL/BR", "%d / %d / %d / %d",
                encoder(0), encoder(1), encoder(2), encoder(3));
        opMode.telemetry.update();
        opMode.sleep(150);
    }

    /** Στροφή σε απόλυτη γωνία (μοίρες, αριστερά = +). */
    public void turnToHeading(double targetDeg) {
        ElapsedTime t = new ElapsedTime();
        while (opMode.opModeIsActive() && t.seconds() < TURN_TIMEOUT_S) {
            double err = angleDiff(targetDeg, heading());   // + = πρέπει να στρίψει αριστερά
            if (Math.abs(err) < TURN_TOL_DEG) break;

            double power = Math.min(TURN_MAX_POWER, TURN_MIN_POWER + TURN_KP * Math.abs(err));
            drive(0, 0, -HEADING_SIGN * Math.signum(err) * power);   // FTCLib: + = δεξιά
        }
        stop();
        opMode.sleep(150);
    }

    /** Στροφή κατά τόσες μοίρες από την τωρινή γωνία (αριστερά = +). */
    public void turnDeg(double deg) {
        turnToHeading(heading() + deg);
    }

    // ---------------------------------------------------------------- βοηθητικά

    public static double deadzone(double value) {
        if (Math.abs(value) < STICK_DEADZONE) return 0;
        return Math.signum(value) * (Math.abs(value) - STICK_DEADZONE) / (1.0 - STICK_DEADZONE);
    }

    /** Διαφορά γωνιών, πάντα από −180 έως +180. */
    public static double angleDiff(double target, double current) {
        double d = target - current;
        while (d > 180)  d -= 360;
        while (d < -180) d += 360;
        return d;
    }

    private static double clip(double v, double max) {
        return Math.max(-max, Math.min(max, v));
    }
}