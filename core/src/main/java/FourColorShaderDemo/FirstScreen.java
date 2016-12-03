package FourColorShaderDemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

public class FirstScreen implements Screen {
    public static final int VIRTUAL_WIDTH = 64;
    public static final int VIRTUAL_HEIGHT = 64;
    public int playerX = 0, playerY = 0;

    private Color worldBackgroundColor;
    private Color screenBackgroundColor;
    public TextureAtlas atlas;
    public TextureAtlas.AtlasRegion test;
    public Color[] palette;
    public ShaderProgram shader;
    private FrameBuffer frameBuffer;
    private SpriteBatch batch;
    private Viewport worldView;
    private Viewport screenView;
    private Texture screenTexture;
    private TextureRegion screenRegion;

    @Override
    public void show() {
        palette = new Color[4];
        palette[0] = new Color(156 / 255f, 189 / 255f, 15 / 255f, 255 / 255f);
        palette[1] = new Color(140 / 255f, 173 / 255f, 15 / 255f, 0 / 255f);
        palette[2] = new Color(48 / 255f, 98 / 255f, 48 / 255f, 255 / 255f);
        palette[3] = new Color(15 / 255f, 56 / 255f, 15 / 255f, 255 / 255f);

        shader = new ShaderProgram(
                "attribute vec4 a_position;\n" +
                        "attribute vec4 a_color;\n" +
                        "attribute vec2 a_texCoord0;\n" +
                        "uniform mat4 u_projTrans;\n" +
                        "varying vec4 v_color;\n" +
                        "varying vec2 v_texCoords;\n\n" +
                        "void main() {\n" +
                        "    v_color = a_color;\n" +
                        "    v_color.a = v_color.a * (256f / 255f);\n" +
                        "    v_texCoords = a_texCoord0;\n" +
                        "    gl_Position = u_projTrans * a_position;\n" +
                        "}"
                ,
                "#ifdef GL_ES\n" +
                        "#define LOWP lowp\n" +
                        "precision mediump float;\n" +
                        "#else\n" +
                        "#define LOWP\n" +
                        "#endif\n\n" +
                        "varying LOWP vec4 v_color;\n" +
                        "varying vec2 v_texCoords;\n" +
                        "uniform vec4 u_palette[4];\n" +
                        "uniform sampler2D u_texture;\n\n" +
                        "void main() {\n" +
                        "    gl_FragColor = u_palette[int(texture2D(u_texture, v_texCoords).r * 3.9999)];\n" +
                        "}"
        );

        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());

        shader.begin();

        int location = shader.getUniformLocation("u_palette[0]");
        Gdx.app.debug("location", Integer.toString(location));
        for (int x = 0; x < palette.length; x++)
            shader.setUniformf(location + x, palette[x]);

        atlas = new TextureAtlas("art.atlas");
        test = atlas.findRegion("test");

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true, true);

        worldBackgroundColor = Color.PURPLE;
        screenBackgroundColor = Color.PURPLE;
        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true, true);
        worldView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenRegion = new TextureRegion();
        screenView.getCamera().position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        screenView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.enableBlending();
    }

    @Override
    public void render(float delta) {
        frameBuffer.begin();
        Gdx.gl.glClearColor(worldBackgroundColor.r, worldBackgroundColor.g, worldBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldView.apply();
        worldView.getCamera().position.set(playerX, playerY, 0);
        worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.setProjectionMatrix(worldView.getCamera().combined);

        batch.setShader(shader);

        batch.begin();
        batch.draw(test, 0, 0);
        batch.end();
        batch.setShader(null);
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
    public void hide() {
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
