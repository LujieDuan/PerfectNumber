package io.lujieduan.perfectnumber;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class PerfectNumberMaster extends AbstractActor {

    // Message Classes ---------//
    static public class Start {
        public Start() {
        }
    }
    // ------------------------- //

    public static final int jobSize = 10_000;

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props(int countToFind, int countWorkers) {
        return Props.create(PerfectNumberMaster.class, () -> new PerfectNumberMaster(countToFind, countWorkers));
    }

    private final int countToFind;
    private int countFound;
    private int currentJob;
    private final int countWorkers;
    private final ActorRef[] workers;

    public PerfectNumberMaster(int countToFind, int countWorkers) {
        this.countToFind = countToFind;
        this.countWorkers = countWorkers;
        this.workers = new ActorRef[countWorkers];
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Start.class, msg -> {
                    log.info("System Started!");
                    for (int i = 0; i < countWorkers; i++) {
                        workers[i] = this.getContext().getSystem().actorOf(PerfectNumberWorker.props(i), "perfect-number-worker-" + i);
                    }
                    for (int i = 0; i < countWorkers; i++) {
                        workers[i].tell(new PerfectNumberWorker.CurrentJob(i*jobSize, (i+1)*jobSize), self());
                    }
                    currentJob = countWorkers;
                })
                .match(PerfectNumberWorker.FinishJob.class, msg -> {
                    getSender().tell(new PerfectNumberWorker.CurrentJob(currentJob*jobSize, (currentJob+1)*jobSize), self());
                    currentJob ++;
                })
                .match(PerfectNumberWorker.Found.class, msg -> {
                    log.info("Found {}: {}", this.countFound, msg.i);
                    this.countFound ++;
                    if (countFound >= countToFind) this.getContext().getSystem().terminate();
                })
                .build();
    }

}
