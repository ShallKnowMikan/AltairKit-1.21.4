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
        buildTreeString(root, stringBuilder, 0)
        return stringBuilder.toString()
    }

    private fun buildTreeString(node: Node<T>?, builder: StringBuilder, depth: Int) {
        if (node == null) return

        // Aggiunge indentazione in base alla profondità
        repeat(depth) { builder.append("    ") }

        // Aggiunge il nodo corrente e la sua profondità
        builder.append("\n├── ")
        builder.append(node.data.toString())
        builder.append(" (depth: $depth)")
        builder.append("\n")

        // Aggiunge i figli ricorsivamente
        node.children.forEach { child ->
            buildTreeString(child, builder, depth + 1)
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
