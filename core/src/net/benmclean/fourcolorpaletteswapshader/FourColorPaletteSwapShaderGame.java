package net.benmclean.fourcolorpaletteswapshader;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FourColorPaletteSwapShaderGame extends ApplicationAdapter {

    public static final int VIRTUAL_WIDTH = 64;
    public static final int VIRTUAL_HEIGHT = 64;
    public int playerX = 0, playerY = 0;

    private Color worldBackgroundColor;
    private Color screenBackgroundColor;
    public TextureAtlas atlas;
    public TextureAtlas.AtlasRegion test;
    public TextureAtlas.AtlasRegion test2;
    public Palette4 greyPalette;
    public Palette4 gameboyPalette;
    public ShaderProgram shader;
    public ShaderProgram shader2;
    private FrameBuffer frameBuffer;
    private SpriteBatch batch;
    private Viewport worldView;
    private Viewport screenView;
    private Texture screenTexture;
    private TextureRegion screenRegion;

    @Override
    public void create() {

        // fragmentShader is where the magic happens
        shader = new ShaderProgram(Palette4.vertexShader, Palette4.fragmentShader);
        shader2 = new ShaderProgram(Palette4.vertexShader, Palette4.fragmentShaderYieldTransparency);
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        if (!shader2.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader2: " + shader2.getLog());

        Pixmap pixmap = new Pixmap(4, 1, Pixmap.Format.RGBA8888);

        greyPalette = new Palette4(
                0, 0, 0, 0,
                85, 85, 85, 0,
                170, 170, 170, 0,
                255, 255, 255, 0
        );
        gameboyPalette = Palette4.gameboy();

        pixmap.dispose();

        atlas = new TextureAtlas("art.atlas");
        test = atlas.findRegion("test");
        test2 = atlas.findRegion("test2");

        worldBackgroundColor = Color.PURPLE;
        screenBackgroundColor = Color.BLACK;
        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false, false);
        worldView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenRegion = new TextureRegion();
        screenView.getCamera().position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        batch.enableBlending();
    }

    @Override
    public void render() {
        frameBuffer.begin();
        Gdx.gl.glClearColor(worldBackgroundColor.r, worldBackgroundColor.g, worldBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldView.apply();
        worldView.getCamera().position.set(playerX, playerY, 0);
        worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.setProjectionMatrix(worldView.getCamera().combined);
        batch.setShader(shader2);

        batch.begin();
        greyPalette.bind(batch.getShader());
        batch.draw(test, -16, 0);
        batch.end();

        batch.begin();
        gameboyPalette.bind(batch.getShader());
        batch.draw(test, 0, 0);
        batch.end();

        batch.setShader(shader2);

        batch.begin();
        greyPalette.bind(batch.getShader());
        batch.draw(test2, -16, -16);
        batch.end();

        batch.begin();
        gameboyPalette.bind(batch.getShader());
        batch.draw(test2, 0, -16);
        batch.end();

        batch.setShader(null);
        batch.begin();
        batch.draw(greyPalette.getTexture(), -16, 18);
        batch.draw(gameboyPalette.getTexture(), 0, 18);
        batch.end();
        frameBuffer.end();

        Gdx.gl.glClearColor(screenBackgroundColor.r, screenBackgroundColor.g, screenBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        screenView.apply();
        batch.setProjectionMatrix(screenView.getCamera().combined);
        batch.begin();
        screenTexture = frameBuffer.getColorBufferTexture();
        screenTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        screenRegion.setRegion(screenTexture);
        screenRegion.flip(false, true);
        batch.draw(screenRegion, 0, 0);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        screenView.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        atlas.dispose();
        greyPalette.dispose();
        gameboyPalette.dispose();
        frameBuffer.dispose();
    }
}
