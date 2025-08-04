package dev.mikan.altairkit.utils

class Tree<T> {
    var root: Node<T>? = null

    constructor()

    fun setRoot(value: T){
        this.root = Node<T>(value,null)
    }

    constructor(root: T) {
        this.root = Node<T>(root,null)
    }

    fun insert(parentValue:T,value:T,fetchCondition: (T) -> Boolean){
        if (root == null) {
            this.root = Node(value,null)
            return
        }

        var data = fetch(fetchCondition)
        if (data != null) {
            data = value
            return
        }
        else search(parentValue)?.let { parent-> parent.children.add(Node(value,parent)) }
    }

    fun insert(parentValue:T,value:T){
        if (root == null) {
            this.root = Node(value,null)
            return
        }

        val node = search(value)
        if (node != null) {
            node.data = value; return
        }
        else search(parentValue)?.let { parent-> parent.children.add(Node(value,parent)) }
    }


    fun clear() {
        this.root = null
    }

    fun get(value: T) : T? {
        return search(value)?.data
    }

    fun values() : Set<T>{
        val set = mutableSetOf<T>()
        root?: return set

        val stack = ArrayDeque<Node<T>>()
        stack.addLast(root!!)

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()
            set.add(node.data)
            for (child in node.children) {
                stack.addLast(child)
            }
        }
        return set
    }

    fun isLeaf(value:T) : Boolean {
        return search(value)?.children!!.isEmpty()
    }


    fun contains(value: T?) : Boolean {
        if (value == null) return false
        return search(value) != null
    }

    fun search(value:T): Node<T>?{
        root?: return null

        val stack = ArrayDeque<Node<T>>()
        stack.addLast(root!!)

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()
            if (node.data == value) return node
            for (child in node.children) {
                stack.addLast(child)
            }
        }
        return null
    }

    fun remove(value: T): Node<T>? {
        val node = search(value)
        if (node == this.root) {
            this.root = null
            return null
        }
        node?.let { node -> node.parent?.children?.remove(node);
            return node
        }
        return null
    }

    // This is good if you want to implement a DFS algorithm for searching
//    private fun searchRecursive(currentNode:Node<T>?,value: T): Node<T>?{
//        currentNode?: return null
//        if (currentNode.data == value) return currentNode
//
//        for (node in currentNode.children) {
//            val found = searchRecursive(node,value)
//            if (found != null) return found
//        }
//
//        return null
//    }


    fun fetch (condition: (value:T) -> Boolean) : T? {
        root?: return null

        val stack = ArrayDeque<Node<T>>()
        stack.addLast(root!!)

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()
            if (condition(node.data)) return node.data
            for (child in node.children) {
                stack.addLast(child)
            }
        }
        return null
    }

    fun filter (control: (treeNode:Node<T>) -> Boolean) : Set<Node<T>> {
        val passedNodes = mutableSetOf<Node<T>>()

        root?: return passedNodes

        val stack = ArrayDeque<Node<T>>()
        stack.addLast(root!!)

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()
            if (control(node)) passedNodes.add(node)
            for (child in node.children) {
                stack.addLast(child)
            }
        }
        return passedNodes
    }

    override fun toString() : String {
        if (root == null) return "empty"

        val stringBuilder = StringBuilder()
//        buildTreeString(root, stringBuilder)
        toStringDFS(root,stringBuilder)
        stringBuilder.append("\n")
        // Fixing string aesthetic └
        val lastBranchIndex = stringBuilder.toString().lastIndexOf("├")
        return if (lastBranchIndex != -1) {
            stringBuilder.toString().substring(0, lastBranchIndex) + "└" + stringBuilder.toString().substring(lastBranchIndex + 1)
        } else {
            // If char looked for is not present
            stringBuilder.toString()
        }
    }

    /*
    * Logically, to find the depth, for each node I just have to
    * go up with .parent and check if it is null, root only have null
    * parent
    * */
    private fun toStringBFS(node: Node<T>?, builder: StringBuilder) {
        if (node == null) return

        val stack = ArrayDeque<Node<T>>()

        stack.addLast(node)
        while (stack.isNotEmpty()) {

            val current = stack.removeFirst()
            // find depth
            var depth = 0
            var currentNode: Node<T>? = current
            while (currentNode?.parent != null) {
                depth++
                currentNode = currentNode.parent
            }

            for (child in current.children) {
                stack.addLast(child)
            }
            val gonnaExit = stack.isEmpty()
            builder.append("\n│\n"+ (if (gonnaExit) "└" else "├") +"─────── Depth: $depth : $current ${if (gonnaExit) "\n" else ""}")
        }

    }

    // DFS toString implementation

    private fun toStringDFS(node:Node<T>?,builder: StringBuilder) {
        if (node == null) return
        val depth = findDepth(node)
        val additionalLength = StringBuilder()
        repeat(depth) { additionalLength.append("────") }
        builder.append("\n│\n├───${additionalLength} Depth: $depth : $node ")
        node.children.forEach { child ->
            toStringDFSRecursive(child,builder)
        }
    }

    private fun findDepth(node: Node<T>) :Int {
        var current:Node<T>? = node
        var depth = 0
        while (current?.parent != null) {
            depth ++
            current = current.parent
        }
        return depth
    }

    private fun toStringDFSRecursive(node: Node<T>,builder: StringBuilder) {
        val depth = findDepth(node)
        val additionalLength = StringBuilder()
        repeat(depth) { additionalLength.append("────") }
        builder.append("\n│\n├───${additionalLength} Depth: $depth : $node ")
        node.children.forEach { child ->
            toStringDFSRecursive(child,builder)
        }
    }


    inner class Node<T> {
        var data: T
        val parent: Node<T>?
        val children = mutableListOf<Node<T>>()

        constructor(value: T,parent: Node<T>?) {
            this.data = value
            this.parent = parent
        }

        override fun toString() : String{
            return this.data.toString()
        }
    }
}
