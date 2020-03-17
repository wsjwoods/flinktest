package org.datastream.trigger;

import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class CustomProcessingTimeTrigger extends Trigger<Object, TimeWindow> {
    private static final long serialVersionUID = 1L;

    private CustomProcessingTimeTrigger() {}

    private static int flag = 0;

    /**
     * 进入窗口的每个元素都会调用该方法。
     * @param element
     * @param timestamp
     * @param window
     * @param ctx
     * @return
     */
    @Override
    public TriggerResult onElement(Object element, long timestamp, TimeWindow window, TriggerContext ctx) {
        ctx.registerProcessingTimeTimer(window.maxTimestamp());
        // CONTINUE是代表不做输出，也即是，此时我们想要实现比如100条输出一次，
        // 而不是窗口结束再输出就可以在这里实现。
        if(flag > 9){
            flag = 0;
            return TriggerResult.FIRE;
        }else{
            flag++;
        }
        System.out.println("onElement : "+element);
        return TriggerResult.CONTINUE;
    }

    /**
     * 事件时间timer触发的时候被调用。
     * @param time
     * @param window
     * @param ctx
     * @return
     * @throws Exception
     */
    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    /**
     * 处理时间timer触发的时候会被调用
     * @param time
     * @param window
     * @param ctx
     * @return
     */
    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) {
        return TriggerResult.FIRE;
    }

    /**
     * 该方法主要是执行窗口的删除操作。
     * @param window
     * @param ctx
     * @throws Exception
     */
    @Override
    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
        ctx.deleteProcessingTimeTimer(window.maxTimestamp());
    }

    /**
     *
     * @return
     */
    @Override
    public boolean canMerge() {
        return true;
    }

    /**
     * 有状态的触发器相关，并在它们相应的窗口合并时合并两个触发器的状态，例如使用会话窗口。
     * @param window
     * @param ctx
     */
    @Override
    public void onMerge(TimeWindow window,
                        OnMergeContext ctx) {
        // only register a timer if the time is not yet past the end of the merged window
        // this is in line with the logic in onElement(). If the time is past the end of
        // the window onElement() will fire and setting a timer here would fire the window twice.
        long windowMaxTimestamp = window.maxTimestamp();
        if (windowMaxTimestamp > ctx.getCurrentProcessingTime()) {
            ctx.registerProcessingTimeTimer(windowMaxTimestamp);
        }
    }

    @Override
    public String toString() {
        return "ProcessingTimeTrigger()";
    }

    /**
     * Creates a new trigger that fires once system time passes the end of the window.
     */
    public static CustomProcessingTimeTrigger create() {
        return new CustomProcessingTimeTrigger();
    }

}