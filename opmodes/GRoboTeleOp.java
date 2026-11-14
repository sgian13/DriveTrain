package org.firstinspires.ftc.teamcode.grobo.opmodes;

import static org.firstinspires.ftc.teamcode.grobo.RobotConfig.SINGLE_GAMEPAD;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.grobo.GRobot;
import org.firstinspires.ftc.teamcode.grobo.subsystems.Drive;

/*
 * TELEOP ΑΓΩΝΑ: ένα ή δύο χειριστήρια (SINGLE_GAMEPAD στο RobotConfig)
 *
 * INIT (gamepad1): DPAD ← ΜΠΛΕ, DPAD → ΚΟΚΚΙΝΗ
 *
 * Φωνή Driver Hub: "target found" = βλέπει CELL με αναμμένο flywheel (πάτα RB),
 *                  "shoot"        = σημαδεμένο, έτοιμο για βολή
 *
 * ===== ΕΝΑ ΧΕΙΡΙΣΤΗΡΙΟ (SINGLE_GAMEPAD = true): όλα στο gamepad 1 =====
 *   Αριστερό stick  κίνηση
 *   Δεξί stick      στροφή
 *   LB (κράτα)      αργή κίνηση ακρίβειας
 *   RB (κράτα)      σημάδεμα στο CELL
 *   RT (κράτα)      βολή από όποιο flywheel είναι αναμμένο
 *   X               intake μέσα ON/OFF
 *   A               intake έξω ON/OFF
 *   Y               flywheel NECTAR ON/OFF
 *   B               flywheel POLLEN ON/OFF
 *   DPAD ↑ / ↓      box
 *   Σειρά βολής: Y (ή X) -> "target found" -> κράτα RB -> "shoot" -> RT
 *
 * ===== ΔΥΟ ΧΕΙΡΙΣΤΗΡΙΑ (SINGLE_GAMEPAD = false) =====
 * GAMEPAD 1 - ΟΔΗΓΟΣ
 *   Αριστερό stick  κίνηση
 *   Δεξί stick      στροφή
 *   LB (κράτα)      αργή κίνηση ακρίβειας
 *   RB (κράτα)      σημάδεμα στο CELL (δόνηση όταν σημαδέψει)
 *   X               intake μέσα ON/OFF
 *   A               intake έξω ON/OFF
 *
 * GAMEPAD 2 - ΧΕΙΡΙΣΤΗΣ ΜΗΧΑΝΙΣΜΩΝ
 *   DPAD ↑ / ↓      box
 *   Y               flywheel NECTAR ON/OFF
 *   B               flywheel POLLEN ON/OFF
 *   RB (κράτα)      βολή NECTAR (flower + servo)
 *   LB (κράτα)      βολή POLLEN (flower + servo)
 */
@TeleOp(name = "GRoboFTC - TeleOp", group = "GRoboFTC")
public class GRoboTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        GRobot robot = new GRobot(this);
        robot.vision.setManualExposure();

        // ----- INIT: επιλογή συμμαχίας + έλεγχος hardware -----
        while (opModeInInit()) {
            if (gamepad1.dpad_left)  robot.vision.setAlliance(false);
            if (gamepad1.dpad_right) robot.vision.setAlliance(true);
            telemetry.addData("Συμμαχία", robot.vision.isRed() ? "ΚΟΚΚΙΝΗ" : "ΜΠΛΕ");
            telemetry.addLine("DPAD ← ΜΠΛΕ, DPAD → ΚΟΚΚΙΝΗ");
            telemetry.addData("Χειριστήρια", SINGLE_GAMEPAD ? "ΕΝΑ (gamepad 1)" : "ΔΥΟ");
            telemetry.addLine("");
            telemetry.addData("Κάμερα", robot.vision.isAvailable() ? "OK" : "--");
            telemetry.addData("Intake", robot.intake.isAvailable() ? "OK" : "--");
            telemetry.addData("Box", robot.box.isAvailable() ? "OK" : "--");
            telemetry.addData("Nectar", robot.nectar.status());
            telemetry.addData("Pollen", robot.pollen.status());
            telemetry.update();
            sleep(20);
        }

        boolean wasAimed = false;
        boolean lastNectar = false, lastPollen = false;
        boolean lastIntakeIn = false, lastIntakeOut = false;
        int intakeState = 0;   // 0 = OFF, 1 = ΜΕΣΑ, -1 = ΕΞΩ
        boolean targetSpoken = false, aimSpoken = false;
        ElapsedTime sinceTarget = new ElapsedTime();

        while (opModeIsActive()) {
            robot.vision.update();

            // ----- Φωνή: «target found» μία φορά όταν βλέπει CELL ΚΑΙ είναι αναμμένο flywheel -----
            boolean readyToShoot = robot.nectar.isRunning() || robot.pollen.isRunning();
            if (readyToShoot && robot.vision.seesTarget()) {
                sinceTarget.reset();
                if (!targetSpoken) { telemetry.speak("target found"); targetSpoken = true; }
            } else if (sinceTarget.seconds() > 1.0) {
                targetSpoken = false;   // το ξαναλέει μόνο αν χαθεί (ή σβήσει το flywheel) για >1 δευτ.
            }

            // ===================== GAMEPAD 1: οδήγηση / σημάδεμα =====================
            if (gamepad1.right_bumper) {
                boolean aimed = robot.aim.step(Drive.deadzone(gamepad1.left_stick_x));
                if (aimed && !wasAimed) gamepad1.rumble(300);
                if (aimed && !aimSpoken) { telemetry.speak("shoot"); aimSpoken = true; }
                wasAimed = aimed;
                telemetry.addLine(aimed ? ">>> ΣΗΜΑΔΕΜΕΝΟ <<<" : "ΣΗΜΑΔΕΜΑ...");
            } else {
                robot.aim.reset();
                robot.drive.driveManual(gamepad1, gamepad1.left_bumper);
                wasAimed = false;
                aimSpoken = false;
            }
            telemetry.addData("Ακρίβεια (LB)", gamepad1.left_bumper ? "ON - αργά" : "OFF");

            // ===================== ΜΗΧΑΝΙΣΜΟΙ =====================
            // Intake: πάντα στο gamepad 1, ON/OFF με πάτημα (X = μέσα, A = έξω)
            boolean pressIn  = gamepad1.x;
            boolean pressOut = gamepad1.a;

            if (pressIn && !lastIntakeIn) {
                intakeState = (intakeState == 1) ? 0 : 1;
            }
            if (pressOut && !lastIntakeOut) {
                intakeState = (intakeState == -1) ? 0 : -1;
            }
            lastIntakeIn  = pressIn;
            lastIntakeOut = pressOut;

            boolean intakeIn  = intakeState == 1;
            boolean intakeOut = intakeState == -1;

            // Τα υπόλοιπα διαβάζονται από το σωστό χειριστήριο
            boolean boxUp, boxDown, toggleNectar, togglePollen, fireNectar, firePollen;
            if (SINGLE_GAMEPAD) {
                boxUp        = gamepad1.dpad_up;
                boxDown      = gamepad1.dpad_down;
                toggleNectar = gamepad1.y;
                togglePollen = gamepad1.b;
                boolean rt   = gamepad1.right_trigger > 0.5;   // βολή από όποιο flywheel είναι αναμμένο
                fireNectar   = rt && robot.nectar.isRunning();
                firePollen   = rt && robot.pollen.isRunning();
            } else {
                boxUp        = gamepad2.dpad_up;
                boxDown      = gamepad2.dpad_down;
                toggleNectar = gamepad2.y;
                togglePollen = gamepad2.b;
                fireNectar   = gamepad2.right_bumper;
                firePollen   = gamepad2.left_bumper;
            }

            // Intake
            if      (intakeIn)  robot.intake.in();
            else if (intakeOut) robot.intake.out();
            else                robot.intake.stop();

            // Box
            if      (boxUp)   robot.box.forward();
            else if (boxDown) robot.box.reverse();
            else              robot.box.stop();

            // Flywheels: εναλλαγή ON/OFF μόνο τη στιγμή του πατήματος
            if (toggleNectar && !lastNectar) robot.nectar.toggle();
            if (togglePollen && !lastPollen) robot.pollen.toggle();
            lastNectar = toggleNectar;
            lastPollen = togglePollen;

            // Βολή (όσο κρατάς το κουμπί)
            boolean firingNectar = robot.nectar.fire(fireNectar);
            boolean firingPollen = robot.pollen.fire(firePollen);

            // ===================== ΟΘΟΝΗ =====================
            telemetry.addData("Intake", intakeIn ? "ΜΕΣΑ" : intakeOut ? "ΕΞΩ" : "OFF");
            telemetry.addData("Στόχος", robot.vision.targetName() + " (" + robot.vision.tagsSeen() + ")");
            if (robot.vision.hasTarget()) {
                telemetry.addData("Bearing / yaw / απόσταση", "%.1f° / %.1f° / %.0f cm",
                        robot.vision.bearing(), robot.vision.yaw(), robot.vision.rangeCm());
            }
            telemetry.addData("NECTAR", "%s %.0f/%.0f RPM %s%s",
                    robot.nectar.isRunning() ? "ON " : "OFF",
                    robot.nectar.currentRpm(), robot.nectar.targetRpm(),
                    robot.nectar.isReady() ? "ΕΤΟΙΜΟΣ" : "",
                    firingNectar ? " ΒΟΛΗ" : "");
            telemetry.addData("POLLEN", "%s %.0f/%.0f RPM %s%s",
                    robot.pollen.isRunning() ? "ON " : "OFF",
                    robot.pollen.currentRpm(), robot.pollen.targetRpm(),
                    robot.pollen.isReady() ? "ΕΤΟΙΜΟΣ" : "",
                    firingPollen ? " ΒΟΛΗ" : "");
            telemetry.addData("Γωνία", "%.1f°", robot.drive.heading());
            telemetry.update();
        }

        robot.stopAll();
        robot.vision.close();
    }
}