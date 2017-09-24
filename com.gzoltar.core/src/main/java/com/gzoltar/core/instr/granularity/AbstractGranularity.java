package com.gzoltar.core.instr.granularity;

import java.util.StringTokenizer;
import com.gzoltar.core.model.Node;
import com.gzoltar.core.model.NodeType;
import com.gzoltar.core.runtime.Collector;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;

public abstract class AbstractGranularity implements IGranularity {

  protected CtClass c;
  protected MethodInfo mi;
  protected CodeIterator ci;

  public AbstractGranularity(CtClass c, MethodInfo mi, CodeIterator ci) {
    this.c = c;
    this.mi = mi;
    this.ci = ci;
  }

  private Node getNode(Collector c, Node parent, String name, NodeType type) {
    Node node = parent.getChild(name);

    if (node == null) {
      node = c.createNode(parent, name, type);
    }

    return node;
  }

  protected Node getNode(CtClass cls) {
    Collector c = Collector.instance();
    Node node = c.getRootNode();
    String tok = cls.getName();

    // Extract Package Hierarchy
    int pkgEnd = tok.lastIndexOf(".");

    if (pkgEnd >= 0) {
      StringTokenizer stok = new StringTokenizer(tok.substring(0, pkgEnd), ".");

      while (stok.hasMoreTokens()) {
        node = getNode(c, node, stok.nextToken(), NodeType.PACKAGE);
      }
    } else {
      pkgEnd = -1;
    }


    // Extract Class Hierarchy
    StringTokenizer stok = new StringTokenizer(tok.substring(pkgEnd + 1), "$");

    while (stok.hasMoreTokens()) {
      tok = stok.nextToken();
      node = getNode(c, node, tok, NodeType.CLASS);
    }


    return node;
  }

  protected Node getNode(CtClass cls, CtBehavior m) {
    Collector c = Collector.instance();
    Node parent = getNode(cls);

    return getNode(c, parent, m.getName() + Descriptor.toString(m.getSignature()),
        NodeType.METHOD);
  }

  public Node getNode(CtClass cls, CtBehavior m, int line) {
    Collector c = Collector.instance();
    Node parent = getNode(cls, m);

    return getNode(c, parent, String.valueOf(line), NodeType.LINE);
  }

}
