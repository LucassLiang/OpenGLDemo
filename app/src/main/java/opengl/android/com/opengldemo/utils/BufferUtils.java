package opengl.android.com.opengldemo.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Buffer工具类
 * update by lucas on 2018/12/10.
 */
public class BufferUtils {

    /**
     * 创建ShortBuffer
     *
     * @param vertex
     * @return
     */
    public static ShortBuffer createShortBuffer(short[] vertex) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertex.length * 2);
        return byteBuffer
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(vertex);
    }

    /**
     * 创建FloatBuffer
     *
     * @param vertex
     * @return
     */
    public static FloatBuffer createFloatBuffer(float[] vertex) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertex.length * 4);
        return byteBuffer
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex);
    }
}
