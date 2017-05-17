package inescid.opaf.framework;

import java.util.HashSet;

public class ThreadManager {
	HashSet<Thread> consumers=new HashSet<>();
	HashSet<Thread> producers=new HashSet<>();
	HashSet<Thread> independents=new HashSet<>();
	
	public void addConsumers(Thread... threads) {
		for(Thread t: threads)
			consumers.add(t);
	}
	
	public void removeConsumers(Thread... threads) {
		for(Thread t: threads)
			consumers.remove(t);
	}
	
	public void addProducers(Thread... threads) {
		for(Thread t: threads)
			producers.add(t);
	}
	public void removeProducers(Thread... threads) {
		for(Thread t: threads)
			producers.remove(t);
	}
	
	public void addIndependents(Thread... threads) {
		for(Thread t: threads)
			independents.add(t);
	}
	
	public void removeIndependents(Thread... threads) {
		for(Thread t: threads)
			independents.remove(t);
	}
	
	public void waitForFinish(boolean forceFinishOnDeadlock) {		
		boolean running=true;
		while(running) {
			running=false;
			for(Thread t: producers)
				if(t.isAlive()) {
					running=true;
					break;
				}
			if(!running)
				for(Thread t: consumers)
					if(t.isAlive()) {
						if(forceFinishOnDeadlock) 
							t.interrupt();
						running=true;
					}
			if(!running)
				for(Thread t: independents)
					if(t.isAlive()) {
						running=true;
						break;
					}
		}
	}
	
	public boolean isDeadlocked() {
		for(Thread t: producers)
			if(t.isAlive())
				return false;
		for(Thread t: consumers)
			if(t.isAlive())
				return true;
		return false;
	}
	
	public String printState() {
		StringBuilder sb=new StringBuilder();
		sb.append("Producers:\n");
		for(Thread t: producers)
			sb.append(" - ").append(t.getState().name()).append("\n");
		sb.append("Consumers:\n");
		for(Thread t: consumers)
			sb.append(" - ").append(t.getState().name()).append("\n");
		sb.append("Independents:\n");
		for(Thread t: independents)
			sb.append(" - ").append(t.getState().name()).append("\n");
		return sb.toString();
	}
}
