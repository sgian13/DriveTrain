package org.firstinspires.ftc.teamcode.grobo.subsystems;

import static org.firstinspires.ftc.teamcode.grobo.RobotConfig.*;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/*
 * BOX (mB)
 *   forward() / reverse() / stop()   χειροκίνητα με σταθερή ισχύ
 *   setPower(p)                      οποιαδήποτε ισχύς −1..1
 *   goTo(ticks)                      πάει σε θέση encoder (π.χ. για ανύψωση)
 *   position()                       τωρινή θέση encoder
 */
public class Box {

    private final DcMotor motor;

    public Box(LinearOpMode opMode) {
        motor = opMode.hardwareMap.tryGet(DcMotor.class, M_BOX);
        if (motor == null) return;
        motor.setDirection(BOX_REVERSED ? DcMotorSimple.Direction.REVERSE
                                        : DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public boolean isAvailable() { return motor != null; }

    public void forward() { setPower(BOX_POWER); }
    public void reverse() { setPower(-BOX_POWER); }
    public void stop()    { setPower(0); }

    public void setPower(double power) {
        if (motor == null) return;
        if (motor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        motor.setPower(power);
    }

    /** Πηγαίνει σε θέση encoder και την κρατά. */
    public void goTo(int ticks) {
        if (motor == null) return;
        motor.setTargetPosition(ticks);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(BOX_POWER);
    }

    public boolean isBusy()  { return motor != null && motor.isBusy(); }
    public int     position(){ return motor == null ? 0 : motor.getCurrentPosition(); }
}
