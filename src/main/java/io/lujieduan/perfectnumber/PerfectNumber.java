package io.lujieduan.perfectnumber;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class PerfectNumber {

    public static void main(String[] args) {

        // Read in command line arguments
        int countToFind = Integer.parseInt(args[0]);
        int countWorkers = Integer.parseInt(args[1]);

        final ActorSystem system = ActorSystem.create("perfect-number");
        final ActorRef master = system.actorOf(PerfectNumberMaster.props(countToFind, countWorkers), "perfect-number-master");

        master.tell(new PerfectNumberMaster.Start(), ActorRef.noSender());
    }
}
