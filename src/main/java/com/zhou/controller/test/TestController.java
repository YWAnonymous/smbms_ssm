package com.zhou.controller.test;

import com.zhou.util.RedisUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    private final String lock_key ="lock";

    @Test
    public void testRedisLock2() throws InterruptedException {
        long start = System.currentTimeMillis();
        List list =new ArrayList();
        for (int i = 1; i <=2 ; i++) {
            list.add(i);
        }
        //Executors创建线程池new固定的10个线程
        for (int j = 0; j <1000; j++) {
            ExecutorService taskExecutor  = Executors.newCachedThreadPool();
            final CountDownLatch latch = new CountDownLatch(list.size());//用于判断所有的线程是否结束
            System.out.println("个数=="+list.size());
            for (int m = 0; m < list.size(); m++) {
                final int n = m;//内部类里m不能直接用,所以赋值给n
                Runnable run = new Runnable() {
                    public void run() {
                        try {
                            System.out.println("我在执行==count="+n);
                            redisUtil.addRedisLock(lock_key,"zhao_001status"+n,120000);
                        }finally {
                            redisUtil.delRedisLock(lock_key);
                        }
                        latch.countDown();
                    }
                };
                taskExecutor.execute(run);//开启线程执行池中的任务。还有一个方法submit也可以做到，它的功能是提交指定的任务去执行并且返回Future对象，即执行的结果
            }
            try {
                //等待所有线程执行完毕
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            taskExecutor.shutdown();//关闭线程池
            //所有线程执行完毕,执行主线程
        }
        long end = System.currentTimeMillis();
        System.out.println("执行线程数:{}"+list.size()+",总耗时:{}"+(end-start));
    }
}
