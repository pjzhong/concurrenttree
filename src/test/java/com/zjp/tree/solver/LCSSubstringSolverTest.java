package com.zjp.tree.solver;

import static org.junit.Assert.assertEquals;

import com.zjp.tree.common.PrettyPrinter;
import com.zjp.tree.node.NodeFactory;
import com.zjp.tree.node.impl.DefaultCharSequenceNodeFactory;
import com.zjp.tree.node.impl.bytearry.DefaultByteArrayNodeFactory;
import com.zjp.tree.util.IOUtil;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

public class LCSSubstringSolverTest {

  NodeFactory factory = new DefaultByteArrayNodeFactory();


  @Test
  public void testGetLongestCommonSubstring() throws InterruptedException {

    List<String> documents = Arrays.asList(
        "albert einstein, was a german theoretical physicist who developed the theory of general relativity",
        "near the beginning of his career, albert einstein thought that newtonian mechanics was no longer "
            +
            "enough to reconcile the laws of classical mechanics with the laws of the electromagnetic field",
        "in late summer 1895, at the age of sixteen, albert einstein sat the entrance examinations for "
            +
            "the swiss federal polytechnic in zurich");
    LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());

    ExecutorService executorService = Executors.newCachedThreadPool();
    CountDownLatch latch = new CountDownLatch(documents.size());
    for (String d : documents) {
      executorService.execute(() -> {
        solver.add(d);
        latch.countDown();
      });
    }

    latch.await();
    String longestCommon = solver.getLongestCommonSubstring().toString();
    assertEquals("albert einstein", longestCommon);
    String multi = PrettyPrinter.prettyPrint(solver);

    LCSubstringSolver solver2 = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());
    for (String d : documents) {
      solver2.add(d);
    }

    String longestCommon2 = solver2.getLongestCommonSubstring().toString();
    assertEquals("albert einstein", longestCommon2);
    String single = PrettyPrinter.prettyPrint(solver);

    assertEquals(multi, single);
  }

  @Test
  public void testGetLongestCommon_single() {

    List<String> documents = Arrays.asList(
        "albert einstein, was a german theoretical physicist who developed the theory of general relativity",
        "near the beginning of his career, albert einstein thought that newtonian mechanics was no longer "
            +
            "enough to reconcile the laws of classical mechanics with the laws of the electromagnetic field",
        "in late summer 1895, at the age of sixteen, albert einstein sat the entrance examinations for "
            +
            "the swiss federal polytechnic in zurich");

  }

  public static void main(String[] args) throws Exception {
    List<String> files = Arrays.asList(
        "/shakespeare/tragedies/antony_and_cleopatra.txt",
        "/shakespeare/tragedies/coriolanus.txt",
        "/shakespeare/tragedies/hamlet.txt",
        "/shakespeare/tragedies/king_lear.txt",
        "/shakespeare/tragedies/macbeth.txt",
        "/shakespeare/tragedies/othello.txt",
        "/shakespeare/tragedies/romeo_and_juliet.txt",
        "/shakespeare/tragedies/timon_of_athens.txt",
        "/shakespeare/tragedies/titus_andronicus.txt",
        "/shakespeare/tragedies/julius_caesar.txt"
    );

    CountDownLatch latch = new CountDownLatch(files.size());
    ExecutorService executorService = Executors.newCachedThreadPool();
    LCSubstringSolver solver = new LCSubstringSolver(new DefaultByteArrayNodeFactory());
    long start = System.currentTimeMillis();
    for (String file : files) {
      executorService.execute(() -> {
        String manuscript = IOUtil.loadTextFileFromClasspath(file);
        solver.add(manuscript);
        System.out.format("Added manuscript:%s%n", file);
        latch.countDown();
      });
    }
    latch.await();
    System.out.format("Built suffix tree in %s ms%n", System.currentTimeMillis() - start);

    System.out.println("start searching");
    long search = System.currentTimeMillis();
    CharSequence longest = solver.getLongestCommonSubstring();
    System.out
        .format("Found longest common substring in %s ms%n", System.currentTimeMillis() - search);
    System.out.format("Longest common: %s%n", longest);
  }
}
