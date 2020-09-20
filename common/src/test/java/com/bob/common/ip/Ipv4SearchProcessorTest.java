package com.bob.common.ip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.bob.common.utils.ip.IpGeoMetaInfo;
import com.bob.common.utils.ip.v4.Ipv4SearchProcessor;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.util.StopWatch;

//import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

/**
 * @author wb-jjb318191
 * @create 2020-03-23 9:56
 */
public class Ipv4SearchProcessorTest {

    @Test
    public void invokeInThreads() throws Exception {
        Ipv4SearchProcessor finder = new Ipv4SearchProcessor();
        finder.init("C:\\Users\\wb-jjb318191\\Desktop\\ipv4-utf8-index.dat", new IpGeoMetaInfo());
        List<String> ips = FileUtils.readLines(new File("C:\\Users\\wb-jjb318191\\Desktop\\ipv4-ip.txt"), "UTF-8");

        CountDownLatch latch = new CountDownLatch(4);

        int size = ips.size();

        Thread thread1 = new Thread(() -> {
            int k = 0;
            for (int i = 0; i < 1000 * 1000 * 1000; i++) {
                finder.search(ips.get(k++));
                if (k == size) {
                    k = 0;
                }
            }
            latch.countDown();
        });
        Thread thread2 = new Thread(() -> {
            int k = 0;
            for (int i = 0; i < 1000 * 1000 * 1000; i++) {
                finder.search(ips.get(k++));
                if (k == size) {
                    k = 0;
                }
            }
            latch.countDown();
        });
        Thread thread3 = new Thread(() -> {
            int k = 0;
            for (int i = 0; i < 1000 * 1000 * 1000; i++) {
                finder.search(ips.get(k++));
                if (k == size) {
                    k = 0;
                }
            }
            latch.countDown();
        });
        Thread thread4 = new Thread(() -> {
            int k = 0;
            for (int i = 0; i < 1000 * 1000 * 1000; i++) {
                //String ip = ips.get(k++);
                finder.search(ips.get(k++));
                if (k == size) {
                    k = 0;
                }
            }
            latch.countDown();
        });
        StopWatch watch = new StopWatch();
        watch.start();
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        latch.await();

        watch.stop();
        System.out.println(watch.getLastTaskTimeMillis());
        //System.out.println(ip);
        //System.out.println(result);
        //System.gc();
        System.out.println(finder.search("96.84.242.159"));
        System.out.println(ObjectSizeCalculator.getObjectSize(finder));
    }

    @Test
    public void invokeInOneThread() throws Exception {
        Ipv4SearchProcessor finder = new Ipv4SearchProcessor();
        finder.init("C:\\Users\\wb-jjb318191\\Desktop\\ipv4-utf8-index.dat", new IpGeoMetaInfo());
        System.out.println(ObjectSizeCalculator.getObjectSize(finder) / (1024.0 * 1024.0));
        System.out.println(finder.search("171.152.40.15"));
        List<String> ips = FileUtils.readLines(new File("C:\\Users\\wb-jjb318191\\Desktop\\ipv4-ip.txt"), "UTF-8");
        System.gc();
        StopWatch watch = new StopWatch();
        watch.start();
        int k = 0;
        for (int i = 0; i < 1000 * 1000 * 100; i++) {
            finder.search(ips.get(k));
            k += 10;
            if (k >= ips.size()) {
                break;
            }
        }
        watch.stop();
        System.out.println("k=" + k);
        System.out.println(watch.getLastTaskTimeMillis());
    }

    @Test
    public void testWorstSearch() throws Exception {
        Ipv4SearchProcessor finder = new Ipv4SearchProcessor();
        finder.init("C:\\Users\\wb-jjb318191\\Desktop\\ipv4-utf8-index.dat", new IpGeoMetaInfo());
        List<String> ips = FileUtils.readLines(new File("C:\\Users\\wb-jjb318191\\Desktop\\worst-ipv4.txt"), "UTF-8");
        StopWatch watch = new StopWatch();
        int k = 0;
        watch.start();
        for (int i = 0; i < 1000 * 1000 * 100; i++) {
            finder.search("1.3.35.0");
        }
        watch.stop();
        System.out.println(watch.getLastTaskTimeMillis());
    }

    @Test
    public void testGenerateWorstIps() throws IOException {
        List<String> ips = new ArrayList<>(256 * 256);
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                if (j % 2 == 0) {
                    ips.add(i + "." + j + "." + "255.255");
                } else {
                    ips.add(i + "." + j + "." + "0.0");
                }
            }
        }
        FileUtils.writeLines(new File("C:\\Users\\wb-jjb318191\\Desktop\\worst-ipv4.txt"), ips);
    }

}