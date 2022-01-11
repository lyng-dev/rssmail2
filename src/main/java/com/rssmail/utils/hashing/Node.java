package com.rssmail.utils.hashing;

//Source from: https://gist.githubusercontent.com/pranaybathini/cfd85e94a293df74b1fd962caf2a1f78/raw/43ce2a6948401b0dad378353a9d32ed18032aafd/MerkleNode.java
public class Node {

  private Node left;
  private Node right;
  private String hash;

  public Node(Node left, Node right, String hash) {
      this.left = left;
      this.right = right;
      this.hash = hash;
  }

  public Node getLeft() {
      return left;
  }

  public void setLeft(Node left) {
      this.left = left;
  }

  public Node getRight() {
      return right;
  }

  public void setRight(Node right) {
      this.right = right;
  }

  public String getHash() {
      return hash;
  }

  public void setHash(String hash) {
      this.hash = hash;
  }
}
