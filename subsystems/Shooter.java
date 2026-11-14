package org.firstinspires.ftc.teamcode.grobo.subsystems;

import static org.firstinspires.ftc.teamcode.grobo.RobotConfig.*;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * SHOOTER: flywheel + flower (τροφοδοσία) + servo που σπρώχνει.
 * Η ίδια κλάση χρησιμοποιείται για ΔΥΟ shooters: Nectar και Pollen
 * (δες GRobot, όπου φτιάχνονται με τα δικά τους ονόματα και ρυθμίσεις).
 *
 *   spinUp() / stop()   flywheel στις στροφές του / σβήσιμο
 *   isReady()           έφτασε στις στροφές;
 *   fire(true/false)    true: flower τροφοδοτεί + servo σπρώχνει
 *                       false: flower σταματά + servo σε ηρεμία
 */
public class Shooter {

    private final String    label;
    private final DcMotorEx flywheel;
    private final DcMotor   flower;
    private final Servo     pusher;

    private final double rpm;
    private final double flowerPower;
    private final double servoRest, servoPush;
    private boolean running = false;

    public Shooter(LinearOpMode opMode, String label,
                   String flywheelName, boolean flywheelReversed, double rpm,
                   String flowerName, boolean flowerReversed, double flowerPower,
                   String servoName, double servoRest, double servoPush) {
        this.label = label;
        this.rpm = rpm;
        this.flowerPower = flowerPower;
        this.servoRest = servoRest;
        this.servoPush = servoPush;

        flywheel = opMode.hardwareMap.tryGet(DcMotorEx.class, flywheelName);
        if (flywheel != null) {
            flywheel.setDirection(flywheelReversed ? DcMotorSimple.Direction.REVERSE
                                                   : DcMotorSimple.Direction.FORWARD);
            flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            flywheel.setVelocityPIDFCoefficients(FLYWHEEL_P, 0, 0, FLYWHEEL_F);
        }

        flower = opMode.hardwareMap.tryGet(DcMotor.class, flowerName);
        if (flower != null) {
            flower.setDirection(flowerReversed ? DcMotorSimple.Direction.REVERSE
                                               : DcMotorSimple.Direction.FORWARD);
            flower.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        pusher = opMode.hardwareMap.tryGet(Servo.class, servoName);
        if (pusher != null) pusher.setPosition(servoRest);
    }

    public String label() { return label; }

    /** Ποια κομμάτια βρέθηκαν στο configuration (για έλεγχο στο INIT). */
    public String status() {
        return "FW " + (flywheel != null ? "OK" : "--")
             + "  Flower " + (flower != null ? "OK" : "--")
             + "  Servo " + (pusher != null ? "OK" : "--");
    }

    // ---------------------------------------------------------------- flywheel

    public void spinUp() {
        running = true;
        if (flywheel != null) flywheel.setVelocity(rpm / 60.0 * FLYWHEEL_TICKS_PER_REV);
    }

    public void stop() {
        running = false;
        if (flywheel != null) flywheel.setVelocity(0);
        fire(false);
    }

    public void toggle() {
        if (running) stop(); else spinUp();
    }

    public boolean isRunning() { return running; }

    public double currentRpm() {
        return flywheel == null ? 0 : Math.abs(flywheel.getVelocity()) / FLYWHEEL_TICKS_PER_REV * 60.0;
    }

    public double targetRpm() { return running ? rpm : 0; }

    public boolean isReady() {
        return running && Math.abs(currentRpm() - rpm) < FLYWHEEL_RPM_TOL;
    }

    // ---------------------------------------------------------------- βολή

    /**
     * true: τροφοδοτεί και σπρώχνει, false: σταματά.
     * Με FIRE_ONLY_WHEN_READY δεν ρίχνει αν το flywheel δεν έχει στροφές.
     * Επιστρέφει αν όντως ρίχνει.
     */
    public boolean fire(boolean active) {
        boolean allowed = active && (!FIRE_ONLY_WHEN_READY || isReady());
        if (flower != null) flower.setPower(allowed ? flowerPower : 0);
        if (pusher != null) pusher.setPosition(allowed ? servoPush : servoRest);
        return allowed;
    }

    /** Για δοκιμές: μετακινεί το servo σε θέση 0..1 (βρίσκετε REST και PUSH). */
    public void setServo(double position) {
        if (pusher != null) pusher.setPosition(position);
    }
}
