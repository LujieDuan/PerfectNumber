package io.lujieduan.perfectnumber;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class PerfectNumberWorker extends AbstractActor {

    // Message Classes ---------//
    static public class CurrentJob {
        public final int start;
        public final int end;
        public CurrentJob(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
    static public class FinishJob {
        public FinishJob() {
        }
    }
    static public class Found {
        public final int i;
        public Found(int i) {
            this.i = i;
        }
    }
    // ------------------------- //

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props(int workerId) {
        return Props.create(PerfectNumberWorker.class, () -> new PerfectNumberWorker(workerId));
    }

    private final int workerId;
    private int current;

    public PerfectNumberWorker(int workerId) {
        this.workerId = workerId;
    }

    @Override
    public void preStart() {
        log.info("Perfect number worker actor ID {} created!", workerId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(CurrentJob.class, msg -> {
            log.info("Perfect number worker actor ID {} started to check [{}, {})", workerId, msg.start, msg.end);
            for (int i = msg.start; i < msg.end; i++) {
                if (isPerfectNumber(i))
                    getSender().tell(new Found(i), self());
            }
        }).build();
    }


    private boolean isPerfectNumber(int i) {
        int sum = 0;
        for (int factor = 1; factor < i; factor ++) {
            if ((i % factor) == 0) sum += factor;
        }
        return sum == i;
    }

}
