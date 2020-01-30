package nexus.core.registry.assets;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import nexus.core.registry.Pack;
import nexus.util.CommonUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 * Represents an mesh in opengl
 */
public class ShaderAsset implements IAsset {
    @Getter
    private String name;
    @Getter
    @Setter
    private Pack pack;
    @Getter
    private File file;
    @Getter
    private boolean loaded = false;
    //Shader related
    private int programID, vertexID, fragmentID;
    private final Map<String, Integer> uniforms = Maps.newHashMap();
    private final Map<String, int[]> samplers = Maps.newHashMap();
    private String uniformLine, samplerLine, bindsLine;
    private final FloatBuffer matrixBuffer;

    /**
     * Creates an mesh and gets the file name
     *
     * @param file
     */
    public ShaderAsset(File file) {
        this.file = file;
        matrixBuffer = BufferUtils.createFloatBuffer(16);
        try {
            this.name = CommonUtils.removeExtension(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Pass a uniform float to shader
     *
     * @param name  uniform's name
     * @param value the value of the uniform
     */
    public void loadFloat(String name, float value) {
        GL20.glUniform1f(uniforms.get(name), value);
    }

    /**
     * Pass a uniform texture to shader
     *
     * @param name uniform's name
     */
    public void loadSampler(String name) {
        int[] values = samplers.get(name);
        GL20.glUniform1i(values[0], values[1]);
    }

    /**
     * Pass a vec3 to a shader
     *
     * @param name the uniforms name
     * @param vec  the vec to passed to the shader
     */
    public void loadVec3(String name, Vector3f vec) {
        GL20.glUniform3f(uniforms.get(name), vec.x, vec.y, vec.z);
    }

    /**
     * loads vec4 to the shader
     *
     * @param name the vec4 name in the shader
     * @param vec  the vec4 value
     */
    public void loadVec4(String name, Vector4f vec) {
        GL20.glUniform4f(uniforms.get(name), vec.x, vec.y, vec.z, vec.w);
    }

    /**
     * loads boolean to the shader
     *
     * @param name  the boolean name in the shader
     * @param value the boolean value
     */
    public void loadBool(String name, boolean value) {
        int val = (value) ? 1 : 0;
        GL20.glUniform1i(uniforms.get(name), val);
    }

    /**
     * loads matrix to the shader
     *
     * @param name the matrix name in the shader
     * @param mat  the matrix value
     */
    public void loadMat4(String name, Matrix4f mat) {
        mat.get(matrixBuffer);
        glUniformMatrix4fv(uniforms.get(name), false, matrixBuffer);
    }

    /**
     * Loads an matrix array
     *
     * @param name the matrix[] name
     * @param mats the matrix[]
     */
    public void loadMat4Array(String name, Matrix4f[] mats) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = mats != null ? mats.length : 0;
            FloatBuffer fb = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                mats[i].get(16 * i, fb);
            }
            glUniformMatrix4fv(uniforms.get(name), false, fb);
        }
    }

    /**
     * loads matrix to the shader
     *
     * @param name the matrix name in the shader
     */
    public void loadVec2(String name, Vector2f vec) {
        GL20.glUniform2f(uniforms.get(name), vec.x, vec.y);
    }


    /**
     * Must be called before using shader
     */
    public void start() {
        GL20.glUseProgram(programID);
    }

    /**
     * Loads an mesh to opengl
     */
    public void load() {
        if (!loaded) {
            String[] sources = parseSource();

            vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
            GL20.glShaderSource(vertexID, sources[0]);
            GL20.glCompileShader(vertexID);
            if (GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.out.println(GL20.glGetShaderInfoLog(vertexID, 500));
                System.err.println("Failed to compile vertex shader");
                System.exit(-1);
            }

            fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
            GL20.glShaderSource(fragmentID, sources[1]);
            GL20.glCompileShader(fragmentID);
            if (GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.out.println(GL20.glGetShaderInfoLog(fragmentID, 500));
                System.err.println("Failed to compile fragment shader");
                System.exit(-1);
            }

            programID = GL20.glCreateProgram();
            GL20.glAttachShader(programID, vertexID);
            GL20.glAttachShader(programID, fragmentID);
            parseBinds();
            GL20.glLinkProgram(programID);
            GL20.glValidateProgram(programID);
            parseUniforms();
            parseSamplers();
            loaded = true;
        }
    }

    /**
     * This method will take the line and parse the uniforms
     */
    private void parseUniforms() {
        String rawUniforms = CommonUtils.getTextAfter(uniformLine, ':', 1).trim();
        if (rawUniforms.contains(",")) {
            String[] uniforms = rawUniforms.split(",");
            for (String untrimmed : uniforms) {
                String uniform = untrimmed.trim();
                this.uniforms.put(uniform, GL20.glGetUniformLocation(programID, uniform));
            }
        } else {
            String uniform = rawUniforms.trim();
            this.uniforms.put(uniform, GL20.glGetUniformLocation(programID, uniform));
        }
    }

    /**
     * This method will take the line and parse the uniforms
     */
    private void parseSamplers() {
        String rawSamplers = CommonUtils.getTextAfter(samplerLine, ':', 1).trim();
        if (rawSamplers.contains(",")) {
            String[] binds = rawSamplers.split(",");
            for (String untrimmed : binds) {
                String bind = untrimmed.trim();
                String[] combo = bind.split(" ");
                String name = combo[0];
                int position = Integer.parseInt(combo[1]);
                this.samplers.put(name, new int[]{GL20.glGetUniformLocation(programID, name), position});
            }
        } else {
            String[] combo = rawSamplers.split(" ");
            String name = combo[0];
            int position = Integer.parseInt(combo[1]);
            this.samplers.put(name, new int[]{GL20.glGetUniformLocation(programID, name), position});
        }
    }

    /**
     * This method will parse the binds
     * for the given shader and assign them
     */
    private void parseBinds() {
        String rawBinds = CommonUtils.getTextAfter(bindsLine, ':', 1).trim();
        if (rawBinds.contains(",")) {
            String[] binds = rawBinds.split(",");
            for (String untrimmed : binds) {
                String bind = untrimmed.trim();
                String[] combo = bind.split(" ");
                String name = combo[0];
                int position = Integer.parseInt(combo[1]);
                GL20.glBindAttribLocation(programID, position, name);
            }
        } else {
            String[] combo = rawBinds.split(" ");
            String name = combo[0];
            int position = Integer.parseInt(combo[1]);
            GL20.glBindAttribLocation(programID, position, name);
        }
    }

    /**
     * Parses the source code
     *
     * @return returns the two sources for the programs
     */
    private String[] parseSource() {
        StringBuilder vertexSource = new StringBuilder();
        StringBuilder fragSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean isInVert = false;
            boolean isInFrag = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("//define_uniforms"))
                    uniformLine = line;
                else if (line.startsWith("//define_samplers"))
                    samplerLine = line;
                else if (line.startsWith("//define_binds"))
                    bindsLine = line;
                if (isInVert && !line.startsWith("//FRAG_SHADER")) {
                    vertexSource.append(line).append("\n");
                    isInFrag = false;
                } else if (isInFrag) {
                    fragSource.append(line).append("\n");
                    isInVert = false;
                }
                if (line.startsWith("//VERTEX_SHADER")) {
                    isInVert = true;
                    isInFrag = false;
                } else if (line.startsWith("//FRAG_SHADER")) {
                    isInFrag = true;
                    isInVert = false;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{vertexSource.toString(), fragSource.toString()};
    }


    /**
     * Unloads an mesh from opengl
     */
    public void unload() {
        if (loaded) {
            stop();
            GL20.glDetachShader(programID, vertexID);
            GL20.glDetachShader(programID, fragmentID);
            GL20.glDeleteShader(vertexID);
            GL20.glDeleteShader(fragmentID);
            GL20.glDeleteProgram(programID);
            loaded = false;
        }
    }

    /**
     * Must be called after using shader
     */
    public void stop() {
        GL20.glUseProgram(0);
    }
}
