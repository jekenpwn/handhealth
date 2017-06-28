package jeken.com.handhealth.entity.net;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017-06-25.
 */

public class NetPool {
    private static NetPool pool = null;
    private ExecutorService executorService;
    private NetPool(){
        // give 3/4 availableProcessors to finish task by queue
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*3/4);

    }
    //single ThreadPool
    public static NetPool getInstance(){
        if (pool==null){
            synchronized (NetPool.class){
                if (pool==null)
                    pool = new NetPool();
            }
        }
        return pool;
    }

    public boolean addTask(Runnable runnable){
        if (executorService!=null){
            executorService.submit(runnable);
            return true;
        }

        return false;
    }


}
