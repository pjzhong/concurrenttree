package com.zjp.tree.node.util;

import java.util.concurrent.atomic.AtomicReferenceArray;

import com.zjp.tree.node.Node;

public class NodeUtil {

	public static int binarySearch(AtomicReferenceArray<Node> array,
			Character target) {
		int low = 0;
		int height = array.length() - 1;

		while (low <= height) {
			int mid = (low + height) >>> 1;
			Node midVal = array.get(mid);
			int cmp = midVal.getIncomingEdgeFirstCharacter().compareTo(target);

			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				height = mid - 1;
			} else {
				return mid;
			}
		}

		return -(low + 1);
	}

}
