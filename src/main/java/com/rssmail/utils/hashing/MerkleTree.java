package com.rssmail.utils.hashing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.springframework.stereotype.Component;

@Component
public class MerkleTree {

  public static Node generateTree(ArrayList<String> dataBlocks) {
      ArrayList<Node> childNodes = new ArrayList<>();

      for (String message : dataBlocks) {
          childNodes.add(new Node(null, null, HashAlgorithm.generateHash(message)));
      }

      return buildTree(childNodes);
  }

  private static Node buildTree(ArrayList<Node> children) {
      ArrayList<Node> parents = new ArrayList<>();

      while (children.size() != 1) {
          int index = 0, length = children.size();
          while (index < length) {
              Node leftChild = children.get(index);
              Node rightChild = null;

              if ((index + 1) < length) {
                  rightChild = children.get(index + 1);
              } else {
                  rightChild = new Node(null, null, leftChild.getHash());
              }

              String parentHash = HashAlgorithm.generateHash(leftChild.getHash() + rightChild.getHash());
              parents.add(new Node(leftChild, rightChild, parentHash));
              index += 2;
          }
          children = parents;
          parents = new ArrayList<>();
      }
      return children.get(0);
  }
}