package base;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.BitSet;

public class Input {
    /**
     * Bitset which registers if any {@link KeyCode} keeps being pressed or if it is
     * released.
     */
    private BitSet keyboardBitSet = new BitSet();
    private BitSet mouseBitSet = new BitSet();

    private Scene scene = null;

    public Input(Scene scene) {
        this.scene = scene;
    }

    public void addListeners() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedEventHandler);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedEventHandler);

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedEventHandler);
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
    }

    public void removeListeners() {
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedEventHandler);
        scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleasedEventHandler);

        scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedEventHandler);
        scene.removeEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
    }

    /**
     * "Key Pressed" handler for all input events: register pressed key in the
     * bitset
     */
    private EventHandler<KeyEvent> keyPressedEventHandler = event -> {
        // register key down
        keyboardBitSet.set(event.getCode().ordinal(), true);
        event.consume();
    };

    /**
     * "Key Released" handler for all input events: unregister released key in the
     * bitset
     */
    private EventHandler<KeyEvent> keyReleasedEventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            // register key up
            keyboardBitSet.set(event.getCode().ordinal(), false);
            event.consume();
        }
    };

    private EventHandler<MouseEvent> mousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            mouseBitSet.set(mouseEvent.getButton().ordinal(), true);
            mouseEvent.consume();
        }
    };

    private EventHandler<MouseEvent> mouseReleasedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            mouseBitSet.set(mouseEvent.getButton().ordinal(), false);
            mouseEvent.consume();
        }
    };


    // -------------------------------------------------
    // Evaluate bitset of pressed keys and return the player input.
    // If direction and its opposite direction are pressed simultaneously, then the
    // direction isn't handled.
    // -------------------------------------------------

    public boolean isKeyPressed(KeyCode keyCode) {
        return keyboardBitSet.get(keyCode.ordinal());
    }

    public boolean isMouseButtonPressed(MouseButton mouseButton) {
        return mouseBitSet.get(mouseButton.ordinal());
    }
}
