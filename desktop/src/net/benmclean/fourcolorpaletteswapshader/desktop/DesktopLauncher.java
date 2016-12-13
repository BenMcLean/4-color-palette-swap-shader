package net.benmclean.fourcolorpaletteswapshader.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import net.benmclean.fourcolorpaletteswapshader.FourColorPaletteSwapShaderGame;

/** Launches the desktop (LWJGL) application. */
public class DesktopLauncher {
    public static void main(String[] args) {
        new LwjglApplication(new FourColorPaletteSwapShaderGame());
    }
}
