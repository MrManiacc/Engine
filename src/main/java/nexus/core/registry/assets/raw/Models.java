// automatically generated by the FlatBuffers compiler, do not modify

package nexus.core.registry.assets.raw;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Models extends Table {
  public static Models getRootAsModels(ByteBuffer _bb) { return getRootAsModels(_bb, new Models()); }
  public static Models getRootAsModels(ByteBuffer _bb, Models obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; vtable_start = bb_pos - bb.getInt(bb_pos); vtable_size = bb.getShort(vtable_start); }
  public Models __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public RawModel meshes(int j) { return meshes(new RawModel(), j); }
  public RawModel meshes(RawModel obj, int j) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int meshesLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createModels(FlatBufferBuilder builder,
      int meshesOffset) {
    builder.startObject(1);
    Models.addMeshes(builder, meshesOffset);
    return Models.endModels(builder);
  }

  public static void startModels(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addMeshes(FlatBufferBuilder builder, int meshesOffset) { builder.addOffset(0, meshesOffset, 0); }
  public static int createMeshesVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startMeshesVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endModels(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishModelsBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
  public static void finishSizePrefixedModelsBuffer(FlatBufferBuilder builder, int offset) { builder.finishSizePrefixed(offset); }
}

