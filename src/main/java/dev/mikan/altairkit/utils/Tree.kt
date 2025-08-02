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

    fun insert(parentValue:T,value:T){
        if (root == null) {
            this.root = Node(value,null)
            return
        }

        val node = search(value)
        if (node != null) node.data = value
        else search(parentValue)?.let { parent-> parent.children.add(Node(value,parent)) }
    }

    fun clear() {
        this.root = null
    }

    fun get(value: T) : T? {
        return search(value)?.data
    }

    fun contains(value: T) : Boolean {
        return search(value) != null
    }

    fun search(value:T): Node<T>?{
        if (this.root == null) return null

        return searchRecursive(this.root,value)
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

    private fun searchRecursive(currentNode:Node<T>?,value: T): Node<T>?{
        currentNode?: return null
        if (currentNode.data == value) return currentNode

        for (node in currentNode.children) {
            val found = searchRecursive(node,value)
            if (found != null) return found
        }

        return null
    }

    inner class Node<T> {
        var data: T
        val parent: Node<T>?
        val children = mutableListOf<Node<T>>()

        constructor(value: T,parent: Node<T>?) {
            this.data = value
            this.parent = parent
        }
    }
}
