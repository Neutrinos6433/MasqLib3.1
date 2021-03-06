package BasicLib4997.MasqMotors.MasqRobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcontroller.internal.FtcOpModeRegister;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Locale;

import BasicLib4997.DashBoard;
import BasicLib4997.MasqHardware;
import BasicLib4997.MasqMotors.MasqMotor;
import BasicLib4997.MasqMotors.MasqTankDrive;
import BasicLib4997.MasqMotors.NeutDriveTrain;
import BasicLib4997.MasqSensors.AdafruitIMU;
import BasicLib4997.MasqSensors.MasqClock;
import BasicLib4997.MasqSensors.MasqColorSensor;
import BasicLib4997.MasqSensors.MasqRangeSensor;
import BasicLib4997.MasqSensors.MasqODS;
import BasicLib4997.MasqSensors.Sensor_Thresholds;
import BasicLib4997.MasqServos.MasqCRServo;
import BasicLib4997.MasqServos.MasqServo;
import static BasicLib4997.MasqMotors.MasqTankDrive.convert;

/**
 * Created by Archish on 10/28/16.
 */

public class MasqRobot implements Constants, Sensor_Thresholds, MasqHardware {
    DashBoard dash;
    public MasqRobot(Telemetry telemetry){
        this.telemetry  = telemetry;
        instance = this;
    }
    public static MasqRobot getTelemetry(){
        return instance;
    }
    private static MasqRobot instance;
    private Telemetry telemetry;
    public void addTelemetry(String string) {
        telemetry.addLine(string);
    }
    public void addTelemetry(String string, Object data) {
        telemetry.addData(string, data);
    }
    public void addSticky(String string){
        telemetry.log().add(string);
        telemetry.update();
    }
    public void addSticky(String string, Object data){
        telemetry.log().add(string, data);
        telemetry.update();
    }

    // MasqMotor and MasqMotor Systems
    public NeutDriveTrain driveTrain = new NeutDriveTrain("motor_left", "motor_right");
    public MasqMotor shooter = new MasqMotor("motor_shoot");
    ///MasqClock
    //Servos
    public MasqServo rightPresser = new MasqServo("servo_blue");
    public MasqServo leftPresser = new MasqServo("servo_red");
    public MasqMotor collector = new MasqMotor("motor_sweep");
    public MasqServo indexer = new MasqServo("ball_stop");
    //IMU
    public AdafruitIMU imu = new AdafruitIMU("imu");
    public MasqODS ods = new MasqODS("ODS");
    public MasqColorSensor leftColor = new MasqColorSensor("sensor_color", 60);
    //RangeSensor
    private static final int DEFAULT_SLEEP_TIME = 500;
    private static final double DEFAULT_TIMEOUT = 3;
    public double angleLeftCover = 0;
    private boolean opModeIsActive() {
        return ((LinearOpMode) (FtcOpModeRegister.opModeManager.getActiveOpMode())).opModeIsActive();
    }
    public void drive(double power, int distance, Direction DIRECTION, int sleepTime, double targetAngle) {
        double angle = targetAngle;
        int newDistance = convert(distance);
        driveTrain.resetEncoders();
        driveTrain.setDistance((int)((-newDistance) * DIRECTION.value));
        driveTrain.runToPosition();
        while (driveTrain.rightIsBusy() && opModeIsActive()) {
            double newPowerLeft = power;
            double imuVal = imu.getHeading();
            double error = angle - imuVal;
            double errorkp = error * KP_STRAIGHT;
            newPowerLeft = (newPowerLeft - (errorkp) * DIRECTION.value);
            driveTrain.setPowerRight(power);
            driveTrain.setPowerLeft(newPowerLeft);
            MasqRobot.getTelemetry().addTelemetry("Heading", imuVal);
            MasqRobot.getTelemetry().addTelemetry("DistanceLeft", newDistance + driveTrain.getCurrentPos());
            telemetry.update();
        }
        driveTrain.StopDriving();
        driveTrain.runUsingEncoder();
        sleep(sleepTime);
    }
    public void drive(double power, int distance, double targetAngle, Direction DIRECTION) {
        drive(power, distance, DIRECTION, DEFAULT_SLEEP_TIME, targetAngle);
    }
    public void drive(double power, int distance, Direction DIRECTION, int sleepTime) {
        double targetAngle = imu.getHeading();
        int newDistance = convert(distance);
        driveTrain.resetEncoders();
        driveTrain.setDistance((int)((-newDistance) * DIRECTION.value));
        driveTrain.runToPosition();
        while (driveTrain.rightIsBusy() && opModeIsActive()) {
            double newPowerLeft = power;
            double imuVal = imu.getHeading();
            double error = targetAngle - imuVal;
            double errorkp = error * KP_STRAIGHT;
            newPowerLeft = (newPowerLeft - (errorkp) * DIRECTION.value);
            driveTrain.setPowerRight(power);
            driveTrain.setPowerLeft(newPowerLeft);
            MasqRobot.getTelemetry().addTelemetry("Heading", imuVal);
            MasqRobot.getTelemetry().addTelemetry("DistanceLeft", newDistance + driveTrain.getCurrentPos());
            telemetry.update();
        }
        driveTrain.StopDriving();
        driveTrain.runUsingEncoder();
        sleep(sleepTime);
    }
    public void drive(double power, int distance, Direction DIRECTION) {
        drive(power, distance, DIRECTION, DEFAULT_SLEEP_TIME);
    }
    public void turn(int angle, Direction DIRECTION, double timeout, double ki) {
        double targetAngle = imu.adjustAngle(imu.getHeading() + (DIRECTION.value * angle));
        double acceptableError = 0.5;
        double currentError = 1;
        double prevError = 0;
        double integral = 0;
        double newPower = 0;
        double previousTime = 0;
        MasqClock clock = new MasqClock("clock");
        while (opModeIsActive() && (imu.adjustAngle(Math.abs(currentError)) > acceptableError) && !clock.elapsedTime(timeout, MasqClock.Resolution.SECONDS)) {
            double tChange = System.nanoTime() - previousTime;
            previousTime = System.nanoTime();
            tChange = tChange / 1e9;
            double imuVAL = imu.getHeading();
            currentError = imu.adjustAngle(targetAngle - imuVAL);
            integral += currentError  * ID;
            double errorkp = currentError * KP_TURN;
            double integralki = currentError * ki * tChange;
            double dervitive = (currentError - prevError) / tChange;
            double dervitivekd = dervitive * KD_TURN;
            newPower = (errorkp + integralki + dervitivekd);
            driveTrain.setPowerRight(-newPower);
            driveTrain.setPowerLeft(newPower);
            prevError = currentError;
            MasqRobot.getTelemetry().addTelemetry("TargetAngle", targetAngle);
            MasqRobot.getTelemetry().addTelemetry("Heading", imuVAL);
            MasqRobot.getTelemetry().addTelemetry("AngleLeftToCover", currentError);
            telemetry.update();
        }
        driveTrain.StopDriving();
        sleep(1000);
    }
    public void turn(int angle, Direction DIRECTION, double timeOut, int sleepTime) {
        double targetAngle = imu.adjustAngle(imu.getHeading() + (DIRECTION.value * angle));
        double acceptableError = 0.5;
        double currentError = 1;
        double prevError = 0;
        double integral = 0;
        double newPower = 0;
        double previousTime = 0;
        MasqClock clock = new MasqClock("clock");
        while (opModeIsActive() && (imu.adjustAngle(Math.abs(currentError)) > acceptableError) && !clock.elapsedTime(timeOut, MasqClock.Resolution.SECONDS)) {
            double tChange = System.nanoTime() - previousTime;
            previousTime = System.nanoTime();
            tChange = tChange / 1e9;
            double imuVAL = imu.getHeading();
            currentError = imu.adjustAngle(targetAngle - imuVAL);
            integral += currentError  * ID;
            double errorkp = currentError * KP_TURN;
            double integralki = currentError * KI_TURN * tChange;
            double dervitive = (currentError - prevError) / tChange;
            double dervitivekd = dervitive * KD_TURN;
            newPower = (errorkp + integralki + dervitivekd);
            driveTrain.setPowerRight(-newPower);
            driveTrain.setPowerLeft(newPower);
            prevError = currentError;
            MasqRobot.getTelemetry().addTelemetry("TargetAngle", targetAngle);
            MasqRobot.getTelemetry().addTelemetry("Heading", imuVAL);
            MasqRobot.getTelemetry().addTelemetry("AngleLeftToCover", currentError);
            angleLeftCover = currentError;
            telemetry.update();
        }
        driveTrain.StopDriving();
        sleep(sleepTime);
    }
    public void turn( int angle, Direction DIRECTION, double timeout)  {
        turn(angle, DIRECTION, timeout, DEFAULT_SLEEP_TIME);
    }
    public void turn(int angle, Direction DIRECTION)  {
        turn(angle, DIRECTION, DEFAULT_TIMEOUT);
    }
    public void setBrakeMode(int time) {
        driveTrain.setBrakeMode();
        sleep(time);
    }
    public void stopRed(double power, Direction Direction) {
        driveTrain.runUsingEncoder();
        double targetAngle = imu.getHeading();
        while (!(leftColor.isRed()) && opModeIsActive()) {
            double newPower = power;
            double heading = imu.getHeading();
            double error = targetAngle - heading;
            double errorkp = error * KP_STRAIGHT;
            newPower = newPower - (errorkp * Direction.value);
            driveTrain.setPowerLeft(power * Direction.value);
            driveTrain.setPowerRight(newPower * Direction.value);
            MasqRobot.getTelemetry().addTelemetry("Heading", heading);
            MasqRobot.getTelemetry().addTelemetry("red Val", leftColor.colorNumber());
            telemetry.update();
        }
        driveTrain.StopDriving();
    }
    public void stopBlue(double power, Direction Direction) {
        driveTrain.runUsingEncoder();
        double targetAngle = imu.getHeading();
        while ((!leftColor.isBlue()) && opModeIsActive()){
            double newPower = power;
            double heading = imu.getHeading();
            double error = targetAngle - heading;
            double errorkp = error * KP_STRAIGHT;
            newPower = newPower - (errorkp * Direction.value);
            driveTrain.setPowerLeft(power * Direction.value);
            driveTrain.setPowerRight(newPower * Direction.value);
            MasqRobot.getTelemetry().addTelemetry("Heading", heading);
            MasqRobot.getTelemetry().addTelemetry("Blue Val", leftColor.colorNumber());
            telemetry.update();
        }
        driveTrain.StopDriving();
    }
    public void stopRedRight(double power, Direction Direction) {
        driveTrain.runUsingEncoder();
        double targetAngle = imu.getHeading();
        while (!(leftColor.isRed()) && opModeIsActive()) {
            double newPower = power;
            double heading = imu.getHeading();
            double error = targetAngle - heading;
            double errorkp = error * KP_STRAIGHT;
            newPower = newPower - (errorkp * Direction.value);
            driveTrain.setPowerLeft(power * Direction.value);
            driveTrain.setPowerRight(newPower * Direction.value);
            MasqRobot.getTelemetry().addTelemetry("Heading", heading);
            MasqRobot.getTelemetry().addTelemetry("red Val", leftColor.colorNumber());
            telemetry.update();
        }
        driveTrain.StopDriving();
    }
    public void stopBlueRight(double power, Direction Direction) {
        driveTrain.runUsingEncoder();
        double targetAngle = imu.getHeading();
        while ((!leftColor.isBlue()) && opModeIsActive()){
            double newPower = power;
            double heading = imu.getHeading();
            double error = targetAngle - heading;
            double errorkp = error * KP_STRAIGHT;
            newPower = newPower - (errorkp * Direction.value);
            driveTrain.setPowerLeft(power * Direction.value);
            driveTrain.setPowerRight(newPower * Direction.value);
            MasqRobot.getTelemetry().addTelemetry("Heading", heading);
            MasqRobot.getTelemetry().addTelemetry("Blue Val", leftColor.colorNumber());
            telemetry.update();
        }
        driveTrain.StopDriving();
    }
    public void stopWhite (double power, Direction Direction) {
        driveTrain.runUsingEncoder();
        double targetAngle = imu.getHeading();
        while ((!ods.isWhite()) && opModeIsActive()) {
            double newPower = power;
            double heading = imu.getHeading();
            double error = targetAngle - heading;
            double errorkp = error * KP_STRAIGHT;
            newPower = newPower - (errorkp * Direction.value);
            driveTrain.setPowerLeft(power * Direction.value);
            driveTrain.setPowerRight(newPower * Direction.value);
            MasqRobot.getTelemetry().addTelemetry("Heading", heading);
            MasqRobot.getTelemetry().addTelemetry("red Val", leftColor.colorNumber());
            telemetry.update();
        }
        driveTrain.StopDriving();
    }
    //rangeSensor
    //setPower
    public void sleep() {
        sleep(1000);
    }
    public void sleep(int time) {
        try {
            Thread.sleep((long) time);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    public String getName() {
        return "Robot";
    }

    public String getDash() {
        return imu.getDash() +  leftColor.getDash() +  shooter.getDash() + collector.getDash() + driveTrain.getDash();
    }
}
