package net.benmclean.fourcolorpaletteswapshader.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import java.io.File;

/**
 * Created by Benjamin on 12/2/2016.
 */
public class ArtPacker {
    public static void main(final String[] args) throws Exception {
        TexturePacker.process("../assets-raw", ".", "art");
    }

    private static void delete(final File delete) {
        if (delete.isDirectory()) {
            for (final File file : delete.listFiles()) {
                delete(file);
            }
        }
        delete.delete();
    }
}