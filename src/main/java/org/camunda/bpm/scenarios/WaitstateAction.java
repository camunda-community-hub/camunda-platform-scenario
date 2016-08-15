package org.camunda.bpm.scenarios;


/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface WaitstateAction<W extends Waitstate> {

  void execute(W waitstate);

}
