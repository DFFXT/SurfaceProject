import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import com.example.surfaceproject.App
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
// 纹理宽高必须是2的n次方，不能是其他宽高
private val tid = intArrayOf(0)
fun loadTexture(gl: GL10, bitmap: Bitmap): Int {
    tid[0] = 0
    gl.glGenTextures(1, tid, 0)
    // 绑定纹理
    gl.glBindTexture(GL10.GL_TEXTURE_2D, tid[0])
    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
    return tid[0]
}



fun Int.toBitmap(): Bitmap {
    val options = BitmapFactory.Options()
    options.inScaled = false
    // 纹理宽高必须是2的n次方，不能是其他宽高
    return BitmapFactory.decodeResource(App.ctx.resources, this, options)
}
