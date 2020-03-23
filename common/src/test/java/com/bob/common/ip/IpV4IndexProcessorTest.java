package com.bob.common.ip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import com.bob.common.utils.ip.Ipv4IndexProcessor;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * @author wb-jjb318191
 * @create 2020-03-23 9:55
 */
public class IpV4IndexProcessorTest {

    @Test
    public void testIndex() throws Exception {
        System.out.println("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length());

        File txt = new File("C:\\Users\\wb-jjb318191\\Desktop\\全球旗舰版-202002-636871\\全球旗舰版-202002-636871.txt");
        List<String> lines = FileUtils.readLines(txt, "utf-8");
        Ipv4IndexProcessor indexer = new Ipv4IndexProcessor(lines.size());
        List<String> ips = new ArrayList<>();
        for (String line : lines) {
            String[] splits = line.split("\\|");
            StringBuilder sb = new StringBuilder();
            for (int i = 4; i < splits.length; i++) {
                sb.append(splits[i]).append("|");
            }
            String text = sb.toString();
            indexer.indexIpInfo(splits[0], splits[1], text.substring(0, text.length() - 1));
            ips.add(splits[0]);
        }

        indexer.finishProcessing();
        File dat = new File("C:\\Users\\wb-jjb318191\\Desktop\\ipv4-utf8-index.dat");
        if (dat.exists()) {
            dat.delete();
        }
        indexer.flushData(dat.getPath());
        FileUtils.writeLines(new File("C:\\Users\\wb-jjb318191\\Desktop\\ips.txt"), ips);
    }

    @Test
    public void testTreeSetSize(){
        TreeSet<String> treeSet = new TreeSet<>();
        treeSet.add("a");
        treeSet.add("h");
        treeSet.add("l");
        treeSet.add("o");
        treeSet.add("e");
        treeSet.add("m");
        treeSet.add("x");
        treeSet.add("z");
        System.out.println(ObjectSizeCalculator.getObjectSize(new char[]{'a','a','a',}));
    }

}