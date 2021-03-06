package BasicLib4997;

import BasicLib4997.MasqSensors.MasqClock;

/**
 * Created by Archish on 3/3/17.
 */

public class Controller {
    MasqClock clock = new MasqClock();
    public Controller(com.qualcomm.robotcore.hardware.Gamepad gamepad){
        this.gamepad  = gamepad;
        instance = this;
    }
    public static Controller getController(){
        return instance;
    }
    private static Controller instance;
    private com.qualcomm.robotcore.hardware.Gamepad gamepad;
    boolean a() {
        return gamepad.a;
    }
    boolean x() {
        return gamepad.x;
    }
    boolean y() {
        return gamepad.y;
    }
    boolean b() {
        return gamepad.b;
    }
    boolean aIsDoubleTapped () {
        boolean doubleTapped = false;
         if (aPressedAndRealeased() && !clock.elapsedTime(0.5 ,MasqClock.Resolution.SECONDS)){
            doubleTapped = a();
        }
        return doubleTapped;
    }
    boolean bIsDoubleTapped () {
        boolean doubleTapped = false;
        if (bPressedAndRealeased() && !clock.elapsedTime(0.5 ,MasqClock.Resolution.SECONDS)){
            doubleTapped = b();
        }
        return doubleTapped;
    }
    boolean xIsDoubleTapped () {
        boolean doubleTapped = false;
        if (xPressedAndRealeased() && !clock.elapsedTime(0.5 ,MasqClock.Resolution.SECONDS)){
            doubleTapped = x();
        }
        return doubleTapped;
    }
    boolean yIsDoubleTapped () {
        boolean doubleTapped = false;
        if (yPressedAndRealeased() && !clock.elapsedTime(0.5 ,MasqClock.Resolution.SECONDS)){
            doubleTapped = y();
        }
        return doubleTapped;
    }
    boolean aPressedAndRealeased () {
        boolean pressed = false, realeaed = false;
        while (a()) {
            realeaed = false;
            pressed = true;
        }
        while (!a()) {
            realeaed = true;
        }
        return pressed && realeaed;
    }
    boolean bPressedAndRealeased () {
        boolean pressed = false, realeaed = false;
        while (b()) {
            realeaed = false;
            pressed = true;
        }
        while (!b()) {
            realeaed = true;
        }
        return pressed && realeaed;
    }
    boolean yPressedAndRealeased () {
        boolean pressed = false, realeaed = false;
        while (b()) {
            realeaed = false;
            pressed = true;
        }
        while (!b()) {
            realeaed = true;
        }
        return pressed && realeaed;
    }
    boolean xPressedAndRealeased () {
        boolean pressed = false, realeaed = false;
        while (x()) {
            realeaed = false;
            pressed = true;
        }
        while (!x()) {
            realeaed = true;
        }
        return pressed && realeaed;
    }

    public float left_stick_x(){
        return gamepad.left_stick_x;
    }
    public float left_stick_y() {
     return gamepad.left_stick_y;
    }
    public float right_stick_x() {
        return gamepad.right_stick_x;
    }

    public float right_stick_y() {
        return gamepad.right_stick_y;
    }
    public boolean dpad_up() {
        return gamepad.dpad_up;
    }
    public boolean dpad_down() {
        return gamepad.dpad_down;
    }
    public boolean dpad_left() {
        return gamepad.dpad_left;
    }
    public boolean dpad_right() {
        return gamepad.dpad_right;
    }
    public boolean left_bumper() {
        return gamepad.left_bumper;
    }
    public boolean right_bumper() {
        return gamepad.right_bumper;
    }
    public boolean left_stick_button() {
        return gamepad.left_stick_button;
    }
    public boolean right_stick_button() {
        return gamepad.right_stick_button;
    }
    public boolean left_triggerb() {
        return gamepad.left_trigger > 0;
    }
    public boolean right_triggerb() {
        return gamepad.right_trigger > 0;
    }
    public float left_trigger() {
        return gamepad.left_trigger;
    }
    public float right_trigger() {
        return gamepad.right_trigger;
    }



}
