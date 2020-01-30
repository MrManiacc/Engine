// automatically generated by the FlatBuffers compiler, do not modify

package nexus.core.registry.assets.raw;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class RawAnimationList extends Table {
  public static RawAnimationList getRootAsRawAnimationList(ByteBuffer _bb) { return getRootAsRawAnimationList(_bb, new RawAnimationList()); }
  public static RawAnimationList getRootAsRawAnimationList(ByteBuffer _bb, RawAnimationList obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; vtable_start = bb_pos - bb.getInt(bb_pos); vtable_size = bb.getShort(vtable_start); }
  public RawAnimationList __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public RawAnimation animations(int j) { return animations(new RawAnimation(), j); }
  public RawAnimation animations(RawAnimation obj, int j) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int animationsLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createRawAnimationList(FlatBufferBuilder builder,
      int animationsOffset) {
    builder.startObject(1);
    RawAnimationList.addAnimations(builder, animationsOffset);
    return RawAnimationList.endRawAnimationList(builder);
  }

  public static void startRawAnimationList(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addAnimations(FlatBufferBuilder builder, int animationsOffset) { builder.addOffset(0, animationsOffset, 0); }
  public static int createAnimationsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startAnimationsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endRawAnimationList(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishRawAnimationListBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
  public static void finishSizePrefixedRawAnimationListBuffer(FlatBufferBuilder builder, int offset) { builder.finishSizePrefixed(offset); }
}

