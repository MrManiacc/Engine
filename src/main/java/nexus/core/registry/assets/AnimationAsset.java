package nexus.core.registry.assets;

import com.google.flatbuffers.FlatBufferBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import nexus.core.animation.AnimatedFrame;
import nexus.core.registry.Pack;
import nexus.core.registry.assets.raw.RawAnimatedFrame;
import nexus.core.registry.assets.raw.RawAnimation;
import nexus.core.registry.assets.raw.RawAnimationList;
import nexus.core.registry.assets.raw.RawModel;
import nexus.core.registry.parsers.AssimpParser;
import nexus.core.render.Animation;
import nexus.core.render.AnimationMap;
import nexus.util.CommonUtils;
import org.joml.Matrix4f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimationAsset implements IAsset {
    @Getter
    private String name;
    @Getter
    @Setter
    private Pack pack;
    @Getter
    private File file;
    @Getter
    private boolean loaded = false;
    private AnimationMap animationMap;
    private FlatBufferBuilder buffer;
    private boolean binary;

    /**
     * Creates an image and gets the file name
     *
     * @param file
     */
    public AnimationAsset(File file, boolean binary) {
        this.file = file;
        this.binary = binary;
        try {
            this.name = CommonUtils.removeExtension(file);
            this.buffer = new FlatBufferBuilder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the animation map
     *
     * @return animation map
     */
    public AnimationMap toComponent() {
        return animationMap;
    }

    /**
     * Loads the animations
     */
    public void load() {
        if (!loaded) {
            //TODO: load mesh
            if (binary) deserialize();
            else {
                AssimpParser.parseAnimations(file).ifPresent(animationMap -> {
                    this.loaded = true;
                    this.animationMap = new AnimationMap();
                    animationMap.forEach(animation -> {
                        animation.setName(this.name);
                        this.animationMap.addAnimation(animation);
                    });
                });
                serialize();
            }
        }
    }

    /**
     * Deserialize the animation
     */
    public void deserialize() {
        RawAnimationList animationList = RawAnimationList.getRootAsRawAnimationList(CommonUtils.decodeFile(file));
        RawAnimation[] rawAnimations = new RawAnimation[animationList.animationsLength()];
        this.animationMap = new AnimationMap();
        for (int i = 0; i < rawAnimations.length; i++) {
            RawAnimation rawAnimation = animationList.animations(i);
            AnimatedFrame[] frames = new AnimatedFrame[rawAnimation.framesLength()];
            Animation animation = new Animation();
            animation.setName(rawAnimation.name());
            animation.setDuration(rawAnimation.duration());
            for (int j = 0; j < frames.length; j++) {
                RawAnimatedFrame rawAnimatedFrame = rawAnimation.frames(j);
                AnimatedFrame animatedFrame = new AnimatedFrame();
                List<Matrix4f> localMatricies = new ArrayList<>();
                for (int x = 0; x < rawAnimatedFrame.localMatriciesLength(); x++)
                    localMatricies.add(CommonUtils.bufferToMatrix(rawAnimatedFrame.localMatricies(x)));
                Collections.reverse(localMatricies);

                List<Matrix4f> parentMatricies = new ArrayList<>();
                for (int x = 0; x < rawAnimatedFrame.parentMatriciesLength(); x++)
                    parentMatricies.add(CommonUtils.bufferToMatrix(rawAnimatedFrame.parentMatricies(x)));
                Collections.reverse(parentMatricies);
                Matrix4f[] localMats = CommonUtils.matrixToArray(localMatricies);
                Matrix4f[] parentMats = CommonUtils.matrixToArray(parentMatricies);
                animatedFrame.setLocalMatrices(localMats);
                animatedFrame.setParentMatrices(parentMats);
                animatedFrame.setRootTransformation(CommonUtils.bufferToMatrix(rawAnimatedFrame.rootTransform()));
                animatedFrame.setTimeStamp(rawAnimatedFrame.timestamp());
                animation.addAnimatedFrame(animatedFrame);
            }
            animationMap.addAnimation(animation);
        }
    }

    /**
     * Serialize to file
     */
    @SneakyThrows
    public void serialize() {
        if (!loaded) load();
        //Create the animated frames
        //Create the Animation list with animation frames
        //Create the list of animations
        int[] rawAnimations = new int[animationMap.getAnimations().size()];
        int counter = 0;
        for (String name : animationMap.getAnimations().keySet()) {
            Animation animation = animationMap.getAnimations().get(name);
            List<AnimatedFrame> frames = animation.getFrames();
            int[] animatedFrames = new int[frames.size()];
            for (int j = 0; j < animatedFrames.length; j++) {
                AnimatedFrame frame = frames.get(j);
                RawAnimatedFrame.startLocalMatriciesVector(buffer, frame.getLocalMatrices().length);
                for (int i = 0; i < frame.getLocalMatrices().length; i++)
                    CommonUtils.matrixToBuffer(buffer, frame.getLocalMatrices()[i]);
                int localMatricies = buffer.endVector();
                RawAnimatedFrame.startParentMatriciesVector(buffer, frame.getParentMatrices().length);
                for (int i = 0; i < frame.getParentMatrices().length; i++)
                    CommonUtils.matrixToBuffer(buffer, frame.getParentMatrices()[i]);
                int parentMatricies = buffer.endVector();
                RawAnimatedFrame.startRawAnimatedFrame(buffer);
                RawAnimatedFrame.addLocalMatricies(buffer, localMatricies);
                RawAnimatedFrame.addParentMatricies(buffer, parentMatricies);
                RawAnimatedFrame.addTimestamp(buffer, frame.getTimeStamp());
                RawAnimatedFrame.addRootTransform(buffer, CommonUtils.matrixToBuffer(buffer, frame.getRootTransformation()));
                animatedFrames[j] = RawAnimatedFrame.endRawAnimatedFrame(buffer);
            }
            int rawFrames = RawAnimation.createFramesVector(buffer, animatedFrames);
            int rawName = buffer.createString(animation.getName());
            RawAnimation.startRawAnimation(buffer);
            RawAnimation.addDuration(buffer, animation.getDuration());
            RawAnimation.addName(buffer, rawName);
            RawAnimation.addFrames(buffer, rawFrames);
            int rawAnimation = RawAnimation.endRawAnimation(buffer);
            rawAnimations[counter] = rawAnimation;
            counter++;
        }

        int animationsVector = RawAnimationList.createAnimationsVector(buffer, rawAnimations);
        RawAnimationList.startRawAnimationList(buffer);
        RawAnimationList.addAnimations(buffer, animationsVector);
        int animationList = RawAnimationList.endRawAnimationList(buffer);
        RawAnimationList.finishRawAnimationListBuffer(buffer, animationList);

        FileOutputStream fos = new FileOutputStream(new File(file.getParentFile().getPath() + File.separator + name + ".animation"));
        fos.write(buffer.sizedByteArray());
        fos.close();
    }

    /**
     * Unloads the animations
     */
    public void unload() {
        this.loaded = false;
        this.animationMap = null;
    }
}
