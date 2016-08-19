import {Injectable} from "@angular/core";
import {BroadcastEmmitterService} from "./broadcast-emitter.servie";
import {AlertMessage} from "../../domains/alert/alert-message";

/**
 * Delegate helper service
 */
@Injectable()
export class DelegateService {

  constructor(private broadcastEmitter: BroadcastEmmitterService) {
    console.log(broadcastEmitter.emit);
  }

  /**
   * Emit some type of alert
   * @param alert - alert message
   */
  emitAlert(alert: AlertMessage): void {
    this.broadcastEmitter.emit(alert, 'alertComponent');
  }

}
