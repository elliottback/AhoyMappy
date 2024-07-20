class MyLinkedList {

    private Node head;
    private int length = 0;

    public MyLinkedList() {
        this.head = null;
    }

    /**
     * gets the node at index n
     */
    private Node getNode(int index) {
        if (this.length < index)
            return null;

        Node current = head;
        for (int i = 0; i < index; i++)
            current = current.next;

        return current;
    }

    /**
     * Get the value of the indexth node in the linked list. If the index is invalid, return -1.
     */
    public int get(int index) {
        Node n = getNode(index);
        return n == null ? -1 : n.val;
    }

    /**
     * Add a node of value val before the first element of the linked list. After the insertion, the new node will be the first node of the linked list.
     */
    public void addAtHead(int val) {
        this.length++;

        if (head == null) {
            head = new Node(val);
            return;
        }

        Node temp = new Node(val);
        temp.next = head;
        head = temp;
    }

    /**
     * Append a node of value val as the last element of the linked list.
     */
    public void addAtTail(int val) {
        this.length++;

        if (head == null) {
            head = new Node(val);
            return;
        }

        getNode(this.length - 2).next = new Node(val); // -1 for 0 based index, -2 because we ++ it
    }

    /**
     * Add a node of value val before the indexth node in the linked list.
     * If index equals the length of the linked list, the node will be appended to the end of the linked list.
     * If index is greater than the length, the node will not be inserted.
     */
    public void addAtIndex(int index, int val) {
        if (this.length < index)
            return;

        if (index == 0) {
            addAtHead(val);
        } else if (index == this.length) {
            addAtTail(val);
        } else {
            Node n = getNode(index - 1);
            Node tmp = n.next;
            n.next = new Node(val);
            n.next.next = tmp;
            this.length++;
        }
    }

    /**
     * Delete the indexth node in the linked list, if the index is valid.
     */
    public void deleteAtIndex(int index) {
        if ( index >= length )
            return;

        if (index == 0) {
            head = head.next;
        } else {
            Node n = getNode(index - 1);
            n.next = n.next.next;
        }

        this.length--;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "MyLinkedList{" +
                "head=" + head +
                '}';
    }

    public static void main(String [] args)
    {
        MyLinkedList l = new MyLinkedList();

        l.addAtHead(2);
        l.deleteAtIndex(1);
        l.addAtHead(2);
        l.addAtHead(7);
        l.addAtHead(3);
        l.addAtHead(2);
        l.addAtHead(5);
        l.addAtTail(5);

        int r = l.get(5);
        System.out.println(l);

        l.deleteAtIndex(6);
        System.out.println(l);
        l.deleteAtIndex(4);
        System.out.println(l);
    }
}

class Node {
    public int val;
    public Node next;

    public Node(int val) {
        this.val = val;
        this.next = null;
    }

    @Override
    public String toString() {
        return "Node{" +
                "val=" + val +
                ", next=" + next +
                '}';
    }
}