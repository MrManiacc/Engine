package nexus.core.registry.parsers;

import nexus.core.registry.Registry;
import nexus.core.registry.assets.AnimationAsset;
import nexus.core.registry.assets.MeshAsset;
import nexus.util.opengl.RawMesh;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class will take various nexus.core.assets
 * and export them to a uniform format
 */
public class AssimpExport {


    //TESTING TODO: removes this
    public static void main(String[] args) throws IllegalAccessException, IOException {
        MeshAsset testAsset = new MeshAsset(new File("src/main/resources/domains/core/animations/Boxing.dae"), false);
        testAsset.serialize();

        MeshAsset testAsset2 = new MeshAsset(new File("src/main/resources/domains/core/animations/Boxing.bin"), true);
        testAsset2.deserialize();

        RawMesh fromAssip = testAsset.getMeshes()[0];
        RawMesh fromBin = testAsset2.getMeshes()[0];
        Files.writeString(Paths.get("src/main/resources/assimp.txt"), fromAssip.toString());
        Files.writeString(Paths.get("src/main/resources/bin.txt"), fromBin.toString());


        AnimationAsset testAsset3 = new AnimationAsset(new File("src/main/resources/domains/core/animations/Boxing.dae"), false);
        testAsset3.serialize();

        AnimationAsset testAsset4 = new AnimationAsset(new File("src/main/resources/domains/core/animations/Boxing.animation"), true);
        testAsset4.deserialize();

        Files.writeString(Paths.get("src/main/resources/boxing.txt"), testAsset3.toComponent().toString());
        Files.writeString(Paths.get("src/main/resources/boxingbin.txt"), testAsset4.toComponent().toString());

    }


    /**
     * This will export a mesh asset
     *
     * @param meshAsset the mesh asset to export
     */
    public static void exportMesh(MeshAsset meshAsset) {
        RawMesh[] rawMeshes = meshAsset.getMeshes();

    }
}
