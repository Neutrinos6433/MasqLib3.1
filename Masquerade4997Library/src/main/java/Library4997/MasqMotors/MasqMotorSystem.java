package Library4997.MasqMotors;

import com.qualcomm.robotcore.hardware.DcMotor;

import Library4997.MasqHardware;
import Library4997.PID_Constants;

/**
 * MasqMotorSystem That supports two motora and treats them as one
 */
public class MasqMotorSystem implements PID_Constants, MasqHardware {
    private MasqMotor motor1 , motor2 = null;
    String systemName;
    public MasqMotorSystem(String name1, DcMotor.Direction direction, String name2, DcMotor.Direction directionOther, String systemName) {
        this.systemName = systemName;
        motor1 = new MasqMotor(name1, direction);
        motor2 = new MasqMotor(name2, directionOther);
    }
    public MasqMotorSystem(String name1, String name2, String systemName) {
        this.systemName = systemName;
        motor1 = new MasqMotor(name1, DcMotor.Direction.FORWARD);
        motor2 = new MasqMotor(name2, DcMotor.Direction.FORWARD);
    }

    public void resetEncoders () {
        motor1.resetEncoder();
        motor2.resetEncoder();
    }
    public void setPower (double power) {
        motor1.setPower(power);
        motor2.setPower(power);
    }
    public void setDistance(int distance){
        motor1.setDistance(distance);
        motor2.setDistance(distance);
    }
    public void runUsingEncoder() {
        motor1.runUsingEncoder();
        motor2.runUsingEncoder();
    }
    public void runToPosition(){
        motor1.runToPosition();
        motor2.runToPosition();

    }
    public void runWithoutEncoders() {
        motor1.runWithoutEncoders();
        motor2.runWithoutEncoders();

    }
    public void StopDriving() {
        setPower(0);
    }
    public void setBrakeMode () {
        motor1.setBrakeMode();
        motor2.setBrakeMode();
    }
    public static int convert(int TICKS) {
        return (int) ((TICKS * 35.1070765836));
    }
    boolean isStalled () {
        int i;
        boolean isStalled;
        if (motor1.isStalled()) i = 1;
        else if (motor2.isStalled()) i = 2;
        else i = 0;
        isStalled = i >= 1;
        return isStalled;
    }
    public boolean isBusy() {
        return (motor1.isBusy() && motor2.isBusy());
    }
    public double getCurrentPos () {
        double motor1Pos = motor1.getCurrentPos();
        double motor2Pos = motor2.getCurrentPos();
        return (motor1Pos + motor2Pos) / 2;
    }
    public String getName() {
        return systemName;
    }
    public String[] getDash() {
        return new String[]{ "Current Position" + getCurrentPos()};
    }
}