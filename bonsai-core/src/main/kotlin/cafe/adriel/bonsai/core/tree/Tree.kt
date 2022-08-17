package cafe.adriel.bonsai.core.tree

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import cafe.adriel.bonsai.core.node.Node
import cafe.adriel.bonsai.core.node.TreeApplier
import cafe.adriel.bonsai.core.tree.extension.ExpandableTree
import cafe.adriel.bonsai.core.tree.extension.ExpandableTreeHandler
import cafe.adriel.bonsai.core.tree.extension.SelectableTree
import cafe.adriel.bonsai.core.tree.extension.SelectableTreeHandler

@DslMarker private annotation class TreeMarker

@Immutable
@TreeMarker
data class TreeScope
internal constructor(
    val depth: Int,
    internal val isExpanded: Boolean = true,
    internal val expandMaxDepth: Int = 0
)

@Stable
class Tree<T> internal constructor(val nodes: List<Node<T>>) :
    ExpandableTree<T> by ExpandableTreeHandler(nodes),
    SelectableTree<T> by SelectableTreeHandler(nodes)

@SuppressLint("ComposableNaming")
@Composable
fun <T> Tree(content: @Composable TreeScope.() -> Unit): Tree<T> {
    val applier = remember { TreeApplier<T>() }
    val compositionContext = rememberCompositionContext()
    val composition =
        remember(applier, compositionContext) { Composition(applier, compositionContext) }
    composition.setContent { TreeScope(depth = 0).content() }
    return remember(applier) { Tree(applier.children) }
}
