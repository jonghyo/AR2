package net.kboy.sceneformsample.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import com.eclipsesource.json.JsonObject
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_sceneform.*
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import net.kboy.sceneformsample.activity.util.HttpUtil


class SceneformActivity : AppCompatActivity() {
    private lateinit var selectedObject: Uri
    private lateinit var fragment: ArFragment
    val url = "https://asia-northeast1-ka1ryu.cloudfunctions.net/v2/land/domain"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(net.kboy.sceneformsample.R.layout.activity_sceneform)

        initializeGallery()

        fragment = sceneformFragment.let { it as ArFragment }

        fragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }
            val anchor = hitResult.createAnchor()
            placeObject(fragment, anchor, selectedObject)
        }
    }

    private fun initializeGallery() {
        searchbutton.setOnClickListener {
            val json = JsonObject()
            json.add("lan", "35.6548852")
            json.add("lon", "139.7549403")
            onClick(this, url, json.toString())
        }
    }

    //非同期処理でHTTP GETを実行します。
    fun onClick(context: Context, url :String, lanlon: String)= GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT, null, {
        val http = HttpUtil()
        val url = http.httpPOST(url, lanlon).await()
        selectedObject = Uri.parse(url)
        Toast.makeText(context, "モデルの取得が完了しました" + url, Toast.LENGTH_LONG).show()
    })

    private fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri) {
        /* When you build a Renderable, Sceneform loads model and related resources
 * in the background while returning a CompletableFuture.
 * Call thenAccept(), handle(), or check isDone() before calling get().
 */
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(
                        this,
                        model,
                        RenderableSource.SourceType.GLTF2)
                        .setScale(0.5f)  // Scale the original model to 50%.
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(model)
                .build()
                .thenAccept {
                    addNodeToScene(fragment, anchor, it)
                }
                .exceptionally { throwable ->
                    val toast = Toast.makeText(this, "Unable to load renderable $model" + throwable.message, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }
}
